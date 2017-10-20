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

package valkyrienwarfare;

import valkyrienwarfare.command.AirshipMapCommand;
import valkyrienwarfare.command.AirshipSettingsCommand;
import valkyrienwarfare.command.PhysSettingsCommand;
import valkyrienwarfare.command.ValkyrienWarfareHelpCommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

public class ModCommands {
	
	// There's some Strange bug with registering commands in the Mod File (The client loading server classes, and then freaking out). Best to just do them all in
	// a separate class
	public static void registerCommands(MinecraftServer server) {
		ServerCommandManager manager = (ServerCommandManager) server.getCommandManager();
		manager.registerCommand(new PhysSettingsCommand());
		manager.registerCommand(new AirshipSettingsCommand());
		manager.registerCommand(new AirshipMapCommand());
		manager.registerCommand(new ValkyrienWarfareHelpCommand());
	}
}
