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

package valkyrienwarfare.addon.cannon.world;

import valkyrienwarfare.addon.cannon.entities.EntityCannonball;
import valkyrienwarfare.addon.cannon.entities.EntityExplosiveball;
import valkyrienwarfare.addon.cannon.entities.EntityGrapeshot;
import valkyrienwarfare.addon.cannon.entities.EntitySolidball;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExplosionHandler {


	@SubscribeEvent
	public void expDet(ExplosionEvent.Detonate event) {

		for (int k2 = 0; k2 < event.getAffectedEntities().size(); ++k2) {
			Entity entity = event.getAffectedEntities().get(k2);
			if (entity instanceof EntityGrapeshot | entity instanceof EntitySolidball |
					entity instanceof EntityCannonball | entity instanceof EntityExplosiveball) {

				event.getAffectedEntities().remove(k2);
			}
		}
	}
}
