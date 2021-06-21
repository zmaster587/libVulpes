package zmaster587.libVulpes.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackMapping {
	private ItemStack stack;
	public ItemStackMapping(@Nonnull ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public String toString() {
		return stack.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemStack)
			return stack.isItemEqual((ItemStack) obj);
		return super.equals(obj);
	}
}