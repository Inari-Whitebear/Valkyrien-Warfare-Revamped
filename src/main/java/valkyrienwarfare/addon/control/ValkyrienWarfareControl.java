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

package valkyrienwarfare.addon.control;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import valkyrienwarfare.ValkyrienWarfareMod;
import valkyrienwarfare.addon.control.block.*;
import valkyrienwarfare.addon.control.block.engine.BlockNormalEngine;
import valkyrienwarfare.addon.control.block.engine.BlockRedstoneEngine;
import valkyrienwarfare.addon.control.block.ethercompressor.BlockCreativeEtherCompressor;
import valkyrienwarfare.addon.control.block.ethercompressor.BlockNormalEtherCompressor;
import valkyrienwarfare.addon.control.capability.ICapabilityLastRelay;
import valkyrienwarfare.addon.control.capability.ImplCapabilityLastRelay;
import valkyrienwarfare.addon.control.capability.StorageLastRelay;
import valkyrienwarfare.addon.control.gui.ControlGUIHandler;
import valkyrienwarfare.addon.control.item.ItemRelayWire;
import valkyrienwarfare.addon.control.item.ItemShipStealer;
import valkyrienwarfare.addon.control.item.ItemSystemLinker;
import valkyrienwarfare.addon.control.network.*;
import valkyrienwarfare.addon.control.piloting.PilotControlsMessage;
import valkyrienwarfare.addon.control.piloting.PilotControlsMessageHandler;
import valkyrienwarfare.addon.control.proxy.ClientProxyControl;
import valkyrienwarfare.addon.control.proxy.CommonProxyControl;
import valkyrienwarfare.addon.control.tileentity.*;
import valkyrienwarfare.addon.world.ValkyrienWarfareWorld;
import valkyrienwarfare.api.addons.Module;
import valkyrienwarfare.api.addons.VWAddon;

import java.io.File;

@VWAddon
public class ValkyrienWarfareControl extends Module<ValkyrienWarfareControl> {
	
	@CapabilityInject(ICapabilityLastRelay.class)
	public static final Capability<ICapabilityLastRelay> lastRelayCapability = null;
	public static Configuration config;
	public static ValkyrienWarfareControl INSTANCE;
	public static SimpleNetworkWrapper controlNetwork;
	public Block basicEngine;
	public Block advancedEngine;
	public Block eliteEngine;
	public Block ultimateEngine; // Couldn't think of what to name these, so I went with the Mekanism naming style
	public Block redstoneEngine;
	public Block basicHoverController;
	public Block dopedEtherium;
	public Block balloonBurner;
	public Block pilotsChair;
	public Block passengerChair;
	public Block shipHelm;
	public Block shipWheel;
	public Block shipTelegraph;
	public Block antigravityEngine; //leaving it with the old name to prevent blocks dissapearing
	public Block advancedEtherCompressor;
	public Block eliteEtherCompressor;
	public Block ultimateEtherCompressor;
	public Block creativeEtherCompressor;
	public Block thrustRelay;
	public Block thrustModulator;
	public Block airshipController_zepplin;
	public Block shipHullSealer;
	public Block gyroscope;
	public Item systemLinker;
	public Item airshipStealer;
	public Item relayWire;
	public ValkyrienWarfareControl() {
		super("VW_Control", new ClientProxyControl(), new CommonProxyControl(), null);
		INSTANCE = this;
	}
	
	private static void registerItemBlocks(RegistryEvent.Register<Item> event, Block... blocks) {
		for (Block block : blocks) {
			event.getRegistry().register(new ItemBlock(block).setUnlocalizedName(block.getUnlocalizedName()).setRegistryName(block.getRegistryName()));
		}
	}
	
	@Override
	public void preInit(FMLStateEvent event) {
		config = new Configuration(new File(ValkyrienWarfareMod.getWorkingFolder() + "/config/valkyrienwarfarecontrol.cfg"));
		config.load();
		config.save();
		systemLinker = new ItemSystemLinker().setUnlocalizedName("systemlinker").setRegistryName(getModID(), "systemlinker").setCreativeTab(ValkyrienWarfareMod.vwTab).setMaxStackSize(1);
		
		airshipStealer = new ItemShipStealer().setUnlocalizedName("airshipStealer").setRegistryName(getModID(), "airshipStealer").setCreativeTab(ValkyrienWarfareMod.vwTab).setMaxStackSize(1);
		relayWire = new ItemRelayWire().setUnlocalizedName("relaywire").setRegistryName(getModID(), "relaywire").setCreativeTab(ValkyrienWarfareMod.vwTab).setMaxStackSize(1);
		
		double basicEnginePower = config.get(Configuration.CATEGORY_GENERAL, "basicEnginePower", 4000D, "engine power for the basic engine").getDouble();
		double advancedEnginePower = config.get(Configuration.CATEGORY_GENERAL, "advancedEnginePower", 6000D, "engine power for the advanced engine").getDouble();
		double eliteEnginePower = config.get(Configuration.CATEGORY_GENERAL, "eliteEnginePower", 8000D, "engine power for the elite engine").getDouble();
		double ultimateEnginePower = config.get(Configuration.CATEGORY_GENERAL, "ultimateEnginePower", 16000D, "engine power for the ultimate engine").getDouble();
		double redstoneEnginePower = config.get(Configuration.CATEGORY_GENERAL, "redstoneEnginePower", 500D, "Multiplied by the redstone power (0-15) to the Redstone engine").getDouble();
		
		double basicEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "basicEtherCompressorPower", 25000D, "engine power for the basic Ether Compressor").getDouble();
		double advancedEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "advancedEtherCompressorPower", 45000D, "engine power for the advanced Ether Compressor").getDouble();
		double eliteEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "eliteEtherCompressorPower", 80000D, "engine power for the elite Ether Compressor").getDouble();
		double ultimateEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "ultimateEtherCompressorPower", 100000D, "engine power for the ultimate Ether Compressor").getDouble();
		
		basicEngine = new BlockNormalEngine(Material.WOOD, basicEnginePower).setHardness(5f).setUnlocalizedName("basicengine").setRegistryName(getModID(), "basicengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		advancedEngine = new BlockNormalEngine(Material.ROCK, advancedEnginePower).setHardness(6f).setUnlocalizedName("advancedengine").setRegistryName(getModID(), "advancedengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		eliteEngine = new BlockNormalEngine(Material.IRON, eliteEnginePower).setHardness(8f).setUnlocalizedName("eliteengine").setRegistryName(getModID(), "eliteengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		ultimateEngine = new BlockNormalEngine(Material.GROUND, ultimateEnginePower).setHardness(10f).setUnlocalizedName("ultimateengine").setRegistryName(getModID(), "ultimateengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		redstoneEngine = new BlockRedstoneEngine(Material.REDSTONE_LIGHT, redstoneEnginePower).setHardness(7.0f).setUnlocalizedName("redstoneengine").setRegistryName(getModID(), "redstoneengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		antigravityEngine = new BlockNormalEtherCompressor(Material.WOOD, basicEtherCompressorPower).setHardness(8f).setUnlocalizedName("antigravengine").setRegistryName(getModID(), "antigravengine").setCreativeTab(ValkyrienWarfareMod.vwTab);
		advancedEtherCompressor = new BlockNormalEtherCompressor(Material.ROCK, advancedEtherCompressorPower).setHardness(8f).setUnlocalizedName("advancedethercompressor").setRegistryName(getModID(), "advancedethercompressor").setCreativeTab(ValkyrienWarfareMod.vwTab);
		eliteEtherCompressor = new BlockNormalEtherCompressor(Material.IRON, eliteEtherCompressorPower).setHardness(8f).setUnlocalizedName("eliteethercompressor").setRegistryName(getModID(), "eliteethercompressor").setCreativeTab(ValkyrienWarfareMod.vwTab);
		ultimateEtherCompressor = new BlockNormalEtherCompressor(Material.GROUND, ultimateEtherCompressorPower).setHardness(8f).setUnlocalizedName("ultimateethercompressor").setRegistryName(getModID(), "ultimateethercompressor").setCreativeTab(ValkyrienWarfareMod.vwTab);
		creativeEtherCompressor = new BlockCreativeEtherCompressor(Material.BARRIER, Double.MAX_VALUE / 4).setHardness(0.0f).setUnlocalizedName("creativeethercompressor").setRegistryName(getModID(), "creativeethercompressor").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		basicHoverController = new BlockHovercraftController(Material.IRON).setHardness(10f).setUnlocalizedName("basichovercraftcontroller").setRegistryName(getModID(), "basichovercraftcontroller").setCreativeTab(ValkyrienWarfareMod.vwTab);
		dopedEtherium = new BlockDopedEtherium(Material.GLASS).setHardness(4f).setUnlocalizedName("dopedetherium").setRegistryName(getModID(), "dopedetherium").setCreativeTab(ValkyrienWarfareMod.vwTab);
		balloonBurner = new BlockBalloonBurner(Material.IRON).setHardness(4f).setUnlocalizedName("balloonburner").setRegistryName(getModID(), "balloonburner").setCreativeTab(ValkyrienWarfareMod.vwTab);
		pilotsChair = new BlockShipPilotsChair(Material.IRON).setHardness(4f).setUnlocalizedName("shippilotschair").setRegistryName(getModID(), "shippilotschair").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		passengerChair = new BlockShipPassengerChair(Material.IRON).setHardness(4f).setUnlocalizedName("shippassengerchair").setRegistryName(getModID(), "shippassengerchair").setCreativeTab(ValkyrienWarfareMod.vwTab);
		shipHelm = new BlockShipHelm(Material.WOOD).setHardness(4f).setUnlocalizedName("shiphelm").setRegistryName(getModID(), "shiphelm").setCreativeTab(ValkyrienWarfareMod.vwTab);
		shipWheel = new BlockShipWheel(Material.WOOD).setHardness(5f).setUnlocalizedName("shiphelmwheel").setRegistryName(getModID(), "shiphelmwheel");
		shipTelegraph = new BlockShipTelegraph(Material.WOOD).setHardness(5f).setUnlocalizedName("shiptelegraph").setRegistryName(getModID(), "shiptelegraph").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		thrustRelay = new BlockThrustRelay(Material.IRON).setHardness(5f).setUnlocalizedName("thrustrelay").setRegistryName(getModID(), "thrustrelay").setCreativeTab(ValkyrienWarfareMod.vwTab);
		thrustModulator = new BlockThrustModulator(Material.IRON).setHardness(8f).setUnlocalizedName("thrustmodulator").setRegistryName(getModID(), "thrustmodulator").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		shipHullSealer = new BlockShipHullSealer(Material.IRON).setHardness(5f).setUnlocalizedName("shiphullsealer").setRegistryName(getModID(), "shiphullsealer").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		airshipController_zepplin = new BlockAirshipController_Zepplin(Material.WOOD).setHardness(5f).setUnlocalizedName("airshipcontroller_zepplin").setRegistryName(getModID(), "airshipcontroller_zepplin").setCreativeTab(ValkyrienWarfareMod.vwTab);
		
		gyroscope = new BlockGyroscope(Material.IRON).setHardness(5f).setUnlocalizedName("vw_gyroscope").setRegistryName(getModID(), "vw_gyroscope").setCreativeTab(ValkyrienWarfareMod.vwTab);
	}
	
	@Override
	public void init(FMLStateEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(ValkyrienWarfareMod.INSTANCE, new ControlGUIHandler());
	}
	
	@Override
	public void postInit(FMLStateEvent event) {
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(basicEngine, advancedEngine, eliteEngine, ultimateEngine, redstoneEngine);
		event.getRegistry().registerAll(antigravityEngine, advancedEtherCompressor, eliteEtherCompressor, ultimateEtherCompressor, creativeEtherCompressor);
		event.getRegistry().registerAll(basicHoverController, dopedEtherium, balloonBurner, pilotsChair);
		event.getRegistry().registerAll(passengerChair, shipHelm, shipWheel, shipTelegraph);
		event.getRegistry().registerAll(thrustRelay, thrustModulator, shipHullSealer, airshipController_zepplin, gyroscope);
	}
	
	@Override
	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityHoverController.class, "tilehovercontroller");
		GameRegistry.registerTileEntity(TileEntityNormalEtherCompressor.class, "tileantigravengine");
		GameRegistry.registerTileEntity(BalloonBurnerTileEntity.class, "tileballoonburner");
		GameRegistry.registerTileEntity(TileEntityPilotsChair.class, "tilemanualshipcontroller");
		GameRegistry.registerTileEntity(ThrustRelayTileEntity.class, "tilethrustrelay");
		GameRegistry.registerTileEntity(ThrustModulatorTileEntity.class, "tilethrustmodulator");
		GameRegistry.registerTileEntity(TileEntityShipHelm.class, "tileshiphelm");
		GameRegistry.registerTileEntity(TileEntityShipTelegraph.class, "tileshiptelegraph");
		GameRegistry.registerTileEntity(TileEntityPropellerEngine.class, "tilepropellerengine");
		GameRegistry.registerTileEntity(TileEntityZepplinController.class, "tilezepplin_controller");
		GameRegistry.registerTileEntity(TileEntityHullSealer.class, "tileentityshiphullsealer");
		GameRegistry.registerTileEntity(TileEntityGyroscope.class, "tileentitygyroscope");
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(systemLinker, airshipStealer, relayWire);
		
		registerItemBlocks(event, basicEngine, advancedEngine, eliteEngine, ultimateEngine, redstoneEngine);
		registerItemBlocks(event, antigravityEngine, advancedEtherCompressor, eliteEtherCompressor, ultimateEtherCompressor, creativeEtherCompressor);
		registerItemBlocks(event, basicHoverController, dopedEtherium, balloonBurner, pilotsChair);
		registerItemBlocks(event, passengerChair, shipHelm, shipWheel, shipTelegraph);
		registerItemBlocks(event, thrustRelay, thrustModulator, shipHullSealer, airshipController_zepplin, gyroscope);
	}
	
	@Override
	public void registerRecipes() {
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_pilotsChair"), null, new ItemStack(pilotsChair), "SLS", "EWE", " S ", 'S', Items.STICK, 'L', Items.LEATHER, 'W', Item.getItemFromBlock(Blocks.LOG), 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_systemLinker"), null, new ItemStack(systemLinker), "IR ", " DR", "I I", 'I', Items.IRON_INGOT, 'D', Items.DIAMOND, 'R', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_balloonBurner"), null, new ItemStack(balloonBurner), "IFI", "WIW", "PPP", 'I', Items.IRON_INGOT, 'F', Items.FLINT_AND_STEEL, 'W', Item.getItemFromBlock(Blocks.LOG), 'P', Item.getItemFromBlock(Blocks.PLANKS));
		
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_basicHoverController"), null, new ItemStack(basicHoverController), "III", "TCT", "III", 'I', Item.getItemFromBlock(Blocks.IRON_BLOCK), 'C', Items.COMPASS, 'T', Item.getItemFromBlock(Blocks.CRAFTING_TABLE));
		
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_antigravityEngine"), null, new ItemStack(antigravityEngine, 4), "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.PLANKS), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG));
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_advancedEtherCompressor"), null, new ItemStack(advancedEtherCompressor, 4), "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.STONE), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG));
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_advancedEtherCompressor"), null, new ItemStack(advancedEtherCompressor, 2), "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.COBBLESTONE), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG));
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_eliteEtherCompressor"), null, new ItemStack(eliteEtherCompressor, 4), "III", "IEI", "WEW", 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG));
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_ultimateEtherCompressor"), null, new ItemStack(ultimateEtherCompressor, 4), "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.OBSIDIAN), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorld.INSTANCE.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG));
		
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_basicEngine"), null, new ItemStack(basicEngine, 4), "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.PLANKS), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_advancedEngine"), null, new ItemStack(advancedEngine, 4), "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.STONE), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_advancedEngine"), null, new ItemStack(advancedEngine, 2), "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.COBBLESTONE), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_eliteEngine"), null, new ItemStack(eliteEngine, 4), "III", "IPP", "III", 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ResourceLocation(getModID(), "crafting_ultimateEngine"), null, new ItemStack(ultimateEngine, 4), "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.OBSIDIAN), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
	}
	
	@Override
	protected void registerNetworks() {
		controlNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("controlnetwork");
		controlNetwork.registerMessage(EntityFixMessageHandler.class, EntityFixMessage.class, 0, Side.CLIENT);
		controlNetwork.registerMessage(HovercraftControllerGUIInputHandler.class, HovercraftControllerGUIInputMessage.class, 1, Side.SERVER);
		controlNetwork.registerMessage(PilotControlsMessageHandler.class, PilotControlsMessage.class, 2, Side.SERVER);
		controlNetwork.registerMessage(MessageStartPilotingHandler.class, MessageStartPiloting.class, 3, Side.CLIENT);
		controlNetwork.registerMessage(MessageStopPilotingHandler.class, MessageStopPiloting.class, 4, Side.CLIENT);
		controlNetwork.registerMessage(MessagePlayerStoppedPilotingHandler.class, MessagePlayerStoppedPiloting.class, 5, Side.SERVER);
		controlNetwork.registerMessage(ThrustModulatorGuiInputMessageHandler.class, ThrustModulatorGuiInputMessage.class, 6, Side.SERVER);
	}
	
	@Override
	protected void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ICapabilityLastRelay.class, new StorageLastRelay(), ImplCapabilityLastRelay.class);
	}
}
