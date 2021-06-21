package zmaster587.libVulpes.block;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import zmaster587.libVulpes.api.ITimeModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockMotor extends RotatableBlock implements ITimeModifier {
	
	float timeModifier;
	
	public BlockMotor(Material par2Material, float timeMultiplier) {
		super(par2Material);
		timeModifier = timeMultiplier;
	}
	
	@Override
	public float getTimeMult() {
		return timeModifier;
	}
	
	 @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	 
	 @Override
	public void addInformation(@Nonnull ItemStack stack, World player,
							   List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		
		tooltip.add(String.format(ChatFormatting.GRAY + "Machine Speed: %.2f", 1/getTimeMult()));
	}
	
	@Override
	@Nonnull
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
			World world, BlockPos pos, EntityPlayer player) {
		return super.getPickBlock(state.getBlock().getDefaultState(), target, world, pos, player);
	}
}
