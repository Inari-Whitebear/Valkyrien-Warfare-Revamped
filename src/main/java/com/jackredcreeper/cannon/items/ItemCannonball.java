package com.jackredcreeper.cannon.items;

import com.jackredcreeper.cannon.CannonModReference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ItemCannonball extends Item {

	public ItemCannonball() {

		setUnlocalizedName(CannonModReference.ModItems.CANNONBALL.getUnlocalizedName());
		setRegistryName(CannonModReference.ModItems.CANNONBALL.getRegistryName());

		this.setCreativeTab(CreativeTabs.COMBAT);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List itemInformation, boolean par4) {
		itemInformation.add(TextFormatting.BLUE + "Standard Shot");
	}

}
