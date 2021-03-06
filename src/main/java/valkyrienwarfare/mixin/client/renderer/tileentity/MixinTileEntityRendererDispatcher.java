package valkyrienwarfare.mixin.client.renderer.tileentity;

import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher {

	@Shadow
	public static double staticPlayerX;
	@Shadow
	public static double staticPlayerY;
	@Shadow
	public static double staticPlayerZ;
	@Shadow
	public double entityX;
	@Shadow
	public double entityY;
	@Shadow
	public double entityZ;
	@Shadow
	public World world;
	@Shadow
	private boolean drawingBatch;

	@Shadow
	public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
	}

	@Shadow
	public void drawBatch(int pass) {
	}

	@Shadow
	public void preDrawBatch() {
	}

	@Overwrite
	public void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage) {
		BlockPos pos = tileentityIn.getPos();
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(tileentityIn.getWorld(), pos);

		if (wrapper != null && wrapper.wrapping != null && wrapper.wrapping.renderer != null) {
			try {
				GlStateManager.resetColor();

				if (drawingBatch) {
					this.drawBatch(MinecraftForgeClient.getRenderPass());
					this.preDrawBatch();
				}

				wrapper.wrapping.renderer.setupTranslation(partialTicks);

				double playerX = TileEntityRendererDispatcher.staticPlayerX;
				double playerY = TileEntityRendererDispatcher.staticPlayerY;
				double playerZ = TileEntityRendererDispatcher.staticPlayerZ;

				TileEntityRendererDispatcher.staticPlayerX = wrapper.wrapping.renderer.offsetPos.getX();
				TileEntityRendererDispatcher.staticPlayerY = wrapper.wrapping.renderer.offsetPos.getY();
				TileEntityRendererDispatcher.staticPlayerZ = wrapper.wrapping.renderer.offsetPos.getZ();

				if (drawingBatch) {
					this.renderTileEntityOriginal(tileentityIn, partialTicks, destroyStage);
					this.drawBatch(MinecraftForgeClient.getRenderPass());
					this.preDrawBatch();
				} else {
					this.renderTileEntityOriginal(tileentityIn, partialTicks, destroyStage);
				}
				TileEntityRendererDispatcher.staticPlayerX = playerX;
				TileEntityRendererDispatcher.staticPlayerY = playerY;
				TileEntityRendererDispatcher.staticPlayerZ = playerZ;

				wrapper.wrapping.renderer.inverseTransform(partialTicks);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.renderTileEntityOriginal(tileentityIn, partialTicks, destroyStage);
		}
	}

	public void renderTileEntityOriginal(TileEntity tileentityIn, float partialTicks, int destroyStage) {
		if (tileentityIn.getDistanceSq(this.entityX, this.entityY, this.entityZ) < tileentityIn.getMaxRenderDistanceSquared()) {
			RenderHelper.enableStandardItemLighting();
			if (!drawingBatch || !tileentityIn.hasFastRenderer()) {
				int i = this.world.getCombinedLight(tileentityIn.getPos(), 0);
				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			BlockPos blockpos = tileentityIn.getPos();
			this.renderTileEntityAt(tileentityIn, (double) blockpos.getX() - staticPlayerX, (double) blockpos.getY() - staticPlayerY, (double) blockpos.getZ() - staticPlayerZ, partialTicks, destroyStage);
		}
	}
}
