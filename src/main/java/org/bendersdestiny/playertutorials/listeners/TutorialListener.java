package org.bendersdestiny.playertutorials.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.manager.ItemManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.bendersdestiny.playertutorials.utils.chat.ChatUtil.parseTutorialColor;

public class TutorialListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		TutorialPlayer.registerPlayer(new TutorialPlayer(p.getUniqueId()));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void areaSelectorInteract(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) return;

		Player p = e.getPlayer();
		Action action = e.getAction();

		if (e.getClickedBlock() == null) return;
		ItemStack inHand = p.getInventory().getItemInMainHand();
		ItemManager itemManager = PlayerTutorials.getInstance().getItemManager();

		if (!isAreaSelector(inHand, itemManager)) return;

		TutorialPlayer tutorialPlayer = TutorialPlayer.getPlayer(p.getUniqueId());
		if (tutorialPlayer == null) return;

		e.setCancelled(true);

		Location blockLocation = e.getClickedBlock().getLocation();

		if (action == Action.LEFT_CLICK_BLOCK) {
			tutorialPlayer.setPos1(blockLocation);
			p.sendMessage(ChatUtil.translate("&#f0c435Pos1 &#828282set to &#f0c435" + locToString(blockLocation)));
			e.setCancelled(true);
		}

		else if (action == Action.RIGHT_CLICK_BLOCK) {
			tutorialPlayer.setPos2(blockLocation);
			p.sendMessage(ChatUtil.translate("&#f0c435Pos2 &#828282set to &#f0c435" + locToString(blockLocation)));
			e.setCancelled(true);
		}

		if (tutorialPlayer.getPos1() != null && tutorialPlayer.getPos2() != null && tutorialPlayer.getParticleTaskId() == -1) {
			Color color = parseTutorialColor(tutorialPlayer.getEditingTutorial().getName());

			int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
					PlayerTutorials.getInstance(),
					() -> drawParticleOutline(p, tutorialPlayer.getPos1(), tutorialPlayer.getPos2(), color),
					0L, 10L
			);
			tutorialPlayer.setParticleTaskId(taskId);

			sendAcceptMessage(p);

		}
	}

	/**
	 * Simple helper to check if the given ItemStack matches the plugin’s area selector.
	 */
	private boolean isAreaSelector(ItemStack stack, ItemManager itemManager) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		return stack.isSimilar(itemManager.getAreaSelector().getItem());
	}

	/**
	 * Simple utility to convert a Location to a readable string.
	 */
	private @NotNull String locToString(@NotNull Location loc) {
		return String.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * Draw a particle outline around the cuboid defined by pos1 and pos2.
	 */
	private void drawParticleOutline(@NotNull Player p, @NotNull Location pos1, @NotNull Location pos2, Color color) {
		World world = p.getWorld();

		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

		Particle.DustOptions dust = new Particle.DustOptions(color, 1.8f);

		for (int x = minX; x <= maxX; x++) {
			spawnParticle(world, x, minY, minZ, dust);
			spawnParticle(world, x, minY, maxZ, dust);
			spawnParticle(world, x, maxY, minZ, dust);
			spawnParticle(world, x, maxY, maxZ, dust);
		}

		for (int y = minY; y <= maxY; y++) {
			spawnParticle(world, minX, y, minZ, dust);
			spawnParticle(world, minX, y, maxZ, dust);
			spawnParticle(world, maxX, y, minZ, dust);
			spawnParticle(world, maxX, y, maxZ, dust);
		}

		for (int z = minZ; z <= maxZ; z++) {
			spawnParticle(world, minX, minY, z, dust);
			spawnParticle(world, minX, maxY, z, dust);
			spawnParticle(world, maxX, minY, z, dust);
			spawnParticle(world, maxX, maxY, z, dust);
		}
	}

	/**
	 * Helper method to spawn a single particle at the given block coords.
	 */
	private void spawnParticle(@NotNull World world, int x, int y, int z, Particle.DustOptions dust) {
		world.spawnParticle(Particle.DUST,
				x + 0.5, y + 0.5, z + 0.5,
				1,
				0, 0, 0,
				0,
				dust
		);
	}

	private void sendAcceptMessage(@NotNull Player p) {
		Component part1 = ChatUtil.translate("&#828282Do you want to ");

		Component accept = ChatUtil.translate("&#4cc734&naccept")
				.clickEvent(ClickEvent.runCommand("/tutorial areaaccept"))
				.hoverEvent(HoverEvent.showText(ChatUtil.translate("&#f0c435&nClick&#828282 to confirm!")));

		Component part3 = ChatUtil.translate("&r &#828282or type &c/cancel");

		Component finalMessage = part1.append(accept).append(part3);
		p.sendMessage(finalMessage);
	}
}
