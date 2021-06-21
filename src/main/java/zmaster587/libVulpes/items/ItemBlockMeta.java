package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockMeta extends  ItemBlock {

	public ItemBlockMeta(Block p_i45326_1_) {
		super(p_i45326_1_);
		
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}
	

	@Override
	public int getMetadata(int damage) {
		return damage & 15;
	}
	
}
