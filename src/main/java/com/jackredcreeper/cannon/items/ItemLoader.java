package com.jackredcreeper.cannon.items;

import com.jackredcreeper.cannon.CannonModReference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ItemLoader extends Item {

	public ItemLoader() {

		setUnlocalizedName(CannonModReference.ModItems.LOADER.getUnlocalizedName());
		setRegistryName(CannonModReference.ModItems.LOADER.getRegistryName());

		this.setCreativeTab(CreativeTabs.COMBAT);
		this.maxStackSize = 1;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List itemInformation, boolean par4) {
		itemInformation.add(TextFormatting.BLUE + "Loads your cannon");
	}

}
