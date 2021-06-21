package zmaster587.libVulpes.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.util.IFluidHandlerInternal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleLiquidIndicator extends ModuleBase {

	IFluidHandler tile;
	IFluidHandlerInternal tile2;

	ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");
	
	int prevLiquidUUID;
	int prevLiquidAmt;
	private static final int invalidFluid = -1;

	public ModuleLiquidIndicator(int offsetX, int offsetY, IFluidHandler progress) {
		super(offsetX, offsetY);
		this.tile = progress;
		if(progress instanceof IFluidHandlerInternal)
			this.tile2 = (IFluidHandlerInternal)progress;
	}

	//TODO: sync changes
	@Override
	public int numberOfChangesToSend() {
		return 3;
	}

	private Fluid getFluid(int id)
	{
		for(Fluid fluid : FluidRegistry.getRegisteredFluidIDs().keySet())
		{
			if(FluidRegistry.getRegisteredFluidIDs().get(fluid) == id)
				return fluid;
		}
		return FluidRegistry.WATER;
	}
	
	private int getFluidID(Fluid fluid)
	{
		return FluidRegistry.getRegisteredFluidIDs().get(fluid);
	}
	
	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		IFluidTankProperties info = tile.getTankProperties()[0];

		if(localId == 0 && info.getContents() != null)
			crafter.sendWindowProperty(container, variableId, info.getContents().amount & 0xFFFF);
		else if(localId == 1 && info.getContents() != null)
			crafter.sendWindowProperty(container, variableId, (info.getContents().amount >>> 16) & 0xFFFF);
		else if(localId == 2)
			if(info.getContents() == null) 
				crafter.sendWindowProperty(container, variableId, invalidFluid);
			else
				crafter.sendWindowProperty(container, variableId, getFluidID(info.getContents().getFluid()));
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		IFluidTankProperties info = tile.getTankProperties()[0];

		if(slot == 2) {
			if(info.getContents() == null && value != invalidFluid) {
				if(tile2 != null)
					tile2.fillInternal(new FluidStack(getFluid(value), 1), true);
				else
					tile.fill(new FluidStack(getFluid(value), 1), true);
			}
			else if(value == invalidFluid) {
				if(tile2 != null) 
					tile2.drainInternal(info.getCapacity(), true);
				else
					tile.drain(info.getCapacity(), true);
			}
			else if(info.getContents()  != null && value != getFluidID(info.getContents().getFluid())) { //Empty the tank then fill it back up with new resource
				FluidStack stack;
				if(tile2 != null)
					stack = tile2.drainInternal(info.getCapacity(), true);
				else
					stack = tile.drain(info.getCapacity(), true);

				stack = new FluidStack(stack.getFluid(), stack.amount);

				tile.fill(stack, true);
			}
		}
		else if((slot == 0 || slot == 1) && info.getContents() != null) {
			int difference;

			if(slot == 0) {
				difference = (value & 0xFFFF) - (info.getContents().amount & 0xFFFF);
			}
			else
				difference = ((value << 16) & 0xFFFF0000) - (info.getContents().amount & 0xFFFF0000);

			if(difference > 0) {
				if(tile2 != null)
					tile2.fillInternal(new FluidStack(info.getContents().getFluid(), difference), true);
				else
					tile.fill(new FluidStack(info.getContents().getFluid(), difference), true);
			}
			else
				if(tile2 != null)
					tile2.drainInternal(-difference, true);
				else
					tile.drain(-difference, true);
		}
	}

	@Override
	public boolean needsUpdate(int localId) {
		IFluidTankProperties info = tile.getTankProperties()[0];

		if(localId == 0 || localId == 1) {
			return (info.getContents() != null && prevLiquidAmt != info.getContents().amount);
		}
		else if(localId == 2) {
			if(info.getContents() == null)
				return prevLiquidUUID != invalidFluid;
			else
				return getFluidID(info.getContents().getFluid()) != prevLiquidUUID;
		}

		return false;
	}

	@Override
	protected void updatePreviousState(int localId) {
		IFluidTankProperties info = tile.getTankProperties()[0];
		if(localId == 0 && info.getContents() != null)
			prevLiquidAmt =  info.getContents().amount;
		else if(localId == 1)
			if( info.getContents() == null) 
				prevLiquidUUID = invalidFluid;
			else
				prevLiquidUUID = getFluidID(info.getContents().getFluid());
	}

	protected float getProgress() {
		IFluidTankProperties[] info = tile.getTankProperties();

		int capacity = 0;
		int fillAmount = 0;

		for(IFluidTankProperties fluidInfo : info) {
			capacity += fluidInfo.getCapacity();
			if(fluidInfo.getContents() != null)
				fillAmount += fluidInfo.getContents().amount;
		}

		return fillAmount/(float)capacity;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderForeground (int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {

		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;
		int ySize = 52;
		int xSize = 12;

		if( relativeX > 0 && relativeX < xSize && relativeY > 0 && relativeY < ySize) {
			List<String> list = new LinkedList<>();
			FluidStack fluidStack = tile.getTankProperties()[0].getContents();

			if(fluidStack != null) {

				list.add(fluidStack.getLocalizedName()+": "+fluidStack.amount + " / " + tile.getTankProperties()[0].getCapacity() + " mB");


			}
			else
				list.add("Empty");

			this.drawTooltip(gui, list, mouseX, mouseY, zLevel, font);
		}

	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY,  font);
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, 176, 58, 14, 54);

		//Draw Fluid
		IFluidTankProperties info = tile.getTankProperties()[0];

		if(info.getContents() != null) {


			Minecraft.getMinecraft().renderEngine.bindTexture(fluidIcon);

			TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
			TextureAtlasSprite sprite = map.getTextureExtry(info.getContents().getFluid().getStill().toString());
			
			int color = info.getContents().getFluid().getColor(info.getContents());

			GL11.glColor3b((byte)((color >>> 16) & 127), (byte)((color >>> 8) & 127), (byte)(color & 127));
			//GL11.glColor3b((byte)127, (byte)127, (byte)127);

			float percent = getProgress();
			int ySize = 52;
			int xSize = 12;

			if(sprite == null)
				gui.drawTexturedModalRect(offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), 0, 0, xSize, (int)(percent*ySize));
			else {
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				gui.drawTexturedModalRect(offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), sprite, xSize, (int)(percent*ySize));
			}
			//gui.drawTexturedModelRectFrom(offsetX + x + 1, offsetY + y + 1 + (ySize-(int)(percent*ySize)), fluidIcon, xSize, (int)(percent*ySize));

			//this.drawProgressBarIconVertical(x + 27, y + 18,, 12, 52, getProgress());
		}
	}

}
