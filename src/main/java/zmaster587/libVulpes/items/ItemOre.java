package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.block.INamedMetaBlock;

import javax.annotation.Nonnull;
import java.util.Locale;

public class ItemOre extends ItemBlock {

	public ItemOre(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack) {
		return ((INamedMetaBlock)this.getBlock()).getUnlocalizedName(stack.getItemDamage());
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
	
	@Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
		String translate = "tile." + this.getUnlocalizedNameInefficiently(stack).substring(9) + "." + ((BlockOre)this.getBlock()).getProduct().name().toLowerCase(Locale.ENGLISH) + ".name";
		if(I18n.canTranslate(translate))
			return I18n.translateToLocal(translate);
		return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name") + " " + I18n.translateToLocal("type." + ((BlockOre)this.getBlock()).getProduct().name().toLowerCase(Locale.ENGLISH) + ".name")).trim();
    }
}
