package valkyrienwarfare.addon.control.network;

import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.ValkyrienWarfareMod;
import valkyrienwarfare.addon.control.tileentity.TileEntityHoverController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HovercraftControllerGUIInputHandler implements IMessageHandler<HovercraftControllerGUIInputMessage, IMessage> {

	@Override
	public IMessage onMessage(final HovercraftControllerGUIInputMessage message, final MessageContext ctx) {
		IThreadListener mainThread = ctx.getServerHandler().serverController;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(ctx.getServerHandler().player.world, message.tilePos);
				TileEntity tileEnt = wrapper.wrapping.VKChunkCache.getTileEntity(message.tilePos);
				if (tileEnt != null) {
					if (tileEnt instanceof TileEntityHoverController) {
						((TileEntityHoverController) tileEnt).handleGUIInput(message, ctx);
					}
				} else {
					System.out.println("Player: " + ctx.getServerHandler().player.getName() + " sent a broken packet");
				}
			}
		});
		return null;
	}

}
