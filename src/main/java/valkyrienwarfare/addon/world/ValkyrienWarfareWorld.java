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

package valkyrienwarfare.addon.world;

import net.minecraftforge.event.RegistryEvent;
import valkyrienwarfare.addon.world.block.BlockEtheriumOre;
import valkyrienwarfare.addon.world.block.BlockQuartzFence;
import valkyrienwarfare.addon.world.proxy.ClientProxyWorld;
import valkyrienwarfare.addon.world.tileentity.TileEntitySkyTempleController;
import valkyrienwarfare.api.addons.Module;
import valkyrienwarfare.ValkyrienWarfareMod;
import valkyrienwarfare.addon.world.block.BlockSkyTempleController;
import valkyrienwarfare.addon.world.proxy.CommonProxyWorld;
import valkyrienwarfare.addon.world.worldgen.ValkyrienWarfareWorldGen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import valkyrienwarfare.api.addons.VWAddon;

@VWAddon
public class ValkyrienWarfareWorld extends Module<ValkyrienWarfareWorld> {
	
	public ValkyrienWarfareWorld()   {
		super("VW_World", new CommonProxyWorld(), new ClientProxyWorld(), null);
		INSTANCE = this;
	}
	private static final WorldEventsCommon worldEventsCommon = new WorldEventsCommon();
	public static ValkyrienWarfareWorld INSTANCE;
	public Block etheriumOre;
	public Block skydungeon_controller;
	public Block quartz_fence;
	public Item etheriumCrystal;

	private static void registerItemBlock(Block block, RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(block).setUnlocalizedName(block.getUnlocalizedName()).setRegistryName(block.getRegistryName()));
	}

	@Override
	public void preInit(FMLStateEvent event) {
		etheriumOre = new BlockEtheriumOre(Material.ROCK).setHardness(3f).setUnlocalizedName("etheriumore").setRegistryName(getModID(), "etheriumore").setCreativeTab(ValkyrienWarfareMod.vwTab);
		skydungeon_controller = new BlockSkyTempleController(Material.GLASS).setHardness(15f).setUnlocalizedName("skydungeon_controller").setRegistryName(getModID(), "skydungeon_controller").setCreativeTab(ValkyrienWarfareMod.vwTab);
		quartz_fence = new BlockQuartzFence(Material.GLASS).setHardness(8f).setUnlocalizedName("quartz_fence").setRegistryName(getModID(), "quartz_fence").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		etheriumCrystal = new ItemEtheriumCrystal().setUnlocalizedName("etheriumcrystal").setRegistryName(getModID(), "etheriumcrystal").setCreativeTab(ValkyrienWarfareMod.vwTab).setMaxStackSize(16);
	}

	@Override
	public void init(FMLStateEvent event) {
		EntityRegistry.registerModEntity(new ResourceLocation(ValkyrienWarfareMod.MODID, "FallingUpBlockEntity"), EntityFallingUpBlock.class, "FallingUpBlockEntity", 75, ValkyrienWarfareMod.INSTANCE, 80, 1, true);
		MinecraftForge.EVENT_BUS.register(worldEventsCommon);

		GameRegistry.registerWorldGenerator(new ValkyrienWarfareWorldGen(), 1);
	}

	@Override
	public void postInit(FMLStateEvent event) {
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(etheriumOre, skydungeon_controller, quartz_fence);
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(etheriumCrystal);
		
		registerItemBlock(etheriumOre, event);
		registerItemBlock(skydungeon_controller, event);
		registerItemBlock(quartz_fence, event);
	}
	
	@Override
	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntitySkyTempleController.class, "skydungeon_controller");
	}
}