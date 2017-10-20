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

package valkyrienwarfare.math;

import valkyrienwarfare.api.RotationMatrices;
import valkyrienwarfare.api.Vector;
import valkyrienwarfare.ValkyrienWarfareMod;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

/**
 * A lot of useful math functions belong here
 *
 * @author thebest108
 */
public class BigBastardMath {

	public static final int maxArrayListFusePasses = 5;

	public static double getPitchFromVec3d(Vector vec) {
		double pitchFromRotVec = -Math.asin(vec.Y) / 0.017453292F;
		return pitchFromRotVec;
	}

	public static double getYawFromVec3d(Vector vec, double rotPitch) {
		double f2 = -Math.cos(-rotPitch * 0.017453292F);
		double yawFromRotVec = Math.atan2(vec.X / f2, vec.Z / f2);
		yawFromRotVec += Math.PI;
		yawFromRotVec /= -0.017453292F;
		return yawFromRotVec;
	}

	// Assuming they're colliding, OR ELSE!
	public static AxisAlignedBB getBetweenAABB(AxisAlignedBB ship1, AxisAlignedBB ship2) {
		if (!ship1.intersects(ship2)) {
			System.out.println("Tried getting relevent BB's for 2 ships not colliding!!!");
			return null;
		}
		final double[] xVals = new double[4];
		final double[] yVals = new double[4];
		final double[] zVals = new double[4];
		xVals[0] = ship1.minX;
		xVals[1] = ship1.maxX;
		xVals[2] = ship2.minX;
		xVals[3] = ship2.maxX;
		yVals[0] = ship1.minY;
		yVals[1] = ship1.maxY;
		yVals[2] = ship2.minY;
		yVals[3] = ship2.maxY;
		zVals[0] = ship1.minZ;
		zVals[1] = ship1.maxZ;
		zVals[2] = ship2.minZ;
		zVals[3] = ship2.maxZ;
		Arrays.sort(xVals);
		Arrays.sort(yVals);
		Arrays.sort(zVals);
		return new AxisAlignedBB(xVals[1], yVals[1], zVals[1], xVals[2], yVals[2], zVals[2]);
	}

	// Maybe update to use Arrays.sort() but that could actually be slower because I
	// only need the min and max arranged
	public static double[] getMinMaxOfArray(double[] distances) {
		double[] minMax = new double[2];
		// Min at 0
		// Max at 1
		minMax[0] = minMax[1] = distances[0];
		for (int i = 1; i < distances.length; i++) {
			if (distances[i] < minMax[0]) {
				minMax[0] = distances[i];
			}
			if (distances[i] > minMax[1]) {
				minMax[1] = distances[i];
			}
		}
		return minMax;
	}

	public static double limitToRange(double input, double minimum, double maximum) {
		return Math.max(Math.min(maximum, input), minimum);
	}

	public static Vector getBodyPosWithOrientation(BlockPos pos, Vector centerOfMass, double[] rotationTransform) {
		final Vector inBody = new Vector(pos.getX() + .5D - centerOfMass.X, pos.getY() + .5D - centerOfMass.Y, pos.getZ() + .5D - centerOfMass.Z);
		RotationMatrices.doRotationOnly(rotationTransform, inBody);
		return inBody;
	}

	public static void getBodyPosWithOrientation(BlockPos pos, Vector centerOfMass, double[] rotationTransform, Vector inBody) {
		inBody.X = pos.getX() + .5D - centerOfMass.X;
		inBody.Y = pos.getY() + .5D - centerOfMass.Y;
		inBody.Z = pos.getZ() + .5D - centerOfMass.Z;
		RotationMatrices.doRotationOnly(rotationTransform, inBody);
	}

	public static void getBodyPosWithOrientation(Vector pos, Vector centerOfMass, double[] rotationTransform, Vector inBody) {
		inBody.X = pos.X - centerOfMass.X;
		inBody.Y = pos.Y - centerOfMass.Y;
		inBody.Z = pos.Z - centerOfMass.Z;
		RotationMatrices.doRotationOnly(rotationTransform, inBody);
	}

	/**
	 * Prevents sliding when moving on small angles dictated by the tolerance set in the ValkyrianWarfareMod class
	 *
	 * @return true/false
	 */
	public static boolean canStandOnNormal(Vector normal) {
		// if(normal.Y<0){
		// return false;
		// }
		double radius = normal.X * normal.X + normal.Z * normal.Z;
		return radius < ValkyrienWarfareMod.standingTolerance;
	}

	/**
	 * Takes an arrayList of AABB's and merges them into larger AABB's
	 *
	 * @param bbs
	 * @return
	 */
	public static void mergeAABBList(List<AxisAlignedBB> toFuse) {
		boolean changed = true;
		int passes = 0;
		while (changed && passes < maxArrayListFusePasses) {
			changed = false;
			passes++;
			for (int i = 0; i < toFuse.size(); i++) {
				AxisAlignedBB bb = toFuse.get(i);
				for (int j = i + 1; j < toFuse.size(); j++) {
					AxisAlignedBB nextOne = toFuse.get(j);
					if (connected(bb, nextOne)) {
						AxisAlignedBB fused = getFusedBoundingBox(bb, nextOne);
						toFuse.remove(j);
						toFuse.remove(i);
						toFuse.add(fused);
						j = toFuse.size();
						changed = true;
					}
				}
			}
		}
	}

	public static AxisAlignedBB getFusedBoundingBox(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		double mnX = bb1.minX;
		double mnY = bb1.minY;
		double mnZ = bb1.minZ;
		double mxX = bb1.maxX;
		double mxY = bb1.maxY;
		double mxZ = bb1.maxZ;
		if (bb2.minX < mnX) {
			mnX = bb2.minX;
		}
		if (bb2.minY < mnY) {
			mnY = bb2.minY;
		}
		if (bb2.minZ < mnZ) {
			mnZ = bb2.minZ;
		}
		if (bb2.maxX > mxX) {
			mxX = bb2.maxX;
		}
		if (bb2.maxY > mxY) {
			mxY = bb2.maxY;
		}
		if (bb2.maxZ > mxZ) {
			mxZ = bb2.maxZ;
		}
		return new AxisAlignedBB(mnX, mnY, mnZ, mxX, mxY, mxZ);
	}

	public static boolean connected(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (connectedInX(bb1, bb2) || connectedInY(bb1, bb2) || connectedInZ(bb1, bb2));
	}

	public static boolean connectedInX(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (intersectInX(bb1, bb2)) && (areXAligned(bb1, bb2));
	}

	public static boolean connectedInY(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (intersectInY(bb1, bb2)) && (areYAligned(bb1, bb2));
	}

	public static boolean connectedInZ(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (intersectInZ(bb1, bb2)) && (areZAligned(bb1, bb2));
	}

	public static boolean intersectInX(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return ((bb1.maxX >= bb2.minX) && (bb1.maxX < bb2.maxX)) || ((bb1.minX > bb2.minX) && (bb1.minX <= bb2.maxX));
	}

	public static boolean intersectInY(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return ((bb1.maxY >= bb2.minY) && (bb1.maxY < bb2.maxY)) || ((bb1.minY > bb2.minY) && (bb1.minY <= bb2.maxY));
	}

	public static boolean intersectInZ(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return ((bb1.maxZ >= bb2.minZ) && (bb1.maxZ < bb2.maxZ)) || ((bb1.minZ > bb2.minZ) && (bb1.minZ <= bb2.maxZ));
	}

	public static boolean areXAligned(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (bb1.minY == bb2.minY) && (bb1.minZ == bb2.minZ) && (bb1.maxY == bb2.maxY) && (bb1.maxZ == bb2.maxZ);
	}

	public static boolean areYAligned(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (bb1.minX == bb2.minX) && (bb1.minZ == bb2.minZ) && (bb1.maxX == bb2.maxX) && (bb1.maxZ == bb2.maxZ);
	}

	public static boolean areZAligned(AxisAlignedBB bb1, AxisAlignedBB bb2) {
		return (bb1.minX == bb2.minX) && (bb1.minY == bb2.minY) && (bb1.maxX == bb2.maxX) && (bb1.maxY == bb2.maxY);
	}

}