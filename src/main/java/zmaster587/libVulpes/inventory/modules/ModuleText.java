package zmaster587.libVulpes.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleText extends ModuleBase {

	List<String> text;
	int color;
	boolean centered;
	float scale;
	boolean alwaysOnTop;

	public ModuleText(int offsetX, int offsetY, String text, int color) {
		super(offsetX, offsetY);

		this.text = new ArrayList<>();
		scale = 1f;
		setText(text);
		this.color = color;
		centered = false;
		alwaysOnTop = false;
	}

	public ModuleText(int offsetX, int offsetY, String text, int color, float scale) {
		this(offsetX, offsetY, text, color);
		this.scale = scale;
	}

	public ModuleText(int offsetX, int offsetY, String text, int color, boolean centered) {
		this(offsetX, offsetY, text, color);
		this.centered = centered;
		scale = 1f;
	}

	public void setText(String text) {

		this.text.clear();
		this.text.addAll(Arrays.asList(text.split("\\n")));
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public String getText() {

		StringBuilder str = new StringBuilder();

		for(String str2 : this.text) {
			str.append("\n").append(str2);
		}

		return str.substring(1);
	}

	@Override
	public void renderForeground(int x, int y, int mouseX,
			int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {

	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {

		GL11.glPushMatrix();
		if(alwaysOnTop)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glScalef(scale, scale, scale);
		for(int i = 0; i < text.size(); i++) {
			if(centered)
				font.drawString(text.get(i), (x + offsetX - (font.getStringWidth(text.get(i))/2)), y + offsetY + i*font.FONT_HEIGHT, color);
			else
				font.drawString(text.get(i),(int)((x + offsetX)/scale), (int)((y + offsetY + i*font.FONT_HEIGHT)/scale), color);
		}
		GlStateManager.color(1f, 1f, 1f);
		
		if(alwaysOnTop)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}
}
