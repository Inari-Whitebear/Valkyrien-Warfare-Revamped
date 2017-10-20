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

package valkyrienwarfare.command;

import valkyrienwarfare.interaction.ShipNameUUIDData;
import valkyrienwarfare.interaction.ShipUUIDToPosData;
import valkyrienwarfare.interaction.ShipUUIDToPosData.ShipPositionData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class AirshipMapCommand extends CommandBase {

	@Override
	public String getName() {
		return "airshipMappings";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/airshipMappings tpto <Ship Name>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String term = args[0];

		if (term.equals("tpto")) {
			String shipName = args[1];
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					shipName += " " + args[i];
				}
			}
			Entity player = sender.getCommandSenderEntity();
			World world = player.world;

			ShipNameUUIDData data = ShipNameUUIDData.get(world);

			if (data.ShipNameToLongMap.containsKey(shipName)) {
				long shipUUIDMostSig = data.ShipNameToLongMap.get(shipName);

				ShipUUIDToPosData posData = ShipUUIDToPosData.get(world);

				ShipPositionData positionData = posData.getShipPositionData(shipUUIDMostSig);

				double posX = positionData.shipPosition.X;
				double posY = positionData.shipPosition.Y;
				double posZ = positionData.shipPosition.Z;

				//Time to teleport!

				if (player instanceof EntityPlayerMP) {
					EntityPlayerMP playerMP = (EntityPlayerMP) player;

					((EntityPlayerMP) player).connection.setPlayerLocation(posX, posY, posZ, 0, 0);
				}
			}
		}

		if (term.equals("help")) {
			sender.sendMessage(new TextComponentString("tpto"));
		}
	}

}
