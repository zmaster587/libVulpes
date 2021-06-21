package zmaster587.libVulpes.inventory.modules;

import java.util.List;

import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.util.BlockDirectionFunction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModuleBlockSideSelector extends ModuleBase implements IButtonInventory {

	private IGuiCallback callback;
	private ModuleButton[] buttons;
	private BlockDirectionFunction bdf;
	private String[] text;

	private static final String[] directions = {"Bottom: ", "Top: ", "North: ", "South ", "West: ", "East: "};
	private static final int[] colors = {0x00DD00, 0xDD0000, 0x0000DD, 0xDDDD00, 0xDD00DD, 0x00DDDD};

	public ModuleBlockSideSelector(int offsetX, int offsetY, IGuiCallback callback, String ... stateNames) {
		super(offsetX, offsetY);
		this.callback = callback;
		bdf = new BlockDirectionFunction(stateNames.length);
		text = stateNames;

		buttons = new ModuleButton[] {new ModuleButton(offsetX + 42, offsetY + 42, 0, "", this, TextureResources.buttonSquare, directions[0] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY + 21, 1, "", this, TextureResources.buttonSquare, directions[1] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY, 2, "", this, TextureResources.buttonSquare, directions[2] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 21, offsetY + 42, 3, "", this, TextureResources.buttonSquare, directions[3] + stateNames[0], 16, 16),
				new ModuleButton(offsetX, offsetY + 21, 4, "", this, TextureResources.buttonSquare, directions[4] + stateNames[0], 16, 16),
				new ModuleButton(offsetX + 42, offsetY + 21, 5, "", this, TextureResources.buttonSquare, directions[5] + stateNames[0], 16, 16)};

		if(FMLCommonHandler.instance().getSide().isClient())
			for(ModuleButton button : buttons)
				button.setBGColor(colors[0]);
	}

	@Override
	public void actionPerform(GuiButton button) {
		for(ModuleButton button2 : buttons)
			button2.actionPerform(button);
	}

	@Override
	public List<GuiButton> addButtons(int x, int y) {

		List<GuiButton> list = super.addButtons(x, y);
		for(int i = 0; i < 6; i++)
			list.addAll(buttons[i].addButtons(x, y));
		return list;
	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);

		for(ModuleBase button : buttons)
			button.renderBackground(gui, x, y, mouseX, mouseY, font);
	}

	@Override
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);
		for(ModuleBase button : buttons)
			button.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
					font);
	}

	public int getStateForSide(EnumFacing side) {
		return getStateForSide(side.ordinal());
	}

	public int getStateForSide(int side) {
		return bdf.getState(side);
	}

	public void setStateForSide(EnumFacing side, int state) {
		setStateForSide(side.ordinal(), state);
	}

	public void setStateForSide(int sideNum, int state) {
		bdf.setState(sideNum, state);
		if(FMLCommonHandler.instance().getSide().isClient()) {
			buttons[sideNum].setBGColor(colors[bdf.getState(sideNum) % colors.length]);
			buttons[sideNum].setToolTipText(directions[sideNum] + text[bdf.getState(sideNum)]);
		}

	}

	public void writeToNBT(NBTTagCompound nbt) {
		bdf.writeToNBT(nbt);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		bdf.readFromNBT(nbt);

		if(FMLCommonHandler.instance().getSide().isClient()) 
			for(int i = 0 ; i < buttons.length; i++) {
				buttons[i].setBGColor(colors[bdf.getState(i) % colors.length]);
				buttons[i].setToolTipText(directions[i] + text[bdf.getState(i)]);
			}

	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		bdf.advanceState(buttonId);
		buttons[buttonId].setBGColor(colors[bdf.getState(buttonId) % colors.length]);
		buttons[buttonId].setToolTipText(directions[buttonId] + text[bdf.getState(buttonId)]);

		callback.onModuleUpdated(this);
	}

}
