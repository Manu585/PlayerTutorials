package org.bendersdestiny.playertutorials.items;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Getter
public abstract class BaseItem {
	public final String name;
	public final List<Component> lore;
	public final Material material;
	public final ItemStack item;

	public static List<BaseItem> allItems = new ArrayList<>();

	public BaseItem(String name, List<Component> lore, Material material) {
		this.name = name;
		this.lore = lore;
		this.material = material;
		this.item = new ItemStack(material);

		this.createItem();

		allItems.add(this);
	}

	/**
	 * Creates the item and
	 * set its {@link ItemMeta}
	 */
	private void createItem() {
		ItemMeta meta = this.item.getItemMeta();

		if (meta == null) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cItemMeta Null!")
			);

		} else { // ItemMeta not null
			meta.displayName(ChatUtil.translate(this.name));
			meta.lore(this.lore);
			this.item.setItemMeta(meta);
		}
	}
}
