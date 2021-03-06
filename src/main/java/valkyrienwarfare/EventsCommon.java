package valkyrienwarfare;

import valkyrienwarfare.api.RotationMatrices;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.capability.IAirshipCounterCapability;
import valkyrienwarfare.interaction.ValkyrienWarfareWorldEventListener;
import valkyrienwarfare.mixin.MixinLoaderForge;
import valkyrienwarfare.physicsmanagement.PhysicsTickHandler;
import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import valkyrienwarfare.physicsmanagement.ShipType;
import valkyrienwarfare.addon.combat.entity.EntityMountingWeaponBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;

public class EventsCommon {
	
	public static HashMap<EntityPlayerMP, Double[]> lastPositions = new HashMap<EntityPlayerMP, Double[]>();
	
	private static final Field[] getFields(Explosion toSet) {
		try {
			Field xField, yField, zField, positionField;
			
			if (!MixinLoaderForge.isObfuscatedEnvironment) {
				xField = toSet.getClass().getDeclaredField("explosionX");
				xField.setAccessible(true);
				
				yField = toSet.getClass().getDeclaredField("explosionY");
				yField.setAccessible(true);
				
				zField = toSet.getClass().getDeclaredField("explosionZ");
				zField.setAccessible(true);
				
				positionField = toSet.getClass().getDeclaredField("position");
				positionField.setAccessible(true);
			} else {
				xField = toSet.getClass().getDeclaredField("field_77284_b");
				xField.setAccessible(true);
				
				yField = toSet.getClass().getDeclaredField("field_77285_c");
				yField.setAccessible(true);
				
				zField = toSet.getClass().getDeclaredField("field_77282_d");
				zField.setAccessible(true);
				
				positionField = toSet.getClass().getDeclaredField("position");
				positionField.setAccessible(true);
			}
			return new Field[]{xField, yField, zField, positionField};
		} catch (Exception e) {
		}
		
		return null;
	}
	
	private static final boolean setExplosionPosition(Explosion toSet, double x, double y, double z, Field[] fields) {
		if (fields == null) {
			return false;
		}
		try {
			Field xField = fields[0], yField = fields[1], zField = fields[2], positionField = fields[3];
			
			double testX = toSet.explosionX;
			
			xField.setDouble(toSet, x);
			
			double testY = toSet.explosionY;
			
			yField.setDouble(toSet, y);
			
			double testZ = toSet.explosionZ;
			
			zField.setDouble(toSet, z);
			
			positionField.set(toSet, new Vec3d(x, y, z));
			
			toSet.getAffectedBlockPositions().clear();
			toSet.getPlayerKnockbackMap().clear();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@SubscribeEvent()
	public void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		BlockPos pos = event.getPos();
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(player.world, pos);
		if (wrapper != null) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				player.sendMessage(new TextComponentString("Spawn Point Set!"));
				player.setSpawnPoint(pos, true);
				event.setResult(SleepResult.NOT_POSSIBLE_HERE);
			}
		}
	}
	
	@SubscribeEvent()
	public void onRightClickBlock(RightClickBlock event) {
		if (!event.getWorld().isRemote) {
			ItemStack stack = event.getItemStack();
			if (stack != null && stack.getItem() instanceof ItemNameTag) {
				BlockPos posAt = event.getPos();
				EntityPlayer player = event.getEntityPlayer();
				World world = event.getWorld();
				PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, posAt);
				if (wrapper != null) {
					wrapper.setCustomNameTag(stack.getDisplayName());
					--stack.stackSize;
					event.setCanceled(true);
				}
			}
		}
	}
	
	//TODO: Fix conflicts with EventListener.onEntityAdded()
	//MAYBE REMOVE DUE TO CONFLICTS
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		World world = entity.world;
		BlockPos posAt = new BlockPos(entity);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, posAt);
		if (!(entity instanceof EntityFallingBlock) && wrapper != null && wrapper.wrapping.coordTransform != null) {
			if (entity instanceof EntityMountingWeaponBase || entity instanceof EntityArmorStand || entity instanceof EntityPig || entity instanceof EntityBoat) {
//				entity.startRiding(wrapper);
				wrapper.wrapping.fixEntity(entity, new Vector(entity));
				wrapper.wrapping.queueEntityForMounting(entity);
			}
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWTransform, wrapper.wrapping.coordTransform.lToWRotation, entity);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityInteractEvent(EntityInteract event) {
		event.setResult(Result.ALLOW);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTickEvent(TickEvent event) {
		if (event instanceof WorldTickEvent) {
			World worldFor = ((WorldTickEvent) event).world;
			// Only run the WorldTickEvent on Server side
			if (!worldFor.isRemote) {
				if (event.phase == Phase.START) {
					PhysicsTickHandler.onWorldTickStart(worldFor);
				}
				if (event.phase == Phase.END) {
					PhysicsTickHandler.onWorldTickEnd(worldFor);
					if (worldFor instanceof WorldServer) {
//                        addOrRemovedAllShipChunksFromMap((WorldServer) worldFor, false);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void worldTick(TickEvent.WorldTickEvent event) {
		if ((event.phase == TickEvent.Phase.END) && (!event.world.isRemote)) {
			World worldFor = event.world;
			if (worldFor instanceof WorldServer) {
//                addOrRemovedAllShipChunksFromMap((WorldServer) worldFor, true);
			}
		}
	}
	
	/**
	 * Either removes or adds all Ship Chunk entries to the World. Its a stupid fix for ChickenChunks; Blame Him For This Mess!!!
	 * Necessary to prevent ChickenChunks from trying to unload the Ship Chunks, and remove the player index in WatchingPlayers while its at it!
	 *
	 * @param worldFor
	 * @param amAdding Use true to add all chunks, false to remove all chunks
	 */
	public void addOrRemovedAllShipChunksFromMap(WorldServer worldFor, boolean amAdding) {
		for (PhysicsWrapperEntity wrapper : ValkyrienWarfareMod.physicsManager.getManagerForWorld(worldFor).physicsEntities) {
			for (Chunk[] chunks : wrapper.wrapping.claimedChunks) {
				for (Chunk chunk : chunks) {
					if (amAdding) {
//                        ((WorldServer) worldFor).getChunkProvider().id2ChunkMap.put(ChunkPos.asLong(chunk.x, chunk.z), chunk);
					} else {
//						((WorldServer)worldFor).getChunkProvider().id2ChunkMap.remove(ChunkPos.chunkXZ2Int(chunk.x, chunk.z))
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onFillBucketEvent(FillBucketEvent event) {
//		event.setResult(Result.ALLOW);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerTickEvent(PlayerTickEvent event) {
		if (!event.player.world.isRemote && event.player != null) {
			EntityPlayerMP p = (EntityPlayerMP) event.player;
			
			Double[] pos = lastPositions.get(p);
			if (pos == null) {
				pos = new Double[3];
				lastPositions.put(p, pos);
			}
			try {
				if (pos[0] != p.posX || pos[2] != p.posZ) { // Player has moved
					if (Math.abs(p.posX) > 27000000 || Math.abs(p.posZ) > 27000000) { // Player is outside of world border, tp them back
						p.attemptTeleport(pos[0], pos[1], pos[2]);
						p.sendMessage(new TextComponentString("You can't go beyond 27000000 blocks because airships are stored there!"));
					}
				}
			} catch (NullPointerException e) {
				ValkyrienWarfareMod.VWLogger.log(Level.WARNING, "Nullpointer EventsCommon.java:onPlayerTickEvent");
			}
			
			pos[0] = p.posX;
			pos[1] = p.posY;
			pos[2] = p.posZ;
		}
	}
	
	/**
	 * @SubscribeEvent(priority = EventPriority.HIGHEST)
	 * public void onExplosionDetonateEvent(ExplosionEvent.Detonate event) {
	 * Explosion e = event.getExplosion();
	 * <p>
	 * double xx = e.explosionX, yy = e.explosionY, zz = e.explosionZ;
	 * List<BlockPos> affectedPositionsList = new ArrayList<BlockPos>(e.getAffectedBlockPositions());
	 * Map<EntityPlayer, Vec3d> playerKnockbackMap = new HashMap<EntityPlayer, Vec3d>(e.getPlayerKnockbackMap());
	 * <p>
	 * Vector center = new Vector(e.explosionX, e.explosionY, e.explosionZ);
	 * World worldIn = e.worldObj;
	 * float radius = e.explosionSize;
	 * <p>
	 * AxisAlignedBB toCheck = new AxisAlignedBB(center.X - radius, center.Y - radius, center.Z - radius, center.X + radius, center.Y + radius, center.Z + radius);
	 * List<PhysicsWrapperEntity> shipsNear = ValkyrienWarfareMod.physicsManager.getManagerForWorld(e.worldObj).getNearbyPhysObjects(toCheck);
	 * // e.doExplosionA();
	 * // TODO: Make this compatible and shit!
	 * Field[] fields = getFields(e);
	 * <p>
	 * for (PhysicsWrapperEntity ship : shipsNear) {
	 * Vector inLocal = new Vector(center);
	 * RotationMatrices.applyTransform(ship.wrapping.coordTransform.wToLTransform, inLocal);
	 * // inLocal.roundToWhole();
	 * <p>
	 * // Explosion expl = new Explosion(ship.worldObj, null, inLocal.X, inLocal.Y, inLocal.Z, radius, false, false);
	 * <p>
	 * Explosion expl = e;
	 * <p>
	 * if (setExplosionPosition(e, inLocal.X, inLocal.Y, inLocal.Z, fields)) {
	 * <p>
	 * double waterRange = .6D;
	 * <p>
	 * boolean cancelDueToWater = false;
	 * <p>
	 * for (int x = (int) math.floor(expl.explosionX - waterRange); x <= math.ceil(expl.explosionX + waterRange); x++) {
	 * for (int y = (int) math.floor(expl.explosionY - waterRange); y <= math.ceil(expl.explosionY + waterRange); y++) {
	 * for (int z = (int) math.floor(expl.explosionZ - waterRange); z <= math.ceil(expl.explosionZ + waterRange); z++) {
	 * if (!cancelDueToWater) {
	 * IBlockState state = e.worldObj.getBlockState(new BlockPos(x, y, z));
	 * if (state.getBlock() instanceof BlockLiquid) {
	 * cancelDueToWater = true;
	 * }
	 * }
	 * }
	 * }
	 * }
	 * <p>
	 * expl.doExplosionA();
	 * <p>
	 * double affectedPositions = 0D;
	 * <p>
	 * for (Object o : expl.affectedBlockPositions) {
	 * BlockPos pos = (BlockPos) o;
	 * IBlockState state = ship.worldObj.getBlockState(pos);
	 * block block = state.getBlock();
	 * if (!block.isAir(state, worldIn, (BlockPos) o) || ship.wrapping.explodedPositionsThisTick.contains((BlockPos) o)) {
	 * affectedPositions++;
	 * }
	 * }
	 * <p>
	 * if (!cancelDueToWater) {
	 * for (Object o : expl.affectedBlockPositions) {
	 * BlockPos pos = (BlockPos) o;
	 * <p>
	 * IBlockState state = ship.worldObj.getBlockState(pos);
	 * block block = state.getBlock();
	 * if (!block.isAir(state, worldIn, (BlockPos) o) || ship.wrapping.explodedPositionsThisTick.contains((BlockPos) o)) {
	 * if (block.canDropFromExplosion(expl)) {
	 * block.dropBlockAsItemWithChance(ship.worldObj, pos, state, 1.0F / expl.explosionSize, 0);
	 * }
	 * block.onBlockExploded(ship.worldObj, pos, expl);
	 * if (!worldIn.isRemote) {
	 * Vector posVector = new Vector(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
	 * <p>
	 * ship.wrapping.coordTransform.fromLocalToGlobal(posVector);
	 * <p>
	 * double mass = BlockMass.basicMass.getMassFromState(state, pos, ship.worldObj);
	 * <p>
	 * double explosionForce = math.sqrt(e.explosionSize) * 1000D * mass;
	 * <p>
	 * Vector forceVector = new Vector(pos.getX() + .5 - expl.explosionX, pos.getY() + .5 - expl.explosionY, pos.getZ() + .5 - expl.explosionZ);
	 * <p>
	 * double vectorDist = forceVector.length();
	 * <p>
	 * forceVector.normalize();
	 * <p>
	 * forceVector.multiply(explosionForce / vectorDist);
	 * <p>
	 * RotationMatrices.doRotationOnly(ship.wrapping.coordTransform.lToWRotation, forceVector);
	 * <p>
	 * PhysicsQueuedForce queuedForce = new PhysicsQueuedForce(forceVector, posVector, false, 1);
	 * <p>
	 * if (!ship.wrapping.explodedPositionsThisTick.contains(pos)) {
	 * ship.wrapping.explodedPositionsThisTick.add(pos);
	 * }
	 * <p>
	 * ship.wrapping.queueForce(queuedForce);
	 * }
	 * }
	 * }
	 * }
	 * <p>
	 * }
	 * e.getAffectedBlockPositions().clear();
	 * e.getAffectedBlockPositions().addAll(affectedPositionsList);
	 * e.getPlayerKnockbackMap().clear();
	 * e.getPlayerKnockbackMap().putAll(playerKnockbackMap);
	 * <p>
	 * }
	 * }
	 **/
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		ValkyrienWarfareMod.physicsManager.initWorld(world);
		world.addEventListener(new ValkyrienWarfareWorldEventListener(world));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldUnload(WorldEvent.Unload event) {
		if (!event.getWorld().isRemote) {
			ValkyrienWarfareMod.chunkManager.removeWorld(event.getWorld());
		} else {
			//fixes memory leak; @DaPorkChop please don't leave static maps lying around D:
			lastPositions.clear();
		}
		ValkyrienWarfareMod.physicsManager.removeWorld(event.getWorld());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkNBTLoad(ChunkDataEvent.Load event) {
		NBTTagCompound data = event.getData();
		
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkNBTUnload(ChunkDataEvent.Save event) {
		NBTTagCompound data = event.getData();
		
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityUntrack(PlayerEvent.StopTracking event) {
		if (!event.getEntityPlayer().world.isRemote) {
			Entity ent = event.getTarget();
			if (ent instanceof PhysicsWrapperEntity) {
				((PhysicsWrapperEntity) ent).wrapping.onPlayerUntracking(event.getEntityPlayer());
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		BlockPos pos = event.getPos();
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(event.getWorld(), pos);
		if (wrapper != null) {
			event.setResult(Result.ALLOW);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerOpenContainerEvent(PlayerContainerEvent event) {
		event.setResult(Result.ALLOW);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBreakEvent(BreakEvent event) {
		
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHarvestDropsEvent(HarvestDropsEvent event) {
		
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHarvestCheck(HarvestCheck event) {
		
	}
	
	//Notice that this event fires for both Entities and TileEntities, so an instanceof is needed to stop weird bugs
	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent evt) {
		if (evt.getObject() instanceof EntityPlayer) {
			evt.addCapability(new ResourceLocation(ValkyrienWarfareMod.MODID, "AirshipCounter"), new ICapabilitySerializable<NBTTagIntArray>() {
				IAirshipCounterCapability inst = ValkyrienWarfareMod.airshipCounter.getDefaultInstance();
				
				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == ValkyrienWarfareMod.airshipCounter;
				}
				
				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					return capability == ValkyrienWarfareMod.airshipCounter ? ValkyrienWarfareMod.airshipCounter.<T>cast(inst) : null;
				}
				
				@Override
				public NBTTagIntArray serializeNBT() {
					return (NBTTagIntArray) ValkyrienWarfareMod.airshipCounter.getStorage().writeNBT(ValkyrienWarfareMod.airshipCounter, inst, null);
				}
				
				@Override
				public void deserializeNBT(NBTTagIntArray nbt) {
					//Otherwise its old, then ignore it
					if (nbt instanceof NBTTagIntArray) {
						ValkyrienWarfareMod.airshipCounter.getStorage().readNBT(ValkyrienWarfareMod.airshipCounter, inst, null, nbt);
					}
				}
			});
		}
	}
	
	@SubscribeEvent
	public void onJoin(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			lastPositions.put((EntityPlayerMP) event.player, new Double[]{0D, 256D, 0D});
			
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			
			if (player.getName().equals("Drake_Eldridge") || player.getDisplayName().equals("Drake_Eldridge")) {
				WorldServer server = (WorldServer) event.player.world;
				
				if (Math.random() < .01D) {
					player.setPosition(player.posX, 696969, player.posZ);
					server.mcServer.getPlayerList().sendMessage(new TextComponentString("Cheers m8!"));
				}
				
				server.mcServer.getPlayerList().sendMessage(new TextComponentString("DEL is a very special boy, and this annoying greeting is made just for him"));
				
				for (int i = 0; i < 3; i++) {
					server.mcServer.getPlayerList().sendMessage(new TextComponentString("VW Version Alpha Beta (Outdated)"));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLeave(PlayerLoggedOutEvent event) {
		if (!event.player.world.isRemote) {
			lastPositions.remove((EntityPlayerMP) event.player);
		}
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (!event.getWorld().isRemote) {
			PhysicsWrapperEntity physObj = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(event.getWorld(), event.getPos());
			if (physObj != null) {
				if (ValkyrienWarfareMod.runAirshipPermissions && !(physObj.wrapping.creator.equals(event.getEntityPlayer().entityUniqueID.toString()) || physObj.wrapping.allowedUsers.contains(event.getEntityPlayer().entityUniqueID.toString()))) {
					event.getEntityPlayer().sendMessage(new TextComponentString("You need to be added to the airship to do that!" + (physObj.wrapping.creator == null || physObj.wrapping.creator.trim().isEmpty() ? " Try using \"/airshipSettings claim\"!" : "")));
					event.setCanceled(true);
					return;
				} else {
					event.setResult(Result.ALLOW);
					event.setCanceled(false);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isRemote) {
			PhysicsWrapperEntity physObj = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(event.getWorld(), event.getPos());
			if (physObj != null) {
				if (ValkyrienWarfareMod.runAirshipPermissions && !(physObj.wrapping.creator.equals(event.getPlayer().entityUniqueID.toString()) || physObj.wrapping.allowedUsers.contains(event.getPlayer().entityUniqueID.toString()))) {
					event.getPlayer().sendMessage(new TextComponentString("You need to be added to the airship to do that!" + (physObj.wrapping.creator == null || physObj.wrapping.creator.trim().isEmpty() ? " Try using \"/airshipSettings claim\"!" : "")));
					event.setCanceled(true);
					return;
				}
				
				if (physObj.wrapping.shipType == ShipType.Oribtal) {
					//Do not let it break
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlaceEvent(PlaceEvent event) {
		PhysicsWrapperEntity physObj = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(event.getWorld(), event.getPos());
		if (physObj != null) {
			if (ValkyrienWarfareMod.runAirshipPermissions && !(physObj.wrapping.creator.equals(event.getPlayer().entityUniqueID.toString()) || physObj.wrapping.allowedUsers.contains(event.getPlayer().entityUniqueID.toString()))) {
				event.getPlayer().sendMessage(new TextComponentString("You need to be added to the airship to do that!" + (physObj.wrapping.creator == null || physObj.wrapping.creator.trim().isEmpty() ? " Try using \"/airshipSettings claim\"!" : "")));
				event.setCanceled(true);
				return;
			}
			
			if (physObj.wrapping.shipType == ShipType.Oribtal) {
				//Do not let it place any blocks
//				System.out.println("test");
				event.setCanceled(true);
			}
		}
	}
	
}
