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

package valkyrienwarfare.addon.control.nodenetwork;

import valkyrienwarfare.api.RotationMatrices;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.NBTUtils;
import valkyrienwarfare.physics.PhysicsCalculations;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BasicForceNodeTileEntity extends BasicNodeTileEntity implements IForceTile {

	protected double maxThrust = 5000D;
	protected double currentThrust = 0D;
	private Vector forceOutputVector = new Vector();
	private Vector normalVelocityUnoriented;
	private int ticksSinceLastControlSignal = 0;
	//Tells if the tile is in Ship Space, if it isn't then it doesn't try to find a parent Ship object
	private boolean hasAlreadyCheckedForParent = false;

	/**
	 * Only used for the NBT creation, other <init> calls should go through the other methods
	 */
	public BasicForceNodeTileEntity() {
	}

	public BasicForceNodeTileEntity(Vector normalVeclocityUnoriented, boolean isForceOutputOriented, double maxThrust) {
		this.normalVelocityUnoriented = normalVeclocityUnoriented;
		this.maxThrust = maxThrust;
	}

	/**
	 * True for all engines except for Ether Compressors
	 *
	 * @return
	 */
	public boolean isForceOutputOriented() {
		return true;
	}

	@Override
	public Vector getForceOutputNormal() {
		// TODO Auto-generated method stub
		return normalVelocityUnoriented;
	}

	@Override
	public Vector getForceOutputUnoriented(double secondsToApply) {
		return normalVelocityUnoriented.getProduct(currentThrust * secondsToApply);
	}

	@Override
	public Vector getForceOutputOriented(double secondsToApply) {
		Vector outputForce = getForceOutputUnoriented(secondsToApply);
		if (isForceOutputOriented()) {
			if (updateParentShip()) {
				RotationMatrices.applyTransform(tileNode.getPhysicsObject().coordTransform.lToWRotation, outputForce);
			}
		}
		return outputForce;
	}

	@Override
	public double getMaxThrust() {
		return maxThrust;
	}

	@Override
	public double getThrust() {
		return currentThrust;
	}

	@Override
	public void setThrust(double newMagnitude) {
		currentThrust = newMagnitude;
	}

	@Override
	public Vector getPositionInLocalSpaceWithOrientation() {
		if (updateParentShip()) {
			return null;
		}
		PhysicsWrapperEntity parentShip = tileNode.getPhysicsObject().wrapper;
		Vector engineCenter = new Vector(getPos().getX() + .5D, getPos().getY() + .5D, getPos().getZ() + .5D);
		RotationMatrices.applyTransform(parentShip.wrapping.coordTransform.lToWTransform, engineCenter);
		engineCenter.subtract(parentShip.posX, parentShip.posY, parentShip.posZ);
		return engineCenter;
	}

	@Override
	public Vector getVelocityAtEngineCenter() {
		if (updateParentShip()) {
			return null;
		}
		PhysicsCalculations calculations = tileNode.getPhysicsObject().physicsProcessor;
		return calculations.getVelocityAtPoint(getPositionInLocalSpaceWithOrientation());
	}

	@Override
	public Vector getLinearVelocityAtEngineCenter() {
		if (updateParentShip()) {
			return null;
		}
		PhysicsCalculations calculations = tileNode.getPhysicsObject().physicsProcessor;
		return calculations.linearMomentum;
	}

	@Override
	public Vector getAngularVelocityAtEngineCenter() {
		if (updateParentShip()) {
			return null;
		}
		PhysicsCalculations calculations = tileNode.getPhysicsObject().physicsProcessor;
		return calculations.angularVelocity.cross(getPositionInLocalSpaceWithOrientation());
	}


	@Override
	public void readFromNBT(NBTTagCompound compound) {
		maxThrust = compound.getDouble("maxThrust");
		currentThrust = compound.getDouble("currentThrust");
		normalVelocityUnoriented = NBTUtils.readVectorFromNBT("normalVelocityUnoriented", compound);
		ticksSinceLastControlSignal = compound.getInteger("ticksSinceLastControlSignal");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("maxThrust", maxThrust);
		compound.setDouble("currentThrust", currentThrust);
		NBTUtils.writeVectorToNBT("normalVelocityUnoriented", normalVelocityUnoriented, compound);
		compound.setInteger("ticksSinceLastControlSignal", ticksSinceLastControlSignal);
		return super.writeToNBT(compound);
	}

	/**
	 * Returns false if a parent Ship exists, and true if otherwise
	 *
	 * @return
	 */
	public boolean updateParentShip() {
		if (hasAlreadyCheckedForParent) {
			return tileNode.getPhysicsObject() == null;
		}
		BlockPos pos = this.getPos();
		World world = this.getWorld();
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, pos);
		//Already checked
		hasAlreadyCheckedForParent = true;
		if (wrapper != null) {
			tileNode.updateParentEntity(wrapper.wrapping);
			return false;
		} else {
			return true;
		}
	}

	public void updateTicksSinceLastRecievedSignal() {
		ticksSinceLastControlSignal = 0;
	}

	@Override
	public void update() {
		super.update();

		ticksSinceLastControlSignal++;
		if (ticksSinceLastControlSignal > 5) {
			setThrust(getThrust() * .9D);
		}
	}

}
