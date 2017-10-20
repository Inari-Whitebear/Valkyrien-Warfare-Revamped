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

package valkyrienwarfare.addon.cannon.entities;

import valkyrienwarfare.addon.cannon.world.NewExp2;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySolidball extends EntitySnowball {


	int Pen;


	public EntitySolidball(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.01F;
	}

	protected void onImpact(RayTraceResult result) {

		if (!this.world.isRemote) {
			double x = this.posX + this.motionX / 2;
			double y = this.posY + this.motionY / 2;
			double z = this.posZ + this.motionZ / 2;
			double x2 = this.posX + this.motionX;
			double y2 = this.posY + this.motionY;
			double z2 = this.posZ + this.motionZ;
			float size = 0.6F;
			float power = 0;
			float blast = 0;
			float damage = 100F;

			NewExp2 explosion = new NewExp2(this.getEntityWorld(), null, x, y, z, size, power, damage, blast, false, true);
			explosion.newBoom(this.getEntityWorld(), null, x, y, z, size, power, damage, blast, false, true);
			explosion.newBoom(this.getEntityWorld(), null, x2, y2, z2, size, power, damage, blast, false, true);

			Pen++;
			if (Pen > 4) {
				this.setDead();
			}
		}
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {

		super.writeToNBT(compound);
		compound.setInteger("Penetration", Pen);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {

		super.readFromNBT(compound);
		Pen = compound.getInteger("Penetration");
	}


}