package valkyrienwarfare.addon.control.piloting;

import valkyrienwarfare.physicsmanagement.PhysicsWrapperEntity;
import net.minecraft.util.math.BlockPos;

public interface IShipPilot {

	PhysicsWrapperEntity getPilotedShip();

	void setPilotedShip(PhysicsWrapperEntity wrapper);

	boolean isPilotingShip();

	boolean isPilotingATile();

	boolean isPiloting();

	BlockPos getPosBeingControlled();

	void setPosBeingControlled(BlockPos pos);

	ControllerInputType getControllerInputEnum();

	void setControllerInputEnum(ControllerInputType type);

	void stopPilotingEverything();
}
