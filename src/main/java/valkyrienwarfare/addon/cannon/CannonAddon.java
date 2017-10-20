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

package valkyrienwarfare.addon.cannon;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import valkyrienwarfare.addon.cannon.init.ModBlocks;
import valkyrienwarfare.addon.cannon.init.ModItems;
import valkyrienwarfare.addon.cannon.proxy.ClientProxy;
import valkyrienwarfare.addon.cannon.proxy.ServerProxy;
import valkyrienwarfare.addon.cannon.tileentity.TileEntityCannon;
import valkyrienwarfare.addon.cannon.world.ExplosionHandler;
import valkyrienwarfare.api.addons.Module;
import valkyrienwarfare.api.addons.VWAddon;

@VWAddon
public class CannonAddon extends Module<CannonAddon> {
	public static CannonAddon INSTANCE;
	
	public CannonAddon() {
		super("Cannons!", new ClientProxy(), null, new ServerProxy());
		INSTANCE = this;
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.register(event);
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		ModItems.register(event);
		ModBlocks.registerItemBlocks(event);
	}
	
	@Override
	public void preInit(FMLStateEvent event) {
		ModItems.init();
		ModBlocks.init();
	}
	
	@Override
	public void init(FMLStateEvent event) {
		MinecraftForge.EVENT_BUS.register(new ExplosionHandler());
		
		//Craft
		
		GameRegistry.registerTileEntity(TileEntityCannon.class, CannonModReference.MOD_ID + "TileEntityCannon");
	}
	
	@Override
	public void postInit(FMLStateEvent event) {
		
	}
}
