package zmaster587.libVulpes.api;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.client.ResourceIcon;

import javax.annotation.Nonnull;
import java.util.List;

public interface IArmorComponent {
	
	/**
	 * Called on Armor tick for each IArmorComponent in IModularArmor
	 * @param world the world
	 * @param player the player wearing the armor
	 * @param armorStack the ItemStack representing the piece of armor being worn
	 * @param modules Inventory of the armorStack containing all modules inlcuding the current one
	 * @param componentStack the ItemStack representing the current component being ticked
	 */
	void onTick(World world, EntityPlayer player, @Nonnull ItemStack armorStack, IInventory modules, @Nonnull ItemStack componentStack);
	
	/**
	 * Called right before adding a component to the armor
	 * @param world
	 * @param armorStack the armor itemStack to add the component to
	 * @return true if it should be added false otherwise
	 */
	boolean onComponentAdded(World world, @Nonnull ItemStack armorStack);
	
	void onComponentRemoved(World world, @Nonnull ItemStack armorStack);
	
	void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack, @Nonnull ItemStack componentStack, DamageSource source, int damage);
	
	boolean isAllowedInSlot(@Nonnull ItemStack componentStack, EntityEquipmentSlot armorType);

	@SideOnly(Side.CLIENT)
	void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules,
					  RenderGameOverlayEvent event, Gui gui);
	
	/**
	 * @param armorStack
	 * @return The Icon for the HUD, ItemStack.EMPTY renders standard item
	 */
	@SideOnly(Side.CLIENT)
	ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack);


}