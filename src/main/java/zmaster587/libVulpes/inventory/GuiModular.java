package zmaster587.libVulpes.inventory;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GuiModular extends GuiContainer {

	List<ModuleBase> modules;
	String unlocalizedName;
	boolean hasSlots;

	public GuiModular(EntityPlayer playerInv, List<ModuleBase> modules, IModularInventory modularInv, boolean includePlayerInv, boolean includeHotBar, String name) {
		super(new ContainerModular(playerInv, modules,modularInv, includePlayerInv, includeHotBar));
		this.modules = modules;
		unlocalizedName = name;
		hasSlots = includePlayerInv;
	}

	@Override
	public void initGui() {
		super.initGui();

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		for(ModuleBase module : modules) {
			List<GuiButton> buttonList = module.addButtons(x, y);
			if(!buttonList.isEmpty()) {
				this.buttonList.addAll(buttonList);
			}
		}
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		for(ModuleBase module : modules) {
			module.actionPerform(button);
		}

	}

	@Override
	protected void keyTyped(char key, int something) throws IOException {

		boolean superKeypress = true;

		for(ModuleBase module : modules) {
			if(superKeypress)
				superKeypress = module.keyTyped(key, something);
			else
				module.keyTyped(key, something);
		}


		if(superKeypress)
			super.keyTyped(key, something);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a,
			int b) {
		super.drawGuiContainerForegroundLayer(a, b);

		this.fontRenderer.drawString(I18n.format(unlocalizedName), 8, 6, 4210752);

		for(ModuleBase module : modules)
			if(module.getVisible())
				module.renderForeground((width - xSize)/2, (height - ySize) / 2,a - (width - xSize)/2 ,b - (height - ySize) / 2, zLevel, this, this.fontRenderer);

	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {

		super.mouseClicked(x, y, button);

		for(ModuleBase module : modules)
			module.onMouseClicked(this, x - (width - xSize) / 2, y - (height - ySize) / 2, button);
	}

	@Override
	protected void mouseClickMove(int x, int y,
			int button, long timeSinceLastClick) {
		super.mouseClickMove(x, y, button, timeSinceLastClick);

		for(ModuleBase module : modules)
			module.onMouseClickedAndDragged(x - (width - xSize) / 2, y - (height - ySize) / 2, button,timeSinceLastClick);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f1,
			int i2, int i3) {
		//Garuntee proper color
		GlStateManager.color(1, 1, 1, 1);
		
		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);

		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);

		if(!hasSlots) {
			this.drawTexturedModalRect(x + 7, y + 88, 7, 12, 162, 54);
		}

		for(ModuleBase module : modules) {
			if(module.getVisible())
				module.renderBackground(this, x, y, i2, i3, fontRenderer);
		}
	}

	public List<Rectangle> getExtraAreasCovered() {
		List<Rectangle> list = new LinkedList<>();
		
		for(ModuleBase module : modules) {
				list.add(new Rectangle((width - xSize) / 2 + module.offsetX, (height - ySize) / 2 + module.offsetY, module.getSizeX(), module.getSizeY()));
			
		}
		return list;
	}

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
