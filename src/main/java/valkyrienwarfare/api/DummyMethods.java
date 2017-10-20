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

package valkyrienwarfare.api;

import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface DummyMethods {
	// DO NOT RUN METHODS FROM HERE! USE PhysicsEntityHooks
	
	Vector getShipCenterOfMass(Entity shipEnt);
	
	boolean isEntityAShip(Entity entityToTest);
	
	Vector getPositionInShipFromReal(World worldObj, Entity shipEnt, Vector positionInWorld);
	
	Vector getPositionInRealFromShip(World worldObj, Entity shipEnt, Vector posInShip);
	
	boolean isBlockPartOfShip(World worldObj, BlockPos pos);
	
	PhysicsWrapperEntity getShipEntityManagingPos(World worldObj, BlockPos pos);
	
	Vector getLinearVelocity(Entity shipEnt, double secondsToApply);
	
	Vector getAngularVelocity(Entity shipEnt);
	
	// Returns the matrix which converts local coordinates (The positions of the blocks in the world) to the entity coordinates (The position in front of the player)
	double[] getShipTransformMatrix(Entity shipEnt);
	
	// Note, do not call this from World coordinates; first subtract the world coords from the shipEntity xyz and then call!
	Vector getVelocityAtPoint(Entity shipEnt, Vector inBody, double secondsToApply);
	
	double getShipMass(Entity shipEnt);
}
