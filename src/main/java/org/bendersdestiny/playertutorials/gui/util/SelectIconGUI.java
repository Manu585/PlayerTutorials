package org.bendersdestiny.playertutorials.gui.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SelectIconGUI {
	@Getter
	private final ChestGui gui;
	private final PaginatedPane paginatedPane;
	private final List<StaticPane> iconPages = new ArrayList<>();
	private final CreateTutorialGUI parentGUI;

	final int maxIconsPerPage = 27;

	public SelectIconGUI(CreateTutorialGUI parentGUI) {
		this.parentGUI = parentGUI;

		this.gui = new ChestGui(4, ChatUtil.translateString("Icons"), PlayerTutorials.getInstance());
		this.paginatedPane = new PaginatedPane(0,0,9, 4);

		this.setupUI();
		this.gui.setOnGlobalClick(click -> click.setCancelled(true));
	}

	void setupUI() {
		this.gui.addPane(this.paginatedPane);
		createIconPages();

		int i = 0;

		for (StaticPane iconPage : this.iconPages) {
			this.paginatedPane.addPane(i, iconPage);
			iconPage.setVisible(true);
			i++;
		}

		this.paginatedPane.setOnClick(event -> {
			if (event.getRawSlot() < 27) {
				ItemStack clickedItem = event.getCurrentItem();
				if (clickedItem != null && clickedItem.getType() != Material.AIR) {
					Player p = (Player) event.getWhoClicked();
					p.closeInventory();

					parentGUI.setTutorialIcon(clickedItem.getType());
					parentGUI.updateGUI();
					parentGUI.getGui().show(p);
					p.sendMessage(ChatUtil.translate("&aTutorial icon updated to " + clickedItem.getType()));
				}
			}
		});
	}

	private void createIconPages() {
		StaticPane currentPage = new StaticPane(0, 0, 9, 4);
		int currentIndex = 0;

		for (Material material : Material.values()) {
			if (material == Material.AIR) {
				continue;
			}

			try {
				currentPage.addItem(new GuiItem(new ItemStack(material)), Slot.fromIndex(currentIndex));
				currentIndex++;

				if (currentIndex >= maxIconsPerPage) {
					addNavigationButtons(currentPage);
					this.iconPages.add(currentPage);

					currentPage = new StaticPane(0, 0, 9, 4);
					currentIndex = 0;
				}
			} catch (IllegalArgumentException ignored) {}
		}

		// Add any remaining items on the last page
		if (currentIndex > 0) {
			addNavigationButtons(currentPage);
			this.iconPages.add(currentPage);
		}
	}

	private void addNavigationButtons(StaticPane page) {
		GuiItem pane = new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
		for (int i = 27; i <= 35; i++) {
			page.addItem(pane, Slot.fromIndex(i));
		}
		page.addItem(getNextPageItem(), Slot.fromIndex(32));
		page.addItem(getPreviousPageItem(), Slot.fromIndex(30));
	}

	private GuiItem getNextPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();

		assert meta != null;

		meta.displayName(Component.text("Next page", TextColor.color(245, 182, 66)));

		item.setItemMeta(meta);

		return new GuiItem(item, event -> {
			int currentPage = this.paginatedPane.getPage();
			if (currentPage < this.paginatedPane.getPages() - 1) {  // Prevent going past the last page
				this.paginatedPane.setPage(currentPage + 1);
				this.gui.update();
			}
		});
	}

	private GuiItem getPreviousPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();

		assert meta != null;

		meta.displayName(Component.text("Previous page", TextColor.color(245, 182, 66)));

		item.setItemMeta(meta);

		return new GuiItem(item, event -> {
			int currentPage = this.paginatedPane.getPage();
			if (currentPage > 0) {  // Prevent going before the first page
				this.paginatedPane.setPage(currentPage - 1);
				this.gui.update();
			}
		});
	}
}
