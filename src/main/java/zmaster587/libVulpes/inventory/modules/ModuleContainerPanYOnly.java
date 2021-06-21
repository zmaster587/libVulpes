package zmaster587.libVulpes.inventory.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.inventory.GuiModular;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ModuleContainerPanYOnly extends ModuleBase {

	protected int currentPosX, currentPosY;
	protected int screenSizeX;
	protected int screenSizeY;
	protected int containerSizeX;
	protected int containerSizeY;
	protected List<ModuleBase> moduleList;
	protected List<ModuleBase> staticModuleList;
	protected List<GuiButton> buttonList, staticButtonList;
	protected List<Slot> slotList;
	protected int mouseLastX, mouseLastY;
	boolean outofBounds;
	ResourceLocation backdrop;
	protected int internalOffsetX;
	protected int internalOffsetY;

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, 16, 16, 0, 0);
	}

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX, int paddingY) {
		this(offsetX, offsetY, moduleList, staticModules, backdrop, screenSizeX, screenSizeY, paddingX, paddingY, 0, 0);
	}

	public ModuleContainerPanYOnly(int offsetX, int offsetY, List<ModuleBase> moduleList, List<ModuleBase> staticModules, ResourceLocation backdrop, int screenSizeX, int screenSizeY, int paddingX , int paddingY, int containerSizeX, int containerSizeY) {
		super(offsetX, offsetY);
		this.moduleList = moduleList;
		this.staticModuleList = staticModules;
		outofBounds = true;

		this.screenSizeX = screenSizeX;
		this.screenSizeY = screenSizeY;

		buttonList = new LinkedList<>();
		staticButtonList = new LinkedList<>();
		slotList = new LinkedList<>();

		this.backdrop = backdrop;

		if(containerSizeX == 0 || containerSizeY == 0) {
			//AutoSize the container -----
			int maxX = 0, maxY = 0;
			for(ModuleBase module : moduleList) {
				if(module.offsetX > maxX)
					maxX = module.offsetX;
				if(module.offsetY > maxY)
					maxY = module.offsetY;
			}

			this.containerSizeX = maxX + paddingX;
			this.containerSizeY = maxY + paddingY;
			// -----------------------------
		}
		else {
			this.containerSizeX = containerSizeX;
			this.containerSizeY = containerSizeY;
		}

		if(moduleList != null)
			for(ModuleBase module : this.moduleList) {
				module.offsetX += offsetX;
				module.offsetY += offsetY;
			}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiButton> addButtons(int x, int y) {

		buttonList.clear();
		staticButtonList.clear();

		for(ModuleBase module : this.moduleList) {
			buttonList.addAll(module.addButtons(x, y));
		}

		for(ModuleBase module : this.staticModuleList) {
			staticButtonList.addAll(module.addButtons(x, y));
		}

		return new LinkedList<>();
	}

	public void setOffset(int y) {
		internalOffsetY = y + screenSizeY;
	}

	public void setOffset2(int y) {
		int deltaY = -y - currentPosY;
		currentPosY += deltaY;

		for(GuiButton button2 : buttonList) {
			button2.y += deltaY;
		}

		for(ModuleBase module : moduleList) {
			module.offsetY += deltaY;
		}
	}
	
	public int getScrollY() {
		return currentPosY;
	}
	
	@Override
	public List<Slot> getSlots(Container container) {
		List<Slot> list = new LinkedList<>();

		for(ModuleBase module : this.moduleList) {
			list.addAll(module.getSlots(container));
		}

		for(ModuleBase module : this.staticModuleList) {
			list.addAll(module.getSlots(container));
		}

		slotList = list;
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {

		for(ModuleBase module : moduleList)
			module.actionPerform(button);

		for(ModuleBase module : staticModuleList)
			module.actionPerform(button);
	}

	public void onScroll(int dwheel) {
		if(dwheel < 0) {
			moveContainerInterior(-20);
		}
		else if(dwheel > 0) {
			moveContainerInterior(20);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			GuiContainer gui, FontRenderer font) {

		//Handle scrolling
		int d;
		if(isMouseInBounds(0, 0, mouseX, mouseY) && (d = Mouse.getDWheel()) != 0 )
			onScroll(d);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		setUpScissor(gui, offsetX + guiOffsetX, guiOffsetY + offsetY, offsetX + screenSizeX, offsetY + screenSizeY);

		for(ModuleBase module : moduleList)
			module.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		for(ModuleBase module : staticModuleList)
			module.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@SideOnly(Side.CLIENT)
	protected void setUpScissor(GuiContainer gui, int screenOffsetX, int screenOffsetY, int screenSizeX, int screenSizeY) {
		float multiplierX = gui.mc.displayWidth / (float)gui.width;
		float multiplierY = gui.mc.displayHeight / (float)gui.height;

		GL11.glScissor((int)( screenOffsetX*multiplierX), gui.mc.displayHeight - (int)((screenOffsetY + screenSizeY)*multiplierY), (int)(screenSizeX*multiplierX), (int)(screenSizeY*multiplierY));

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(GuiModular gui,int x, int y, int button) {
		super.onMouseClicked(gui, x, y, button);

		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		int scaledX = Mouse.getX() * i / Minecraft.getMinecraft().displayWidth;
		int scaledY = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;

		mouseLastX = scaledX;
		mouseLastY = scaledY;

		//Handles buttons (mostly vanilla copy)
		if(button == 0 && isMouseInBounds(0, 0, x, y)) {

			List<GuiButton> fullButtonList = new LinkedList<>();
			fullButtonList.addAll(buttonList);
			fullButtonList.addAll(staticButtonList);

			for(GuiButton button2 : fullButtonList) {
				if(button2.mousePressed(Minecraft.getMinecraft(), scaledX, scaledY)) {
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(gui, button2, buttonList);
					if(MinecraftForge.EVENT_BUS.post(event))
						break;
					event.getButton().playPressSound(gui.mc.getSoundHandler());
					
					try {
						gui.actionPerformed(event.getButton());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(gui.equals(gui.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(gui, event.getButton(), buttonList));

				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private boolean isMouseInBounds(int x , int y, int mouseX, int mouseY) {
		int transformedMouseX = mouseX - x - offsetX;
		int transformedMouseY = mouseY - y - offsetY;
		//return true;
		return transformedMouseX > 0 && transformedMouseX < screenSizeX + offsetX && transformedMouseY > 0 && transformedMouseY < screenSizeY + offsetY;
	}

	protected void moveContainerInterior(int deltaY) {
		//Clamp bounds ------------------------------------------------
		if(deltaY > 0) {
			deltaY = Math.min(deltaY, -currentPosY);
		}
		else if(deltaY < 0) {
			deltaY = Math.max(deltaY, -containerSizeY - currentPosY);
		}
		//--------------------------------------------------------------

		currentPosY += deltaY;

		for(GuiButton button2 : buttonList) {
			button2.y += deltaY;
		}

		for(ModuleBase module : moduleList) {
			module.offsetY += deltaY;
		}
	}

	//DO the actual scrolling
	@Override
	@SideOnly(Side.CLIENT)
	public void onMouseClickedAndDragged(int x, int y, int button, long timeSinceLastClick) {

		if(isMouseInBounds(0, 0, x, y) ) {

			ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			final int l = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;

			if(outofBounds) {
				mouseLastY = l;
				outofBounds = false;
			}
			else if(mouseLastY != y) {

				int deltaY = (l - mouseLastY);

				moveContainerInterior(deltaY);

				mouseLastY = l;
			}
		}
		else {
			outofBounds = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		setUpScissor(gui, x + offsetX, y + offsetY, screenSizeX + offsetX, screenSizeY + offsetY);

		if(backdrop != null) {
			gui.mc.getTextureManager().bindTexture(backdrop);
			gui.drawTexturedModalRect(x + offsetX, y + offsetY, (int)(-0.1*currentPosX), (int)(-0.1*currentPosY), screenSizeX + offsetX,  screenSizeY + offsetY);
		}
		
		for(GuiButton button : buttonList)
			button.drawButton(gui.mc, mouseX, mouseY, 0);

		for(GuiButton button : staticButtonList)
			button.drawButton(gui.mc, mouseX, mouseY, 0);

		for(ModuleBase module : moduleList) {
			module.renderBackground(gui, x, y, mouseX, mouseY, font);
		}

		for(ModuleBase module : staticModuleList) {
			module.renderBackground(gui, x, y, mouseX, mouseY, font);
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

	}
	
	@Override
	public void setEnabled(boolean state) {
		if(state && !isEnabled()) {
			moveContainerInterior(0);
		}
		else if(!state && isEnabled()) {
			moveContainerInterior(0);
		}
		super.setEnabled(state);
	}
}
