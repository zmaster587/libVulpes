package zmaster587.libVulpes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Displays a image rendered on top of a backdrop, amount of the image showing is equal to the progress
 *
 */
public class ProgressBarImage {
	protected ResourceLocation image;
	protected int backOffsetX, backOffsetY, foreOffsetX, foreOffsetY, backWidth, backHeight, foreWidth, foreHeight, insetX, insetY;
	
	EnumFacing direction;
	
	/**
	 * @param backOffsetX X Offset of the background on image
	 * @param backOffsetY Y Offset of the background on image
	 * @param backWidth The width of the background to draw
	 * @param backHeight The height of the background to draw
	 * @param foreOffsetX The X Offset foreground image
	 * @param foreOffsetY The Y Offset foreground image
	 * @param foreWidth The maximum width of the progress indicator
	 * @param foreHeight The maximum height of the progress indicator
	 * @param insetX the amount of X offset the progress bar has from the background
	 * @param insetY the amount of Y offset the progress bar has from the background
	 * @param direction the direction to which the progress bar fills
	 * @param image the resource location to pull the texture from
	 */
	public ProgressBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight, int insetX, int insetY, EnumFacing direction, ResourceLocation image) {
		this.backOffsetX = backOffsetX;
		this.backOffsetY = backOffsetY;
		this.backWidth = backWidth;
		this.backHeight = backHeight;
		
		this.foreOffsetX = foreOffsetX;
		this.foreOffsetY = foreOffsetY;
		this.foreHeight = foreHeight;
		this.foreWidth = foreWidth;
		
		this.insetX = insetX;
		this.insetY = insetY;
		
		this.direction = direction;
		
		this.image = image;
	}
	
	public ProgressBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight,EnumFacing direction, ResourceLocation image) {
		this(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, 0, 0, direction, image);
	}
	
	public ProgressBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, EnumFacing direction, ResourceLocation image) {
		this(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, backWidth, backHeight, 0, 0, direction, image);
	}
	
	public ResourceLocation getResourceLocation() { return image; } 
	public int getBackOffsetX() { return backOffsetX; }
	public int getBackOffsetY() { return backOffsetY; }
	public int getForeOffsetX() { return foreOffsetX; }
	public int getForeOffsetY() { return foreOffsetY; }
	public int getBackWidth() { return backWidth; }
	public int getBackHeight() { return backHeight; }
	public int getForeWidth() { return foreWidth; }
	public int getForeHeight() { return foreHeight; }
	public int getInsetX() { return insetX; }
	public int getInsetY() { return insetY; }
	public EnumFacing getDirection() { return direction; }
	
	@SideOnly(Side.CLIENT)
	public void renderProgressBarPartial(int x, int y, float center, float variation, net.minecraft.client.gui.Gui gui) {
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		gui.drawTexturedModalRect(x, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		if(center - variation/2 < 0) {
			float change = center - (variation/2f);
			
			center -= change/2f;
			variation += change;
		}
		
		if(center + variation/2 > 1) {
			float change = 1 - center - (variation/2f);
			
			center += change/2f;
			variation -= change;
		}
		
		
		if(direction == EnumFacing.EAST )//Left to right
			gui.drawTexturedModalRect(x + insetX + (int)(foreWidth*(1 - center - variation/2f)), y + insetY, foreOffsetX + (int)((1-variation/2-center)*foreWidth), foreOffsetY, (int)(variation*foreWidth), foreHeight);
		//else if(direction == ForgeDirection.WEST)
		else if(direction == EnumFacing.UP) // bottom to top
			gui.drawTexturedModalRect(x + insetX, y + insetY + foreHeight - (int)(variation*foreHeight), foreOffsetX, foreOffsetY + foreHeight - (int)(variation*foreHeight), foreWidth, (int)(variation*foreHeight) );
		else if(direction == EnumFacing.DOWN)
			gui.drawTexturedModalRect(x + insetX, y + insetY, foreOffsetX, foreOffsetY, foreWidth, (int)(variation*foreHeight) );
	
	}
	
	/**
	 * 
	 * @param x X location to render the progress bar
	 * @param y Y location to render the progress bar
	 * @param percent percent to fill
	 */
	@SideOnly(Side.CLIENT)
	public void renderProgressBar(int x, int y, float percent, net.minecraft.client.gui.Gui gui) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		//backdrop
		gui.drawTexturedModalRect(x, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		if(direction == EnumFacing.EAST )//Left to right
			gui.drawTexturedModalRect(x + insetX, y + insetY, foreOffsetX, foreOffsetY, (int)(percent*foreWidth), foreHeight);
		else if(direction == EnumFacing.WEST ) 
			gui.drawTexturedModalRect(x + insetX + foreWidth - (int)(percent*foreWidth), y + insetY, foreOffsetX + foreWidth - (int)(percent*foreWidth), foreOffsetY, (int)(percent*foreWidth), foreHeight);
		else if(direction == EnumFacing.UP) // bottom to top
			gui.drawTexturedModalRect(x + insetX, y + insetY + foreHeight - (int)(percent*foreHeight), foreOffsetX, foreOffsetY + foreHeight - (int)(percent*foreHeight), foreWidth, (int)(percent*foreHeight) );
		else if(direction == EnumFacing.DOWN)
			gui.drawTexturedModalRect(x + insetX, y + insetY, foreOffsetX, foreOffsetY, foreWidth, (int)(percent*foreHeight) );
	}
	
	@SideOnly(Side.CLIENT)
	public void renderProgressBar(int x, int zLevel, int y, float percent) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		//backdrop
		drawTexturedModalRect(x, zLevel, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		if(direction == EnumFacing.EAST )//Left to right
			drawTexturedModalRect(x + insetX,zLevel, y + insetY, foreOffsetX, foreOffsetY, (int)(percent*foreWidth), foreHeight);
		else if(direction == EnumFacing.WEST ) 
			drawTexturedModalRect(x + insetX + foreWidth - (int)(percent*foreWidth),zLevel, y + insetY, foreOffsetX + foreWidth - (int)(percent*foreWidth), foreOffsetY, (int)(percent*foreWidth), foreHeight);
		else if(direction == EnumFacing.UP) // bottom to top
			drawTexturedModalRect(x + insetX,zLevel, y + insetY + foreHeight - (int)(percent*foreHeight), foreOffsetX, foreOffsetY + foreHeight - (int)(percent*foreHeight), foreWidth, (int)(percent*foreHeight) );
		else if(direction == EnumFacing.DOWN)
			drawTexturedModalRect(x + insetX,zLevel, y + insetY, foreOffsetX, foreOffsetY, foreWidth, (int)(percent*foreHeight) );
	}
	
	@SideOnly(Side.CLIENT)
    public void drawTexturedModalRect(int x, int zLevel, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x, y + height, zLevel).tex((float)(textureX) * 0.00390625F, (float)(textureY + height) * 0.00390625F).endVertex();
        vertexbuffer.pos(x + width, y + height, zLevel).tex((float)(textureX + width) * 0.00390625F, (float)(textureY + height) * 0.00390625F).endVertex();
        vertexbuffer.pos(x + width, y, zLevel).tex((float)(textureX + width) * 0.00390625F, (float)(textureY) * 0.00390625F).endVertex();
        vertexbuffer.pos(x, y, zLevel).tex((float)(textureX) * 0.00390625F, (float)(textureY) * 0.00390625F).endVertex();
        tessellator.draw();
    }
	
}
