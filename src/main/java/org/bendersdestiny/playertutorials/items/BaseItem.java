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
	public String name;
	public List<String> lore;
	public Material material;
	public ItemStack item;

	public BaseItem(String name, List<String> lore, Material material) {
		this.name = name;
		this.lore = lore;
		this.material = material;

		this.createItem();
	}

	private void createItem() {
		item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(name);
			meta.setLore(lore);
			item.setItemMeta(meta);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"NULL Itemmeta!");
			throw new NullPointerException();
		}
	}
}
