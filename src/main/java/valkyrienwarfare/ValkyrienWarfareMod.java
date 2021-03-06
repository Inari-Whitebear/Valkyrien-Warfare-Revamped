package valkyrienwarfare;

import valkyrienwarfare.api.addons.Module;
import valkyrienwarfare.api.addons.VWAddon;
import valkyrienwarfare.api.DataTag;
import valkyrienwarfare.api.ValkyrienWarfareHooks;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.block.BlockPhysicsInfuser;
import valkyrienwarfare.block.BlockPhysicsInfuserCreative;
import valkyrienwarfare.capability.IAirshipCounterCapability;
import valkyrienwarfare.capability.ImplAirshipCounterCapability;
import valkyrienwarfare.capability.StorageAirshipCounter;
import valkyrienwarfare.chunkmanagement.DimensionPhysicsChunkManager;
import valkyrienwarfare.gui.TabValkyrienWarfare;
import valkyrienwarfare.mixin.MixinLoaderForge;
import valkyrienwarfare.network.*;
import valkyrienwarfare.physicsmanagement.DimensionPhysObjectManager;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Mod(modid = ValkyrienWarfareMod.MODID, name = ValkyrienWarfareMod.MODNAME, version = ValkyrienWarfareMod.MODVER, guiFactory = "valkyrienwarfare.gui.GuiFactoryValkyrienWarfare", updateJSON = "https://raw.githubusercontent.com/BigBastard/Valkyrien-Warfare-Revamped/update.json")
public class ValkyrienWarfareMod {
	public static final ArrayList<Module> addons = new ArrayList<>();
	public static final String MODID = "valkyrienwarfare";
	public static final String MODNAME = "Valkyrien Warfare";
	public static final String MODVER = "0.9_alpha";
	@CapabilityInject(IAirshipCounterCapability.class)
	public static final Capability<IAirshipCounterCapability> airshipCounter = null;
	// NOTE: These only calculate physics, so they are only relevant to the Server end
	public static final ExecutorService MultiThreadExecutor = Executors.newWorkStealingPool();
	public static final ExecutorService PhysicsMasterThread = Executors.newCachedThreadPool();
	@SidedProxy(clientSide = "valkyrienwarfare.proxy.ClientProxy", serverSide = "valkyrienwarfare.proxy.ServerProxy")
	public static CommonProxy proxy;
	public static File configFile;
	public static Configuration config;
	public static boolean dynamicLighting;
	public static boolean multiThreadedPhysics;
	public static boolean doSplitting = false;
	public static boolean doShipCollision = false;
	public static boolean shipsSpawnParticles = false;
	public static Vector gravity = new Vector(0, -9.8D, 0);
	public static int physIter = 10;
	public static double physSpeed = .05D;
	public static Block physicsInfuser;
	public static Block physicsInfuserCreative;
	public static SimpleNetworkWrapper physWrapperNetwork;
	public static DimensionPhysicsChunkManager chunkManager;
	public static DimensionPhysObjectManager physicsManager;
	public static CreativeTabs vwTab = new TabValkyrienWarfare();
	@Instance(MODID)
	public static ValkyrienWarfareMod INSTANCE = new ValkyrienWarfareMod();
	public static int airStateIndex;
	public static double standingTolerance = .42D;
	public static int maxShipSize = 15000;
	public static double shipUpperLimit = 1000D;
	public static double shipLowerLimit = -30D;
	public static int maxAirships = -1;
	public static boolean highAccuracyCollisions = false;
	public static boolean accurateRain = false;
	public static boolean runAirshipPermissions = false;
	public static double shipmobs_spawnrate = .01D;
	public static Logger VWLogger;
	private static boolean hasAddonRegistrationEnded = false;
	public DataTag tag = null;
	
	/**
	 * Called by the game when loading the configuration file, also called whenever the player makes a change in the MOD OPTIONS menu,
	 * effectively reloading all the configuration values
	 *
	 * @param conf
	 */
	public static void applyConfig(Configuration conf) {
		// dynamicLighting = config.get(Configuration.CATEGORY_GENERAL, "DynamicLighting", false).getBoolean();
		
		// Property spawnParticlesParticle = config.get(Configuration.CATEGORY_GENERAL, "Ships spawn particles", false).getBoolean();
		multiThreadedPhysics = config.get(Configuration.CATEGORY_GENERAL, "Multi-Threaded physics", true, "Use Multi-Threaded physics").getBoolean();
		
		doShipCollision = config.get(Configuration.CATEGORY_GENERAL, "Enable Ship collision", true).getBoolean();
		
		shipUpperLimit = config.get(Configuration.CATEGORY_GENERAL, "Ship Y-Height Maximum", 1000D).getDouble();
		shipLowerLimit = config.get(Configuration.CATEGORY_GENERAL, "Ship Y-Height Minimum", -30D).getDouble();
		
		maxAirships = config.get(Configuration.CATEGORY_GENERAL, "Max airships per player", -1, "Players can't own more than this many airships at once. Set to -1 to disable.").getInt();
		
		accurateRain = config.get(Configuration.CATEGORY_GENERAL, "Enable accurate rain on ships", false, "Debug feature, takes a lot of processing power").getBoolean();
		
		shipsSpawnParticles = config.get(Configuration.CATEGORY_GENERAL, "Enable particle spawns on Ships", true, "Ex. Torch Particles").getBoolean();
		
		runAirshipPermissions = config.get(Configuration.CATEGORY_GENERAL, "Enable airship permissions", false, "Enables the airship permissions system").getBoolean();
		
		shipmobs_spawnrate = config.get(Configuration.CATEGORY_GENERAL, "The spawn rate for ship mobs", .01D, "The spawn rate for ship mobs").getDouble();
	}
	
	public static File getWorkingFolder() {
		File toBeReturned;
		try {
			if (FMLCommonHandler.instance().getSide().isClient()) {
				toBeReturned = Minecraft.getMinecraft().mcDataDir;
			} else {
				toBeReturned = FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");
			}
			return toBeReturned;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Checks to see if a player's airship counter can be changed.
	 *
	 * @param isAdding Should be true if you are adding a player, false if removing the player.
	 * @param player   The player to check for
	 * @return
	 */
	public static boolean canChangeAirshipCounter(boolean isAdding, EntityPlayer player) {
		if (isAdding) {
			if (ValkyrienWarfareMod.maxAirships == -1) {
				return true;
			}
			
			return player.getCapability(ValkyrienWarfareMod.airshipCounter, null).getAirshipCount() < ValkyrienWarfareMod.maxAirships;
		} else {
			return player.getCapability(ValkyrienWarfareMod.airshipCounter, null).getAirshipCount() > 0;
		}
	}
	
	public static void registerBlock(Block block) {
		GameRegistry.register(block);
		registerItemBlock(block);
	}
	
	public static void registerItemBlock(Block block) {
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
	
	public static void registerAddon(Module module) {
		if (hasAddonRegistrationEnded) {
			throw new IllegalStateException("Attempting to register addon after FMLConstructionEvent");
		} else {
			System.out.println("[VW Addon System] Registering addon: " + module);
			addons.add(module);
		}
	}
	
	@EventHandler
	public void fmlConstruct(FMLConstructionEvent event) {
		URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
		ArrayList<String> allAddons = new ArrayList<>();
		if (!MixinLoaderForge.isObfuscatedEnvironment) { //if in dev, read default addons from gradle output folder
			File f = ValkyrienWarfareMod.getWorkingFolder();
			File defaultAddons;
			String[] list = f.list();
			boolean rootDir = false;
			for (String s : list) {
				if (s.endsWith("build.gradle")) {
					rootDir = true;
				}
			}
			if (rootDir) { //assume root directory
			        defaultAddons = new File(f.getPath() + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar + "vwAddon_default"); 
			} else { //assume run/ directory or similar
			        defaultAddons = new File(f.getAbsoluteFile().getParentFile().getParent() + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar + "vwAddon_default"); 
			}
			System.out.println(defaultAddons.getAbsolutePath());
			try {
				InputStream inputStream = new FileInputStream(defaultAddons);
				Scanner scanner = new Scanner(inputStream);
				while (scanner.hasNextLine()) {
					String className = scanner.nextLine().trim();
					allAddons.add(className);
					System.out.println("Found addon " + className);
				}
				scanner.close();
				inputStream.close();
			} catch (IOException e)   {
				e.printStackTrace();
			}
		}
		for (URL url : classLoader.getURLs()) {
			try {
				//ZipFile file = new ZipFile(new File(url.toURI()));
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(new File(url.getPath()))));
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().startsWith("vwAddon_")) {
						try {
							ZipFile file = new ZipFile(new File(url.getPath()));
							InputStream inputStream = file.getInputStream(file.getEntry(entry.getName()));
							Scanner scanner = new Scanner(inputStream);
							while (scanner.hasNextLine()) {
								String className = scanner.nextLine().trim();
								allAddons.add(className);
								System.out.println("Found addon " + className);
							}
							scanner.close();
							inputStream.close();
						} catch (IOException e) {
							//wtf java
						}
						break;
					}
				}
				zis.close();
			} catch (IOException e) {
				// wtf java
			}
		}
		
		allAddons.forEach(className -> {
			try {
				Class<?> abstractclass = Class.forName(className);
				if (abstractclass.isAnnotationPresent(VWAddon.class)) {
					Module module = (Module) abstractclass.newInstance();
					registerAddon(module);
				} else {
					System.out.println("Class " + className + " does not have @VWAddon annonation, not loading");
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.out.println("Not loading addon: " + className);
			}
		});
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		hasAddonRegistrationEnded = true;
		
		proxy.preInit(event);
		registerBlocks(event);
		registerRecipies(event);
		registerNetworks(event);
		runConfiguration(event);
		registerCapibilities();
		ValkyrienWarfareHooks.methods = new RealMethods();
		ValkyrienWarfareHooks.isValkyrienWarfareInstalled = true;
		VWLogger = Logger.getLogger("ValkyrienWarfare");
		
		for (Module addon : addons) {
			addon.preInit(event);
			addon.doRegisteringStuffPreInit();
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "PhysWrapper"), PhysicsWrapperEntity.class, "PhysWrapper", 70, this, 120, 1, false);
		
		for (Module addon : addons) {
			addon.init(event);
			addon.doRegisteringStuffInit();
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		airStateIndex = Block.getStateId(Blocks.AIR.getDefaultState());
		BlockPhysicsRegistration.registerCustomBlockMasses();
		BlockPhysicsRegistration.registerVanillaBlockForces();
		BlockPhysicsRegistration.registerBlocksToNotPhysicise();
		
		
		ForgeChunkManager.setForcedChunkLoadingCallback(INSTANCE, new VWChunkLoadingCallback());
		////We're stealing these tickets bois!////
		try {
			Field ticketConstraintsField = ForgeChunkManager.class.getDeclaredField("ticketConstraints");
			Field chunkConstraintsField = ForgeChunkManager.class.getDeclaredField("chunkConstraints");
			
			ticketConstraintsField.setAccessible(true);
			chunkConstraintsField.setAccessible(true);
			
			Object ticketConstraints = ticketConstraintsField.get(null);
			Object chunkConstraints = chunkConstraintsField.get(null);
			
			Map<String, Integer> ticketsMap = (Map<String, Integer>) ticketConstraints;
			Map<String, Integer> chunksMap = (Map<String, Integer>) chunkConstraints;
			
			ticketsMap.put(MODID, new Integer(69696969));
			chunksMap.put(MODID, new Integer(69696969));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("DAMNIT LEX!");
		}
		
		for (Module addon : addons) {
			addon.postInit(event);
		}
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		MinecraftServer server = event.getServer();
		ModCommands.registerCommands(server);
	}
	
	private void registerNetworks(FMLStateEvent event) {
		physWrapperNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("physChannel");
		physWrapperNetwork.registerMessage(PhysWrapperPositionHandler.class, PhysWrapperPositionMessage.class, 0, Side.CLIENT);
		physWrapperNetwork.registerMessage(PlayerShipRefrenceHandler.class, PlayerShipRefrenceMessage.class, 1, Side.SERVER);
		physWrapperNetwork.registerMessage(EntityRelativePositionMessageHandler.class, EntityRelativePositionMessage.class, 2, Side.CLIENT);
	}
	
	private void registerBlocks(FMLStateEvent event) {
		physicsInfuser = new BlockPhysicsInfuser(Material.ROCK).setHardness(12f).setUnlocalizedName("shipblock").setRegistryName(MODID, "shipblock").setCreativeTab(vwTab);
		physicsInfuserCreative = new BlockPhysicsInfuserCreative(Material.ROCK).setHardness(12f).setUnlocalizedName("shipblockcreative").setRegistryName(MODID, "shipblockcreative").setCreativeTab(vwTab);
		
		registerBlock(physicsInfuser);
		registerBlock(physicsInfuserCreative);
	}
	
	private void registerRecipies(FMLStateEvent event) {
		GameRegistry.addRecipe(new ItemStack(physicsInfuser), "IEI", "ODO", "IEI", 'E', Items.ENDER_PEARL, 'D', Items.DIAMOND, 'O', Item.getItemFromBlock(Blocks.OBSIDIAN), 'I', Items.IRON_INGOT);
	}
	
	private void runConfiguration(FMLPreInitializationEvent event) {
		configFile = event.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
		config.load();
		applyConfig(config);
		config.save();
	}
	
	@EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
		this.loadConfig();
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		this.saveConfig();
	}
	
	public void loadConfig() {
		File file = new File(ValkyrienWarfareMod.getWorkingFolder(), "/valkyrienwarfaresettings.dat");
		
		if (!file.exists()) {
			tag = new DataTag(file);
			tag.setBoolean("doGravity", true);
			tag.setBoolean("doPhysicsBlocks", true);
			tag.setBoolean("doBalloons", true);
			tag.setBoolean("doAirshipRotation", true);
			tag.setBoolean("doAirshipMovement", true);
			tag.setBoolean("doSplitting", false);
			tag.setInteger("maxShipSize", 15000);
			tag.setDouble("gravityVecX", 0);
			tag.setDouble("gravityVecY", -9.8);
			tag.setDouble("gravityVecZ", 0);
			tag.setInteger("physicsIterations", 10);
			tag.setDouble("physicsSpeed", 0.05);
			tag.setBoolean("doEtheriumLifting", true);
			tag.save();
		} else {
			tag = new DataTag(file);
		}
		
		PhysicsSettings.doGravity = tag.getBoolean("doGravity", true);
		PhysicsSettings.doPhysicsBlocks = tag.getBoolean("doPhysicsBlocks", true);
		PhysicsSettings.doBalloons = tag.getBoolean("doBalloons", true);
		PhysicsSettings.doAirshipRotation = tag.getBoolean("doAirshipRotation", true);
		PhysicsSettings.doAirshipMovement = tag.getBoolean("doAirshipMovement", true);
		ValkyrienWarfareMod.doSplitting = tag.getBoolean("doSplitting", false);
		ValkyrienWarfareMod.maxShipSize = tag.getInteger("maxShipSize", 15000);
		ValkyrienWarfareMod.physIter = tag.getInteger("physicsIterations", 8);
		ValkyrienWarfareMod.physSpeed = tag.getDouble("physicsSpeed", 0.05);
		ValkyrienWarfareMod.gravity = new Vector(tag.getDouble("gravityVecX", 0.0), tag.getDouble("gravityVecY", -9.8), tag.getDouble("gravityVecZ", 0.0));
		PhysicsSettings.doEtheriumLifting = tag.getBoolean("doEtheriumLifting", true);
		
		//save the tag in case new fields are added, this way they are saved right away
		tag.save();
	}
	
	public void saveConfig() {
		tag.setBoolean("doGravity", PhysicsSettings.doGravity);
		tag.setBoolean("doPhysicsBlocks", PhysicsSettings.doPhysicsBlocks);
		tag.setBoolean("doBalloons", PhysicsSettings.doBalloons);
		tag.setBoolean("doAirshipRotation", PhysicsSettings.doAirshipRotation);
		tag.setBoolean("doAirshipMovement", PhysicsSettings.doAirshipMovement);
		tag.setBoolean("doSplitting", ValkyrienWarfareMod.doSplitting);
		tag.setInteger("maxShipSize", ValkyrienWarfareMod.maxShipSize);
		tag.setDouble("gravityVecX", ValkyrienWarfareMod.gravity.X);
		tag.setDouble("gravityVecY", ValkyrienWarfareMod.gravity.Y);
		tag.setDouble("gravityVecZ", ValkyrienWarfareMod.gravity.Z);
		tag.setInteger("physicsIterations", ValkyrienWarfareMod.physIter);
		tag.setDouble("physicsSpeed", ValkyrienWarfareMod.physSpeed);
		tag.save();
	}
	
	public void registerCapibilities() {
		CapabilityManager.INSTANCE.register(IAirshipCounterCapability.class, new StorageAirshipCounter(), ImplAirshipCounterCapability.class);
	}
}
