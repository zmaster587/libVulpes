package zmaster587.libVulpes.inventory;

import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TextureResources {
	
	public static final  ResourceLocation starryBG 	   = new ResourceLocation("libvulpes", "textures/gui/starryBg.png");
	public static final ResourceLocation[] buttonNull = {new ResourceLocation("libvulpes","textures/gui/null.png" )};
	public static final ResourceLocation[] buttonScan = {new ResourceLocation("libvulpes", "textures/gui/GuiScan.png"), new ResourceLocation("libvulpes", "textures/gui/GuiScan_hover.png"), new ResourceLocation("libvulpes", "textures/gui/GuiScan_pressed.png"), null};
	public static final ResourceLocation[] buttonBuild = {new ResourceLocation("libvulpes", "textures/gui/GuiButtonRed.png"), new ResourceLocation("libvulpes", "textures/gui/GuiButtonRed_hover.png"), new ResourceLocation("libvulpes", "textures/gui/GuiButtonRed_pressed.png"),  new ResourceLocation("libvulpes", "textures/gui/GuiButtonRed_disabled.png")};
	public static final ResourceLocation[] buttonDown = {new ResourceLocation("libvulpes", "textures/gui/GuiArrowDown.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowDown_hover.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowDown_pressed.png"), null};
	public static final ResourceLocation[] buttonLeft = {new ResourceLocation("libvulpes", "textures/gui/GuiArrowLeft.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowLeft_hover.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowLeft_pressed.png"), null};
	public static final ResourceLocation[] buttonRight = {new ResourceLocation("libvulpes", "textures/gui/GuiArrowRight.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowRight_hover.png"), new ResourceLocation("libvulpes", "textures/gui/GuiArrowRight_pressed.png"), null};
	public static final ResourceLocation[] buttonSquare = {new ResourceLocation("libvulpes", "textures/gui/buttonSquare.png"), new ResourceLocation("libvulpes", "textures/gui/buttonSquare_hover.png"), new ResourceLocation("libvulpes", "textures/gui/buttonSquare.png"), null};
	public static final ResourceLocation[] buttonToggleImage = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/switchOff.png"), new ResourceLocation("libvulpes:textures/gui/buttons/switchOn.png")};
	

	public static final ResourceLocation[] buttonRedstoneActive = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneAllowed.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneAllowed_hover.png")};
	public static final ResourceLocation[] buttonRedstoneInverted = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneInverted.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneInverted_hover.png")};
	public static final ResourceLocation[] buttonRedstoneDisabled = new ResourceLocation[] {new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneNotAllowed.png"), new ResourceLocation("libvulpes:textures/gui/buttons/buttonRedstoneNotAllowed_hover.png")};
	
	public static final ResourceLocation coalGeneratorBarImg = new ResourceLocation("libvulpes", "textures/gui/coalGenerator.png");
	
	public static final ProgressBarImage progressGenerator = new ProgressBarImage(16, 0, 16, 16, 0, 0, 16, 16, EnumFacing.UP, coalGeneratorBarImg);
	public static final IconResource	slotDisabled = new IconResource(230, 36, 18, 18, new ResourceLocation("libvulpes","textures/gui/maingui.png"));
}
