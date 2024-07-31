package org.bendersdestiny.playertutorials.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AreaListener implements Listener {

	private Location pointA = null;
	private Location pointB = null;

	@EventHandler
	public void onRightClick(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		ItemStack heldItem = player.getInventory().getItemInMainHand();

		if (heldItem.getType().equals(Material.WOODEN_AXE)) {
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				Location clickedBlockLocation = Objects.requireNonNull(event.getClickedBlock()).getLocation();

				if (pointA == null) {
					pointA = clickedBlockLocation;
					player.sendMessage("Point A set at: " + pointA);
				} else if (pointB == null) {
					pointB = clickedBlockLocation;
					player.sendMessage("Point B set at: " + pointB);


					//PlayerTutorials.getInstance().getStorage().registerArea(new Area("Test", pointA, pointB, pointA));

					pointA = null;
					pointB = null;
				}
			}
		}
	}
}
