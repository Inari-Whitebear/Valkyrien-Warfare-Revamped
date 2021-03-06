package valkyrienwarfare.addon.control.block.engine;

import valkyrienwarfare.api.block.engine.BlockAirshipEngineLore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneEngine extends BlockAirshipEngineLore {

	public BlockRedstoneEngine(Material materialIn, double powerMultiplier) {
		super(materialIn, powerMultiplier);
	}

	@Override
	public double getEnginePower(World world, BlockPos pos, IBlockState state, Entity shipEntity) {
		return world.isBlockIndirectlyGettingPowered(pos) * this.enginePower;
	}

	@Override
	public String getEnginePowerTooltip() {
		return enginePower + " * redstone power level";
	}

}
