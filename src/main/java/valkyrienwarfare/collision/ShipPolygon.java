package valkyrienwarfare.collision;

import valkyrienwarfare.api.Vector;
import valkyrienwarfare.physicsmanagement.PhysicsObject;
import net.minecraft.util.math.AxisAlignedBB;

public class ShipPolygon extends Polygon {

	public Vector[] normals;
	public PhysicsObject shipFrom;

	public ShipPolygon(AxisAlignedBB bb, double[] rotationMatrix, Vector[] norms, PhysicsObject shipFor) {
		super(bb, rotationMatrix);
		normals = norms;
		shipFrom = shipFor;
	}

}