package org.bendersdestiny.playertutorials.utils.chat;

import net.md_5.bungee.api.ChatColor;
import org.bendersdestiny.playertutorials.PlayerTutorials;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link ChatUtil} class has the purpose to help with
 * some basic formating issues. You are able to colorize
 * messages with the {@link #format(String msg) format}
 * method and do much more with the remaining methods.
 */
public class ChatUtil {
	private final PlayerTutorials plugin;
	private static final Pattern hexPattern = Pattern.compile("(&#)([a-fA-F0-9]{6})");

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
	public static String format(String msg) {
		Matcher hexMatcher = hexPattern.matcher(msg);
		while (hexMatcher.find()) {
			String color = hexMatcher.group(2);
			String hexColor = ChatColor.of("#" + color).toString();
			msg = msg.replace(hexMatcher.group(0), hexColor);
		}
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
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

		String formattedMessage = format(String.format(startupMessage, this.plugin.getDescription().getVersion()));
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

		String formattedMessage = format(String.format(startupMessage, this.plugin.getDescription().getVersion()));
		this.plugin.getServer().getConsoleSender().sendMessage(formattedMessage);
	}
}
