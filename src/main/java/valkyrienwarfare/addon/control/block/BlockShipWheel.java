package valkyrienwarfare.addon.control.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockShipWheel extends Block {

	public static final PropertyInteger modelId = PropertyInteger.create("modelid", 0, 15);

	public BlockShipWheel(Material materialIn) {
		super(materialIn);
		setDefaultState(getStateFromMeta(0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{modelId});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(modelId, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(modelId);
	}

}
