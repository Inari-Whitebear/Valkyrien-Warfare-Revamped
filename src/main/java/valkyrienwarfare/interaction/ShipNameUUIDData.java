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

import net.minecraft.world.storage.WorldSavedData;
import valkyrienwarfare.NBTUtils;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ShipNameUUIDData extends WorldSavedData {

	private static final String key = "ShipNameUUIDData";

	public HashMap<String, Long> ShipNameToLongMap = new HashMap<String, Long>();

	public ShipNameUUIDData(String name) {
		super(name);
	}

	public ShipNameUUIDData() {
		this(key);
	}

	public static ShipNameUUIDData get(World world) {
		MapStorage storage = world.getPerWorldStorage();
		ShipNameUUIDData data = (ShipNameUUIDData) storage.getOrLoadData(ShipNameUUIDData.class, key);
		if (data == null) {
			data = new ShipNameUUIDData();
			world.setData(key, data);
		}
		return data;
	}

	/**
	 * Only run this for the initial creation of a Ship; doesnt have checks for duplicate names
	 *
	 * @param wrapper
	 * @param defaultName
	 */
	public void placeShipInRegistry(PhysicsWrapperEntity wrapper, String defaultName) {
		ShipNameToLongMap.put(defaultName, wrapper.getPersistentID().getMostSignificantBits());
		markDirty();
	}

	/**
	 * Returns true if successfully renamed the ship, false if there was a duplicate
	 *
	 * @param wrapper
	 * @param newName
	 * @param oldName
	 * @return
	 */
	public boolean renameShipInRegsitry(PhysicsWrapperEntity wrapper, String newName, String oldName) {
		if (ShipNameToLongMap.containsKey(newName)) {
			return false;
		}

		ShipNameToLongMap.put(newName, wrapper.getPersistentID().getMostSignificantBits());
		ShipNameToLongMap.remove(oldName);

		markDirty();
		return true;
	}

	public void removeShipFromRegistry(PhysicsWrapperEntity wrapper) {
		String customName = wrapper.getCustomNameTag();
		ShipNameToLongMap.remove(customName);
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		ByteBuffer buffer = NBTUtils.getByteBuf("NameToUUIDMap", nbt);
		while (buffer.hasRemaining()) {
			byte stringByteLength = buffer.get();
			byte[] stringBytes = new byte[stringByteLength];
			for (int i = 0; i < stringByteLength; i++) {
				stringBytes[i] = buffer.get();
			}
			String shipName = new String(stringBytes, StandardCharsets.UTF_8);
			long shipUUIDMostSig = buffer.getLong();
			ShipNameToLongMap.put(shipName, shipUUIDMostSig);
		}
	}

	//Inefficient, but the Ship name map shouldn't update often anyways
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Set<Entry<String, Long>> entryMap = ShipNameToLongMap.entrySet();
		int stringEntriesSize = 0;
		for (Entry<String, Long> entry : entryMap) {
			byte[] stringBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
			stringEntriesSize += (stringBytes.length) + 9;
		}

		ByteBuffer buffer = ByteBuffer.allocate(stringEntriesSize);
		for (Entry<String, Long> entry : entryMap) {
			byte[] stringBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
			//Array length
			buffer.put((byte) stringBytes.length);
			for (byte b : stringBytes) {
				buffer.put(b);
			}
			buffer.putLong(entry.getValue());
		}
		NBTUtils.setByteBuf("NameToUUIDMap", buffer, compound);
		return compound;
	}

}
