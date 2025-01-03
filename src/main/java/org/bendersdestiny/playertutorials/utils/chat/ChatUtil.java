package org.bendersdestiny.playertutorials.utils.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link ChatUtil} class has the purpose to help with
 * some basic formating issues. You are able to colorize
 * messages with the {@link #translate(String msg)} format}
 * method and do much more with the remaining methods.
 */
public class ChatUtil {
	private final PlayerTutorials plugin;
	private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");

	public ChatUtil(PlayerTutorials plugin) {
		this.plugin = plugin;
	}

	/**
	 * Format a {@link String} to be able to use
	 * HEX colors and normal Minecraft {@link ChatColor}
	 *
	 * @param msg Message to format
	 * @return formatted message
	 */
	@Deprecated
	public static @NotNull String format(String msg) {
		Matcher hexMatcher = HEX_PATTERN.matcher(msg);
		while (hexMatcher.find()) {
			String color = hexMatcher.group(2);
			String hexColor = ChatColor.of("#" + color).toString();
			msg = msg.replace(hexMatcher.group(0), hexColor);
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Converts a string containing both legacy `&` codes and `&#rrggbb` hex codes
	 * into a single Adventure {@link Component}.
	 *
	 * @param input the string to parse
	 * @return a Component with the correct colors applied
	 */
	public static Component translate(String input) {
		if (input == null || input.isEmpty()) {
			return Component.empty();
		}

		String replacedHex = replaceHexCodes(input);

		replacedHex = replacedHex.replace('&', '§');

		Component component = LegacyComponentSerializer.legacySection().deserialize(replacedHex);

		component = component.decoration(TextDecoration.ITALIC, false);

		return component;
	}

	/**
	 * Replaces `&#RRGGBB` occurrences with the legacy `§x§R§R§G§G§B§B` format.
	 */
	private static String replaceHexCodes(String input) {
		Matcher matcher = HEX_PATTERN.matcher(input);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String hex = matcher.group(1);
			StringBuilder replacement = new StringBuilder("§x");
			for (char c : hex.toCharArray()) {
				replacement.append('§').append(c);
			}
			// Replace the entire `&#xxxxxx` match with the built string
			matcher.appendReplacement(sb, replacement.toString());
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	/**
	 * Message for the server start
	 */
	public void sendServerStartupMessage() {
		String startupMessage = """
		\n
		&6██████╗ ████████╗
		&6██╔══██╗╚══██╔══╝
		&6██████╔╝   ██║    &bPlayerTutorials
		&6██╔═══╝    ██║    &av%s
		&6██║        ██║
		&6╚═╝        ╚═╝
		""";

		String formattedMessage = LegacyComponentSerializer.legacySection().serialize(translate(String.format
				(startupMessage, this.plugin.getPluginMeta().getVersion())));
		this.plugin.getServer().getConsoleSender().sendMessage(formattedMessage);
	}

	/**
	 * Message for the server stop
	 */
	public void sendServerStopMessage() {
		String startupMessage = """
		\n
		&6██████╗ ████████╗
		&6██╔══██╗╚══██╔══╝
		&6██████╔╝   ██║    &bPlayerTutorials
		&6██╔═══╝    ██║    &av%s
		&6██║        ██║
		&6╚═╝        ╚═╝
		""";

		String formattedMessage = LegacyComponentSerializer.legacySection().serialize(translate(String.format
				(startupMessage, this.plugin.getPluginMeta().getVersion())));
		this.plugin.getServer().getConsoleSender().sendMessage(formattedMessage);
	}
}
