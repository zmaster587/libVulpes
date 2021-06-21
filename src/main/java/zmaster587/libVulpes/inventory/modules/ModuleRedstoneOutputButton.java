package zmaster587.libVulpes.inventory.modules;

import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleRedstoneOutputButton extends ModuleButton {

	RedstoneState state;
	
	String suppText;

	public ModuleRedstoneOutputButton(int offsetX, int offsetY, int buttonId,
			String text, IButtonInventory tile) {
		super(offsetX, offsetY, buttonId, text, tile, TextureResources.buttonRedstoneActive, 24 ,24);
		sizeX=24;
		sizeY=24;
		state = RedstoneState.ON;
		suppText = "";
	}
	
	public ModuleRedstoneOutputButton(int offsetX, int offsetY, int buttonId,
			String text, IButtonInventory tile, String text2) {
		this(offsetX, offsetY, buttonId,
			text, tile);
		suppText = text2;
	}

	public RedstoneState getState() {
		return state;
	}

	public void setRedstoneState(int i) {
		state = RedstoneState.values()[i];
	}

	public void setRedstoneState(RedstoneState i) {
		state = i;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		if(state != null)

			switch(state) {
			case ON:
				button.setButtonTexture(TextureResources.buttonRedstoneActive);
				tooltipText = suppText + "Redstone control normal";
				break;
			case OFF:
				button.setButtonTexture(TextureResources.buttonRedstoneDisabled);
				tooltipText = suppText + "Redstone control disabled";
				break;
			case INVERTED:
				button.setButtonTexture(TextureResources.buttonRedstoneInverted);
				tooltipText = suppText + "Redstone control inverted";
			}

		super.renderBackground(gui, x, y, mouseX, mouseY, font);
	}

	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {
		if(enabled && button == this.button) {
			if(state == null)
				state = RedstoneState.ON;
			state = state.getNext();
			tile.onInventoryButtonPressed(buttonId);
		}
	}
}
