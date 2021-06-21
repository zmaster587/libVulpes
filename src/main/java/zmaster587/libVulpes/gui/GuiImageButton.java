package zmaster587.libVulpes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

public class GuiImageButton extends GuiButton {

	protected ResourceLocation[] buttonTexture;
	private String soundString;
	private int bgColor;
	/**
	 * 
	 * @param id id of the button
	 * @param x x position of the button
	 * @param y y position of the button
	 * @param width width of the button in pixels
	 * @param height height of the button in pixels
	 * @param location index 0: default, index 1: hover, index 2: pressed, index 3: disabled
	 */
	public GuiImageButton(int id, int x, int y, int width, int height, ResourceLocation[] location) {
		super(id, x, y, width, height,"");
		buttonTexture = location;
		soundString = "";
		bgColor = 0xFFFFFFFF;
		//TODO: add exception
	}

	public void setButtonTexture(ResourceLocation[] buttonTexture) {
		this.buttonTexture = buttonTexture;
	}


	public void setSound(String str) {
		soundString = str;
	}
	
	public void setBackgroundColor(int color) {
		bgColor = color;
	}

	@Override
	public void playPressSound(SoundHandler sound) {
		if(soundString.isEmpty())
			super.playPressSound(sound);
		else
			sound.playSound(PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation("advancedrocketry:" + soundString)), 1.0F));
	}

	@Override
	public void drawButton(Minecraft minecraft, int par2, int par3, float f1)
	{
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			int hoverState = this.getHoverState(this.hovered);

			/*if(mousePressed(minecraft, par2, par3) && buttonTexture[2] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[2]);*/
			if(buttonTexture.length > 1 && hoverState == 2 && buttonTexture[1] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[1]);
			else if(buttonTexture.length > 2 && hoverState == 4 && buttonTexture[3] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[3]);
			else
				minecraft.getTextureManager().bindTexture(buttonTexture[0]);

			
			
			GL11.glColor4ub((byte)(bgColor & 0xFF), (byte)((bgColor >>> 8) & 0xFF), (byte)((bgColor >>> 16) & 0xFF), (byte)0xFF);


			//Draw the button...each button should contain 3 images default state, hover, and pressed

			GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
           
			
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        vertexbuffer.pos(x, y + height, this.zLevel).tex(0, 1).endVertex();
	        vertexbuffer.pos(x + width, y + height, this.zLevel).tex( 1, 1).endVertex();
	        vertexbuffer.pos(x + width, y, this.zLevel).tex(1, 0).endVertex();
	        vertexbuffer.pos(x, y, this.zLevel).tex(0, 0).endVertex();
	        tessellator.draw();
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.disableBlend();
			this.mouseDragged(minecraft, par2, par3);
		}
	}
}
