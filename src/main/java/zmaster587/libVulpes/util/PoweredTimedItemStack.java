package zmaster587.libVulpes.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PoweredTimedItemStack {
	ItemStack storedItem;
	int storedPower, storedTime;
	public PoweredTimedItemStack(@Nonnull ItemStack item, int power, int time) {
		storedItem = item;
		storedPower = power;
		storedTime = time;
	}

	@Nonnull
	public ItemStack getItemStack() { return storedItem; }
	public int getTime() { return storedTime;}
	public int getPower() { return storedPower; }
}
