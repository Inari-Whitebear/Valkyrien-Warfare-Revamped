package valkyrienwarfare.proxy;

import valkyrienwarfare.api.addons.Module;
import valkyrienwarfare.api.addons.ModuleProxy;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.EventsClient;
import valkyrienwarfare.math.Quaternion;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.render.PhysObjectRenderFactory;
import valkyrienwarfare.VWKeyHandler;
import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	public static ICamera lastCamera;
	VWKeyHandler keyEvents = new VWKeyHandler();

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		OBJLoader.INSTANCE.addDomain(ValkyrienWarfareMod.MODID.toLowerCase());
		RenderingRegistry.registerEntityRenderingHandler(PhysicsWrapperEntity.class, new PhysObjectRenderFactory());
		
		for (Module addon : ValkyrienWarfareMod.addons) {
			ModuleProxy proxy = addon.getClientProxy();
			if (proxy != null)  {
				proxy.preInit(e);
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		MinecraftForge.EVENT_BUS.register(new EventsClient());
		MinecraftForge.EVENT_BUS.register(keyEvents);
		registerBlockItem(ValkyrienWarfareMod.physicsInfuser);
		registerBlockItem(ValkyrienWarfareMod.physicsInfuserCreative);
		
		for (Module addon : ValkyrienWarfareMod.addons) {
			ModuleProxy proxy = addon.getClientProxy();
			if (proxy != null)  {
				proxy.init(e);
			}
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
		Minecraft.getMinecraft().getFramebuffer().enableStencil();
		
		for (Module addon : ValkyrienWarfareMod.addons) {
			ModuleProxy proxy = addon.getClientProxy();
			if (proxy != null)  {
				proxy.postInit(e);
			}
		}
	}

	private void registerBlockItem(Block toRegister) {
		Item item = Item.getItemFromBlock(toRegister);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ValkyrienWarfareMod.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

	@Override
	public void updateShipPartialTicks(PhysicsWrapperEntity entity) {
		double partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		// entity.wrapping.renderer.updateTranslation(partialTicks);
		Vector centerOfRotation = entity.wrapping.centerCoord;
		if (entity.wrapping.renderer == null) {
			return;
		}
		entity.wrapping.renderer.curPartialTick = partialTicks;

		double moddedX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double moddedY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double moddedZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		double p0 = Minecraft.getMinecraft().player.lastTickPosX + (Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.lastTickPosX) * (double) partialTicks;
		double p1 = Minecraft.getMinecraft().player.lastTickPosY + (Minecraft.getMinecraft().player.posY - Minecraft.getMinecraft().player.lastTickPosY) * (double) partialTicks;
		double p2 = Minecraft.getMinecraft().player.lastTickPosZ + (Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.lastTickPosZ) * (double) partialTicks;

		Quaternion smoothRotation = entity.wrapping.renderer.getSmoothRotationQuat(partialTicks);
		double[] radians = smoothRotation.toRadians();

		double moddedPitch = Math.toDegrees(radians[0]);
		double moddedYaw = Math.toDegrees(radians[1]);
		double moddedRoll = Math.toDegrees(radians[2]);

		entity.wrapping.coordTransform.updateRenderMatrices(moddedX, moddedY, moddedZ, moddedPitch, moddedYaw, moddedRoll);
	}
}
