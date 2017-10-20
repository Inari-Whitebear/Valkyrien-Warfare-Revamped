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

package valkyrienwarfare.fixes;

import valkyrienwarfare.api.Vector;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class SoundFixWrapper implements ISound {

	final Vector soundLocation;
	private final ISound wrappedSound;
	private final PhysicsWrapperEntity wrapper;

	public SoundFixWrapper(ISound wrappedSound, PhysicsWrapperEntity wrapper, Vector soundLocation) {
		this.wrappedSound = wrappedSound;
		this.wrapper = wrapper;
		this.soundLocation = soundLocation;
	}

	@Override
	public ResourceLocation getSoundLocation() {
		return wrappedSound.getSoundLocation();
	}

	@Override
	public SoundEventAccessor createAccessor(SoundHandler handler) {
		return wrappedSound.createAccessor(handler);
	}

	@Override
	public Sound getSound() {
		return wrappedSound.getSound();
	}

	@Override
	public SoundCategory getCategory() {
		return wrappedSound.getCategory();
	}

	@Override
	public boolean canRepeat() {
		return wrappedSound.canRepeat();
	}

	@Override
	public int getRepeatDelay() {
		return wrappedSound.getRepeatDelay();
	}

	@Override
	public float getVolume() {
		return wrappedSound.getVolume();
	}

	@Override
	public float getPitch() {
		return wrappedSound.getPitch();
	}

	@Override
	public float getXPosF() {
		return (float) soundLocation.X;
	}

	@Override
	public float getYPosF() {
		return (float) soundLocation.Y;
	}

	@Override
	public float getZPosF() {
		return (float) soundLocation.Z;
	}

	@Override
	public AttenuationType getAttenuationType() {
		return wrappedSound.getAttenuationType();
	}

}