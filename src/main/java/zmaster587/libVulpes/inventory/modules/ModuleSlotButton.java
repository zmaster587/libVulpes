package zmaster587.libVulpes.inventory.modules;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.TextureResources;

import javax.annotation.Nonnull;

public class ModuleSlotButton extends ModuleButton {

	ItemStack stack;
	World worldObj;

	public ModuleSlotButton(int offsetX, int offsetY, int buttonId, IButtonInventory tile, @Nonnull ItemStack slotDisplay, World world ) {

		super(offsetX, offsetY, buttonId , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName() ,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	public ModuleSlotButton(int offsetX, int offsetY, int buttonId, IButtonInventory tile, @Nonnull ItemStack slotDisplay, String extraDisplay,  World world ) {

		super(offsetX, offsetY, buttonId , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName() + " \n" + extraDisplay,16,16);
		stack = slotDisplay;
		this.worldObj = world;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();


		textureManager.bindTexture(CommonResources.genericBackground);
		gui.drawTexturedModalRect(x + offsetX - 1, y + offsetY - 1, 176, 0, 18, 18);

		int p_77015_4_ = x + offsetX;
		int p_77015_5_ = y + offsetY;

		int zLevel = 500;


		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);



		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
		GL11.glDisable(GL11.GL_BLEND);


		GL11.glPushMatrix();
		GL11.glTranslatef((float)(p_77015_4_ + 8), (float)(p_77015_5_ + 12), -3.0F + zLevel);
		GL11.glScalef(10.0F, 10.0F, 10.0F);

		GL11.glScalef(1.0F, 1.0F, -1.0F);
		GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotated(45.0F + ((System.currentTimeMillis() % 200000)/50F) * 2, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-.5f,-255,-.5f);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Block itemBlock = Block.getBlockFromItem(stack.getItem());
		if(itemBlock != Blocks.AIR) {
			IBlockState block =  itemBlock.getStateFromMeta(stack.getItemDamage());
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			//Requires world OBJ for some stupid reason involving DEBUG WORLD check... why not idk... have a var for that?
			Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(block, new BlockPos(0, 255, 0), worldObj, buffer);
			Tessellator.getInstance().draw();
		}
		GL11.glPopMatrix();

	}

}
