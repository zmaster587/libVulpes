package zmaster587.libVulpes.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.inventory.GuiModular;

import java.util.List;

public class ModuleTab extends ModuleBase implements IButtonInventory {

	private int tab;
	IGuiCallback gameObject;
	private ModuleButton[] buttons;
	
	public ModuleTab(int offsetX, int offsetY,int startingId, IGuiCallback object, int numTabs, String[] tabText, ResourceLocation[][] textures) {
		super(offsetX, offsetY);
		gameObject = object;
		
		buttons = new ModuleButton[numTabs];
		
		for(int i = 0; i  < numTabs; i++) {
			buttons[i] = new ModuleButton(offsetX + i*24, offsetY - 20, i, "", this, textures[i], tabText[i], 24,24);
		}
		sizeX = 24;
		sizeY = 24;
	}
	
	
	public void setTab(int tabNum) { tab = tabNum; }
	
	public int getTab() { return tab; }
	
	@Override
	public void onMouseClicked(GuiModular gui, int x, int y, int button) {
		super.onMouseClicked(gui, x, y, button);
		
		for(ModuleButton button2 : buttons)
			button2.onMouseClicked(gui, x, y, button);
	}
	
	@Override
	public List<GuiButton> addButtons(int x, int y) {
		List<GuiButton> list = super.addButtons(x, y);
		
		for(ModuleButton button2 : buttons) list.addAll(button2.addButtons(x, y));
		
		return list;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {
		super.actionPerform(button);
		
		for(ModuleButton button2 : buttons)
			button2.actionPerform(button);
	}
	
	@Override
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);
		
		for(ModuleButton button2 : buttons)
			button2.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
					font);
		
	}
	
	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);
		
		for(ModuleButton button2 : buttons)
			button2.renderBackground(gui, x, y, mouseX, mouseY, font);
	}


	@Override
	public void onInventoryButtonPressed(int buttonId) {
		tab = buttonId;
		gameObject.onModuleUpdated(this);
	}

}
