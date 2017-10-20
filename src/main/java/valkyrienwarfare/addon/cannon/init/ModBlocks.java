/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2016-2017 the Valkyrien Warfare team
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it.
 * Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income unless it is to be used as a part of a larger project (IE: "modpacks"), nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from the Valkyrien Warfare team.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: The Valkyrien Warfare team), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package valkyrienwarfare.addon.cannon.init;

import net.minecraftforge.event.RegistryEvent;
import valkyrienwarfare.addon.cannon.blocks.BlockAirMine;
import valkyrienwarfare.addon.cannon.blocks.BlockCannon;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

	public static Block cannon;
	public static Block airmine;


	public static void init() {
		cannon = new BlockCannon();
		airmine = new BlockAirMine();

	}

	public static void register(RegistryEvent.Register<Block> event) {
		registerBlock(cannon, event);
		registerBlock(airmine, event);
	}
	
	public static void registerItemBlocks(RegistryEvent.Register<Item> event)   {
		event.getRegistry().registerAll(new ItemBlock(cannon).setUnlocalizedName(cannon.getUnlocalizedName()).setRegistryName(cannon.getRegistryName())
				, new ItemBlock(airmine).setUnlocalizedName(airmine.getUnlocalizedName()).setRegistryName(airmine.getRegistryName()));
	}

	public static void registerBlock(Block block, RegistryEvent.Register<Block> event) {
		event.getRegistry().register(block);
	}

	public static void registerRenders() {
		registerRender(cannon);
		registerRender(airmine);

	}

	private static void registerRender(Block block) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}

}
