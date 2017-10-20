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

package valkyrienwarfare.interaction;

import valkyrienwarfare.api.RotationMatrices;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.EventsClient;
import valkyrienwarfare.physicsmanagement.CoordTransformObject;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.ValkyrienWarfareMod;
import valkyrienwarfare.addon.combat.entity.EntityCannonBall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntityDraggable {
	public static void tickAddedVelocityForWorld(World world) {
		try {
			//TODO: Fix this
			if (true) {
//				return;
			}
			for (int i = 0; i < world.loadedEntityList.size(); i++) {
				Entity e = world.loadedEntityList.get(i);
				//TODO: Maybe add a check to prevent moving entities that are fixed onto a Ship, but I like the visual effect
				if (!(e instanceof PhysicsWrapperEntity) && !(e instanceof EntityCannonBall)) {
					IDraggable draggable = getDraggableFromEntity(e);
//					e.onGround = true;
//
					doTheEntityThing(e);

//					draggable.tickAddedVelocity();
//
//					e.onGround = true;
//					e.setPosition(draggable.getVelocityAddedToPlayer().X + e.posX, draggable.getVelocityAddedToPlayer().Y + e.posY, draggable.getVelocityAddedToPlayer().Z + e.posZ);
					
					if (draggable.getWorldBelowFeet() == null) {
						if (e.onGround) {
							draggable.getVelocityAddedToPlayer().zero();
							draggable.setYawDifVelocity(0);
						} else {
							if (e instanceof EntityPlayer) {
								EntityPlayer player = (EntityPlayer) e;
								if (player.isCreative() && player.capabilities.isFlying) {
									draggable.getVelocityAddedToPlayer().multiply(.99D * .95D);
									draggable.setYawDifVelocity(draggable.getYawDifVelocity() * .95D * .95D);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void doTheEntityThing(Entity entity) {
		IDraggable draggable = EntityDraggable.getDraggableFromEntity(entity);
		if (draggable.getWorldBelowFeet() != null && !ValkyrienWarfareMod.physicsManager.isEntityFixed(entity)) {
			CoordTransformObject coordTransform = draggable.getWorldBelowFeet().wrapping.coordTransform;
			
			if (entity.world.isRemote && entity instanceof EntityPlayer) {
				EventsClient.updatePlayerMouseOver(entity);
			}
			
			float rotYaw = entity.rotationYaw;
			float rotPitch = entity.rotationPitch;
			float prevYaw = entity.prevRotationYaw;
			float prevPitch = entity.prevRotationPitch;
			
			Vector oldPos = new Vector(entity);
			
			RotationMatrices.applyTransform(coordTransform.prevwToLTransform, coordTransform.prevWToLRotation, entity);
			RotationMatrices.applyTransform(coordTransform.lToWTransform, coordTransform.lToWRotation, entity);
			
			Vector newPos = new Vector(entity);
			
			//Move the entity back to its old position, the added velocity will be used afterwards
			entity.setPosition(oldPos.X, oldPos.Y, oldPos.Z);
			Vector addedVel = oldPos.getSubtraction(newPos);
			
			draggable.setVelocityAddedToPlayer(addedVel);
			
			entity.rotationYaw = rotYaw;
			entity.rotationPitch = rotPitch;
			entity.prevRotationYaw = prevYaw;
			entity.prevRotationPitch = prevPitch;
			
			Vector oldLookingPos = new Vector(entity.getLook(1.0F));
			RotationMatrices.applyTransform(coordTransform.prevWToLRotation, oldLookingPos);
			RotationMatrices.applyTransform(coordTransform.lToWRotation, oldLookingPos);
			
			double newPitch = Math.asin(oldLookingPos.Y) * -180D / Math.PI;
			double f4 = -Math.cos(-newPitch * 0.017453292D);
			double radianYaw = Math.atan2((oldLookingPos.X / f4), (oldLookingPos.Z / f4));
			radianYaw += Math.PI;
			radianYaw *= -180D / Math.PI;
			
			
			if (!(Double.isNaN(radianYaw) || Math.abs(newPitch) > 85)) {
				double wrappedYaw = MathHelper.wrapDegrees(radianYaw);
				double wrappedRotYaw = MathHelper.wrapDegrees(entity.rotationYaw);
				double yawDif = wrappedYaw - wrappedRotYaw;
				if (Math.abs(yawDif) > 180D) {
					if (yawDif < 0) {
						yawDif += 360D;
					} else {
						yawDif -= 360D;
					}
				}
				yawDif %= 360D;
				final double threshold = .1D;
				if (Math.abs(yawDif) < threshold) {
					yawDif = 0D;
				}
				draggable.setYawDifVelocity(yawDif);
			}
		}
		
		boolean onGroundOrig = entity.onGround;
		
		if (!ValkyrienWarfareMod.physicsManager.isEntityFixed(entity)) {
			float originalWalked = entity.distanceWalkedModified;
			float originalWalkedOnStep = entity.distanceWalkedOnStepModified;
			boolean originallySneaking = entity.isSneaking();
			
			entity.setSneaking(false);
			
			if (draggable.getWorldBelowFeet() == null && entity.onGround) {
				draggable.getVelocityAddedToPlayer().zero();
			}

//            entity.move(MoverType.SELF, draggable.getVelocityAddedToPlayer().X, draggable.getVelocityAddedToPlayer().Y, draggable.getVelocityAddedToPlayer().Z);
			
			entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(draggable.getVelocityAddedToPlayer().X, draggable.getVelocityAddedToPlayer().Y, draggable.getVelocityAddedToPlayer().Z));
			entity.resetPositionToBB();
			
			if (EntityArrow.class.isInstance(entity)) {
				entity.prevRotationYaw = entity.rotationYaw;
				entity.rotationYaw -= draggable.getYawDifVelocity();
			} else {
				entity.prevRotationYaw = entity.rotationYaw;
				entity.rotationYaw += draggable.getYawDifVelocity();
			}
			
			//Do not add this movement as if the entity were walking it
			entity.distanceWalkedModified = originalWalked;
			entity.distanceWalkedOnStepModified = originalWalkedOnStep;
			entity.setSneaking(originallySneaking);
			
		}
		
		if (onGroundOrig) {
			entity.onGround = onGroundOrig;
		}
		
		draggable.getVelocityAddedToPlayer().multiply(.99D);
		draggable.setYawDifVelocity(draggable.getYawDifVelocity() * .95D);
	}
	
	public static IDraggable getDraggableFromEntity(Entity entity) {
		if (entity == null) {
			return null;
		}
		Object o = entity;
		return (IDraggable) o;
	}
	
	public static Entity getEntityFromDraggable(IDraggable draggable) {
		if (draggable == null) {
			return null;
		}
		Object o = draggable;
		return (Entity) o;
	}
}
