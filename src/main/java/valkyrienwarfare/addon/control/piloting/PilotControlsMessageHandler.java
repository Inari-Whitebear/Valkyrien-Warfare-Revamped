package valkyrienwarfare.addon.control.piloting;

import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PilotControlsMessageHandler implements IMessageHandler<PilotControlsMessage, IMessage> {

	@Override
	public IMessage onMessage(final PilotControlsMessage message, final MessageContext ctx) {
		IThreadListener mainThread = ctx.getServerHandler().serverController;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				World worldObj = ctx.getServerHandler().player.world;
				if (ValkyrienWarfareMod.physicsManager.getManagerForWorld(worldObj) != null) {
//                	UUID shipId = message.shipFor;
					BlockPos posFor = message.controlBlockPos;
					TileEntity tile = worldObj.getTileEntity(posFor);

					if (tile instanceof ITileEntityPilotable) {
						((ITileEntityPilotable) tile).onPilotControlsMessage(message, ctx.getServerHandler().player);
					}
				}
			}
		});

		return null;
	}

}
