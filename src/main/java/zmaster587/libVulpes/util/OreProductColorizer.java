package zmaster587.libVulpes.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.items.ItemOreProduct;

import javax.annotation.Nonnull;

public class OreProductColorizer   implements IItemColor, IBlockColor  {
	@Override
	public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
		if(stack.getItem() instanceof ItemOreProduct)
			return ((ItemOreProduct)stack.getItem()).properties.get(stack.getMetadata()).getColor();
		else 
			return colorMultiplier(Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getItemDamage()), null, null, 0);
	}

	//From blockOres
	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn,
			BlockPos pos, int tintIndex) {

		int meta = state.getBlock().getMetaFromState(state);
		if(state.getBlock() instanceof BlockOre) {
			Material mat = ((BlockOre)state.getBlock()).ores[meta];
			if(mat != null)
				return mat.getColor();
		}
		return 0xFFFFFF;
	}

	/*@Override
	public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
		return ((BlockOre)Block.getBlockFromItem(stack.getItem())).ores[stack.getMetadata()].getColor();
	}*/
}
