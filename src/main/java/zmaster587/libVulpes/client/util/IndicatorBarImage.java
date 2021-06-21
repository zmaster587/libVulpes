package zmaster587.libVulpes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
/**
 * Used to display an arrow or some other image moving along a bar
 *
 */
public class IndicatorBarImage extends ProgressBarImage {

	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight, int insetX, int insetY, EnumFacing direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, insetX, insetY, direction,image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight,EnumFacing direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, 0, 0, direction, image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, EnumFacing direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, backWidth, backHeight, 0, 0, direction, image);
	}
	
	@Override
	public void renderProgressBar(int x, int y, float percent, Gui gui) {
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		
		gui.drawTexturedModalRect(x, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		int xProgress, yProgress;
		
		if(direction == EnumFacing.WEST)
			xProgress = (int) (backWidth - insetX - ( ( backWidth - ( insetX*2 ) )*percent)) - foreHeight/2;
		else if(direction == EnumFacing.EAST)
			xProgress = (int) (insetX - foreWidth + ( ( backWidth - ( insetX*2 ) )*percent));
		else
			xProgress = insetX;
		
		if(direction == EnumFacing.UP)
			yProgress = (int) (backHeight - insetY - ( ( backHeight - ( insetY*2 ) )*percent)) - foreHeight/2;
		else if(direction == EnumFacing.DOWN)
			yProgress = (int) (insetY + ( ( backHeight - ( insetY*2 ) )*percent)) + foreHeight/2;
		else
			yProgress = insetY;
		
		gui.drawTexturedModalRect(x + xProgress, y + yProgress, foreOffsetX, foreOffsetY, foreWidth, foreHeight);
	}
}
