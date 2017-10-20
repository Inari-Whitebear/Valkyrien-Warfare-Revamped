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

package valkyrienwarfare.api.addons;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class Module<ImplName> {
	private String name;
	private boolean registeredStuffPreInit = false, registeredStuffInit = false;
	private ModuleProxy common, client, server;
	
	public Module(String name, ModuleProxy common, ModuleProxy client, ModuleProxy server)  {
		this.name = name;
		this.common = common;
		this.client = client;
		this.server = server;
	}
	
	public final void doRegisteringStuffPreInit()  {
		if (!registeredStuffPreInit)   {
			registerEntities();
			registerCapabilities();
			registeredStuffPreInit = true;
		}
	}
	
	public final void doRegisteringStuffInit()  {
		if (!registeredStuffInit)   {
			registerTileEntities();
			registerNetworks();
			registerRecipes();
			registeredStuffInit = true;
		}
	}
	
	public void registerItems(RegistryEvent.Register<Item> event)  {
		
	}
	
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		
	}
	
	public void registerRecipes()    {
		
	}
	
	protected void registerEntities()   {
		
	}
	
	protected void registerTileEntities()   {
		
	}
	
	protected void registerNetworks()   {
		
	}
	
	protected void registerCapabilities()   {
		
	}
	
	public final ModuleProxy getClientProxy()   {
		return client;
	}
	
	public final ModuleProxy getServerProxy()   {
		return server;
	}
	
	public final ModuleProxy getCommonProxy()   {
		return common;
	}
	
	public abstract void preInit(FMLStateEvent event);
	
	public abstract void init(FMLStateEvent event);
	
	public abstract void postInit(FMLStateEvent event);
	
	public String getModID()    {
		return ValkyrienWarfareMod.MODID;
	}
}
