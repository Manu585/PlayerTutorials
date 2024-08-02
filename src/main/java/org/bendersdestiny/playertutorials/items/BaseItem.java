package org.bendersdestiny.playertutorials.items;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

@Getter
public abstract class BaseItem {
	public final String name;
	public final List<String> lore;
	public final Material material;
	public final ItemStack item;

	public BaseItem(String name, List<String> lore, Material material) {
		this.name = name;
		this.lore = lore;
		this.material = material;

		this.item = new ItemStack(material);

		this.createItem();
	}

	private void createItem() {
		ItemMeta meta = this.item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(this.name);
			meta.setLore(this.lore);
			this.item.setItemMeta(meta);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"NULL Itemmeta!");
			throw new NullPointerException();
		}
	}
}
