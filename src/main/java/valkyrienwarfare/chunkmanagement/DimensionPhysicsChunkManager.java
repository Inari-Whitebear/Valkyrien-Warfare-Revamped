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

package valkyrienwarfare.chunkmanagement;

import valkyrienwarfare.interaction.BlockPosToShipUUIDData;
import valkyrienwarfare.interaction.ShipNameUUIDData;
import valkyrienwarfare.interaction.ShipUUIDToPosData;
import valkyrienwarfare.interaction.ShipUUIDToPosData.ShipPositionData;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class DimensionPhysicsChunkManager {
	
	private HashMap<World, PhysicsChunkManager> managerPerWorld;
	private PhysicsChunkManager cachedManager;
	
	public DimensionPhysicsChunkManager() {
		managerPerWorld = new HashMap<World, PhysicsChunkManager>();
	}
	
	public void initWorld(World toInit) {
		if (!managerPerWorld.containsKey(toInit)) {
			managerPerWorld.put(toInit, new PhysicsChunkManager(toInit));
		}
	}
	
	public PhysicsChunkManager getManagerForWorld(World world) {
		if (world == null) {
			return null;
		}
		if (cachedManager == null || cachedManager.worldObj != world) {
			cachedManager = managerPerWorld.get(world);
			if (cachedManager == null) {
				initWorld(world);
				cachedManager = managerPerWorld.get(world);
			}
		}
		return cachedManager;
	}
	
	public void removeWorld(World world) {
		managerPerWorld.remove(world);
	}
	
	public void registerChunksForShip(PhysicsWrapperEntity wrapper) {
		World shipWorld = wrapper.world;
		BlockPosToShipUUIDData data = BlockPosToShipUUIDData.get(shipWorld);
		data.addShipToPersistantMap(wrapper);
	}
	
	public void removeRegistedChunksForShip(PhysicsWrapperEntity wrapper) {
		World shipWorld = wrapper.world;
		BlockPosToShipUUIDData data = BlockPosToShipUUIDData.get(shipWorld);
		
		data.removeShipFromPersistantMap(wrapper);
	}
	
	public UUID getShipIDManagingPos_Persistant(World worldFor, int chunkX, int chunkZ) {
		BlockPosToShipUUIDData data = BlockPosToShipUUIDData.get(worldFor);
		
		return data.getShipUUIDFromPos(chunkX, chunkZ);
	}
	
	public ShipPositionData getShipPosition_Persistant(World worldFor, UUID shipID) {
		ShipUUIDToPosData data = ShipUUIDToPosData.get(worldFor);
		
		return data.getShipPositionData(shipID);
	}
	
	public void updateShipPosition(PhysicsWrapperEntity wrapper) {
		World shipWorld = wrapper.world;
		ShipUUIDToPosData data = ShipUUIDToPosData.get(shipWorld);
		
		data.updateShipPosition(wrapper);
	}
	
	public void removeShipPosition(PhysicsWrapperEntity wrapper) {
		World shipWorld = wrapper.world;
		ShipUUIDToPosData data = ShipUUIDToPosData.get(shipWorld);
		
		data.removeShipFromMap(wrapper);
	}
	
	public void removeShipNameRegistry(PhysicsWrapperEntity wrapper) {
		World shipWorld = wrapper.world;
		ShipNameUUIDData data = ShipNameUUIDData.get(shipWorld);
		
		data.removeShipFromRegistry(wrapper);
	}
}
