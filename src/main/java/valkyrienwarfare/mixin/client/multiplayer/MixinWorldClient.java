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

package valkyrienwarfare.mixin.client.multiplayer;

import valkyrienwarfare.api.RotationMatrices;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.ValkyrienWarfareMod;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient extends World {

	@Shadow
	@Final
	private NetHandlerPlayClient connection;
	@Shadow
	protected Set<ChunkPos> visibleChunks;
	@Shadow
	@Final
	private Minecraft mc;
	@Shadow
	private int ambienceTicks;

	public MixinWorldClient() {
		super(null, null, null, null, false);
	}

	@Overwrite
	public void doVoidFogParticles(int posX, int posY, int posZ) {
		if (ValkyrienWarfareMod.shipsSpawnParticles) {
			int range = 15;
			AxisAlignedBB aabb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
			List<PhysicsWrapperEntity> physEntities = ValkyrienWarfareMod.physicsManager.getManagerForWorld(WorldClient.class.cast(this)).getNearbyPhysObjects(aabb);
			for (PhysicsWrapperEntity wrapper : physEntities) {
				Vector playPosInShip = new Vector(posX + .5D, posY + .5D, posZ + .5D);
				RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.wToLTransform, playPosInShip);
				this.doVoidFogParticlesOriginal(MathHelper.floor(playPosInShip.X), MathHelper.floor(playPosInShip.Y), MathHelper.floor(playPosInShip.Z));
			}
		}
		this.doVoidFogParticlesOriginal(posX, posY, posZ);
	}

	@Shadow
	public abstract void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos);

	public void doVoidFogParticlesOriginal(int posX, int posY, int posZ) {
		int i = 32;
		Random random = new Random();
		ItemStack itemstack = this.mc.player.getHeldItemMainhand();
		boolean flag = this.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock(Blocks.BARRIER);
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int j = 0; j < 667; ++j) {
			this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
			this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
		}
	}
}
