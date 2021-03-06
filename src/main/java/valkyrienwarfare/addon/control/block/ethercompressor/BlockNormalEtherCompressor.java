package valkyrienwarfare.addon.control.block.ethercompressor;

import valkyrienwarfare.addon.control.tileentity.TileEntityNormalEtherCompressor;
import valkyrienwarfare.api.block.ethercompressor.BlockEtherCompressorLore;
import valkyrienwarfare.api.Vector;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNormalEtherCompressor extends BlockEtherCompressorLore {

	public BlockNormalEtherCompressor(Material materialIn, double enginePower) {
		super(materialIn, enginePower);
	}

	@Override
	public String getEnginePowerTooltip() {
		return String.valueOf(this.enginePower);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityNormalEtherCompressor(new Vector(0D, 1.0D, 0.0D), this.enginePower);
	}
}
