package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import zmaster587.libVulpes.api.material.AllowedProducts;

public class BlockOre extends Block implements INamedMetaBlock {
	
	public zmaster587.libVulpes.api.material.Material[] ores = new zmaster587.libVulpes.api.material.Material[16];
	public static final PropertyInteger VARIANT = PropertyInteger.create("varient", 0, 15);
	public byte numBlocks;
	public AllowedProducts product;

	public BlockOre(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT,0));
	}
	
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {

		return this.getDefaultState().withProperty(VARIANT, meta);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(VARIANT);
    }
    
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
    }
    
	public AllowedProducts getProduct() {
		return product;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab,
			NonNullList<ItemStack> list) {
		for(int i = 0; i < numBlocks; i++)
			if(product.isOfType(ores[i].getAllowedProducts()))
				list.add(new ItemStack(this, 1, i));
	}
	

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT);
	}

	@Override
	public String getUnlocalizedName(int itemDamage) {
		zmaster587.libVulpes.api.material.Material mat = ores[itemDamage];
		if(mat != null)
			return  "material." + mat.getUnlocalizedName();
		return "INVALID BLOCK";
	}
}
