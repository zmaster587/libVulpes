package zmaster587.libVulpes.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderHelper {

	/**
	 * 
	 * @param text text to render
	 * @param horizontalLetter max letters in horizontal direction
	 * @param vertLetters max letters in vertial direction
	 * @param font resource location of the font to use
	 */
	/*public static void renderText(TextPart text, int horizontalLetter, int vertLetters, ResourceLocation font) {

		StringTokenizer tokens = new StringTokenizer(text.text, "\n");
		Tessellator tess = Tessellator.instance;

		//tess.setColorRGBA(text.colorRGBA & 255, (text.colorRGBA << 8) & 255, (text.colorRGBA << 16) & 255, (text.colorRGBA << 24) & 255);

		GL11.glColor3ub((byte)((text.colorRGBA >> 16) & 255), (byte)((text.colorRGBA >> 8) & 255), (byte)(text.colorRGBA & 255));//, (byte)((text.colorRGBA >> 24) & 255));


		tess.startDrawingQuads();
		for(int line = 0; line < vertLetters && tokens.hasMoreTokens(); line++) {
			String token = tokens.nextToken();

			for(int i = 0 ; i < token.length() && i < horizontalLetter; i++) {
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY -text.size*line - text.size, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line - text.size, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY - text.size*line, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D,(8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);

			}
		}
		tess.draw();
	}

    /**
	 * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b, a
	 * /
    public static boolean renderStandardBlockWithColorMultiplier(Block p_147736_1_, int p_147736_2_, int p_147736_3_, int p_147736_4_, float p_147736_5_, float p_147736_6_, float p_147736_7_, float alpha)
    {
    	RenderBlocks renderBlocks = RenderBlocks.getInstance();
    	renderBlocks.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (p_147736_1_ != Blocks.grass)
        {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_);

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_, 0))
        {
            tessellator.setBrightness(renderBlocks.renderMinY > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_));
            tessellator.setColorRGBA_F(f10, f13, f16, alpha);
            renderBlocks.renderFaceYNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 0));
            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_, 1))
        {
            tessellator.setBrightness(renderBlocks.renderMaxY < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_));
            tessellator.setColorRGBA_F(f7, f8, f9,alpha);
            renderBlocks.renderFaceYPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1, 2))
        {
            tessellator.setBrightness(renderBlocks.renderMinZ > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1));
            tessellator.setColorRGBA_F(f11, f14, f17, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 2);
            renderBlocks.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_, alpha);
                renderBlocks.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1, 3))
        {
            tessellator.setBrightness(renderBlocks.renderMaxZ < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1));
            tessellator.setColorRGBA_F(f11, f14, f17, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 3);
            renderBlocks.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_,alpha);
                renderBlocks.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_, 4))
        {
            tessellator.setBrightness(renderBlocks.renderMinX > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_));
            tessellator.setColorRGBA_F(f12, f15, f18,alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 4);
            renderBlocks.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_, alpha);
                renderBlocks.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || p_147736_1_.shouldSideBeRendered(renderBlocks.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_, 5))
        {
            tessellator.setBrightness(renderBlocks.renderMaxX < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(renderBlocks.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_));
            tessellator.setColorRGBA_F(f12, f15, f18, alpha);
            iicon = renderBlocks.getBlockIcon(p_147736_1_, renderBlocks.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 5);
            renderBlocks.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                tessellator.setColorRGBA_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_,alpha);
                renderBlocks.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }*/

	public static void renderTag(double distanceSq, String displayString, double x, double y, double z, int sizeOnScreen) {
		renderTag(distanceSq, displayString, x,y,z, 6, 1);
	}
	
	public static void renderTag(double distanceSq, String displayString, double x, double y, double z, int sizeOnScreen, float scale) {
		double d3 = distanceSq;

		Minecraft mc = Minecraft.getMinecraft();
		RenderManager renderManager = mc.getRenderManager();
		if (d3 <= (double)(sizeOnScreen * sizeOnScreen))
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			float f = 1.6F*scale;
			float f1 = 0.016666668F * f;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x + 0.0F, (float)y + 0.5F, (float)z);
			GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-f1, -f1, f1);
			GlStateManager.disableLighting();
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			Tessellator tessellator = Tessellator.getInstance();
			byte b0 = 0;

			BufferBuilder buffer = tessellator.getBuffer();

			GlStateManager.disableTexture2D();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			int j = fontrenderer.getStringWidth(displayString) / 2;
			GlStateManager.color(0.0F, 0.0F, 0.0F, 0.25F);
			buffer.pos(-j - 1, -1 + b0, 0.0D).endVertex();
			buffer.pos(-j - 1, 8 + b0, 0.0D).endVertex();
			buffer.pos(j + 1, 8 + b0, 0.0D).endVertex();
			buffer.pos(j + 1, -1 + b0, 0.0D).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
			fontrenderer.drawString(displayString, -fontrenderer.getStringWidth(displayString) / 2, b0, 553648127);

			GlStateManager.depthMask(true);
			fontrenderer.drawString(displayString, -fontrenderer.getStringWidth(displayString) / 2, b0, -1);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public static void setupPlayerFacingMatrix(double distanceSq, double x, double y, double z) {

		Minecraft mc = Minecraft.getMinecraft();
		RenderManager renderManager = mc.getRenderManager();
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.0F, (float)y + 0.5F, (float)z);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f1, -f1, f1);
	}
	
	public static void cleanupPlayerFacingMatrix() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public static void renderBlockWithEndPointers(BufferBuilder buff, double radius, double x1, double y1, double z1, double x2, double y2, double z2) {
		double buffer;
		renderBottomFaceEndpoints(buff, radius, x1, y1 - radius/2d, z1, x2, y2 - radius/2d, z2);
		renderTopFaceEndpoints(buff, radius, x1, y1 + radius/2d, z1, x2, y2 + radius/2d, z2);
		renderNorthFaceEndpoints(buff, radius, x1, y1, z1 + radius/2d, x2, y2, z2 + radius/2d);
		renderSouthFaceEndpoints(buff, radius, x1, y1, z1 - radius/2d, x2, y2, z2 - radius/2d);
		renderEastFaceEndpoints(buff, radius, x1 + radius/2d, y1, z1, x2 + radius/2d, y2, z2);
		renderWestFaceEndpoints(buff, radius, x1 - radius/2d, y1, z1, x2 - radius/2d, y2, z2);
	}

	public static void renderCrossXZ(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		renderTopFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax);
		renderBottomFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax);
		renderNorthFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax);
		renderSouthFaceEndpoints(buff, width, xMin, yMin, zMin, xMax, yMax, zMax);
	}


	public static void renderTopFace(BufferBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax) {
		//top
		buff.pos(xMin, yMax, zMin).normal(0, 1, 0).endVertex();
		buff.pos(xMin, yMax, zMax).normal(0, 1, 0).endVertex();
		buff.pos(xMax, yMax, zMax).normal(0, 1, 0).endVertex();
		buff.pos(xMax, yMax, zMin).normal(0, 1, 0).endVertex();

	}

	public static void renderTopFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//top
		buff.pos(xMin, yMin, zMin - width).normal(0, 1, 0).endVertex();
		buff.pos(xMin, yMin, zMin + width).normal(0, 1, 0).endVertex();
		buff.pos(xMax, yMax, zMax + width).normal(0, 1, 0).endVertex();
		buff.pos(xMax, yMax, zMax - width).normal(0, 1, 0).endVertex();

	}

	public static void renderBottomFace(BufferBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax) {
		//bottom
		buff.pos(xMax, yMax, zMin).normal(0, -1, 0).endVertex();
		buff.pos(xMax, yMax, zMax).normal(0, -1, 0).endVertex();
		buff.pos(xMin, yMax, zMax).normal(0, -1, 0).endVertex();
		buff.pos(xMin, yMax, zMin).normal(0, -1, 0).endVertex();
	}

	public static void renderBottomFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//top
		buff.pos(xMin, yMin, zMin + width).normal(0, -1, 0).endVertex();
		buff.pos(xMin, yMin, zMin - width).normal(0, -1, 0).endVertex();
		buff.pos(xMax, yMax, zMax - width).normal(0, -1, 0).endVertex();
		buff.pos(xMax, yMax, zMax + width).normal(0, -1, 0).endVertex();
	}	

	public static void renderNorthFace(BufferBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax) {
		//north
		buff.pos(xMin, yMax, zMin).normal(0, 0, 1).endVertex();
		buff.pos(xMax, yMax, zMin).normal(0, 0, 1).endVertex();
		buff.pos(xMax, yMin, zMin).normal(0, 0, 1).endVertex();
		buff.pos(xMin, yMin, zMin).normal(0, 0, 1).endVertex();
	}

	public static void renderNorthFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//north
		buff.pos(xMin, yMin + width, zMin).normal(0, 0, 1).endVertex();
		buff.pos(xMax, yMax + width, zMax).normal(0, 0, 1).endVertex();
		buff.pos(xMax, yMax - width, zMax).normal(0, 0, 1).endVertex();
		buff.pos(xMin, yMin - width, zMin).normal(0, 0, 1).endVertex();
	}

	public static void renderSouthFace(BufferBuilder buff, double zMax, double xMin, double yMin, double xMax, double yMax) {
		//south
		buff.pos(xMin, yMax, zMax).normal(0, 0, -1).endVertex();
		buff.pos(xMin, yMin, zMax).normal(0, 0, -1).endVertex();
		buff.pos(xMax, yMin, zMax).normal(0, 0, -1).endVertex();
		buff.pos(xMax, yMax, zMax).normal(0, 0, -1).endVertex();
	}

	public static void renderSouthFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//south
		buff.pos(xMin, yMin + width, zMin).normal(0, 0, -1).endVertex();
		buff.pos(xMin, yMin - width, zMin).normal(0, 0, -1).endVertex();
		buff.pos(xMax, yMax - width, zMax).normal(0, 0, -1).endVertex();
		buff.pos(xMax, yMax + width, zMax).normal(0, 0, -1).endVertex();
	}

	public static void renderEastFace(BufferBuilder buff, double xMax, double yMin, double zMin, double yMax, double zMax) {
		//east
		buff.pos(xMax, yMax, zMin).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMax, zMax).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMin, zMax).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMin, zMin).normal(1, 0, 0).endVertex();
	}


	public static void renderEastFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//east
		buff.pos(xMax, yMax + width, zMin).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMax - width, zMax).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMin - width, zMax).normal(1, 0, 0).endVertex();
		buff.pos(xMax, yMin + width, zMin).normal(1, 0, 0).endVertex();
	}

	public static void renderWestFace(BufferBuilder buff, double xMin, double yMin, double zMin, double yMax, double zMax) {
		//west
		buff.pos(xMin, yMin, zMin).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMin, zMax).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMax, zMax).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMax, zMin).normal(-1, 0, 0).endVertex();
	}

	public static void renderWestFaceEndpoints(BufferBuilder buff, double width, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		//west
		buff.pos(xMin, yMin + width, zMin).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMin - width, zMax).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMax - width, zMax).normal(-1, 0, 0).endVertex();
		buff.pos(xMin, yMax + width, zMin).normal(-1, 0, 0).endVertex();
	}

	public static void renderTopFaceWithUV(BufferBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//top
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();


	}

	public static void renderBottomFaceWithUV(BufferBuilder buff, double yMax, double xMin, double zMin, double xMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//bottom

		buff.pos(xMax, yMax, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();

	}

	public static void renderNorthFaceWithUV(BufferBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, double uMin, double uMax, double vMin, double vMax) {
		//north
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
	}

	public static void renderNorthFaceWithUVNoNormal(BufferBuilder buff, double zMin, double xMin, double yMin, double xMax, double yMax, double uMin, double uMax, double vMin, double vMax) {
		//north
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMin).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
	}

	public static void renderSouthFaceWithUV(BufferBuilder buff, double zMax, double xMin, double yMin, double xMax, double yMax, double uMin, double uMax, double vMin, double vMax) {
		//south
		buff.pos(xMin, yMax, zMax).tex(uMin, vMin).endVertex();
		buff.pos(xMin, yMin, zMax).tex(uMin, vMax).endVertex();
		buff.pos(xMax, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMin).endVertex();
	}

	public static void renderEastFaceWithUV(BufferBuilder buff, double xMax, double yMin, double zMin, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//east
		buff.pos(xMax, yMax, zMin).tex(uMin, vMin).endVertex();
		buff.pos(xMax, yMax, zMax).tex(uMax, vMin).endVertex();
		buff.pos(xMax, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMax, yMin, zMin).tex(uMin, vMax).endVertex();
	}


	public static void renderWestFaceWithUV(BufferBuilder buff, double xMin, double yMin, double zMin, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {
		//west
		buff.pos(xMin, yMin, zMin).tex(uMin, vMax).endVertex();
		buff.pos(xMin, yMin, zMax).tex(uMax, vMax).endVertex();
		buff.pos(xMin, yMax, zMax).tex(uMax, vMin).endVertex();
		buff.pos(xMin, yMax, zMin).tex(uMin, vMin).endVertex();
	}

	public static void renderCubeWithUV(BufferBuilder buff, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double uMin, double uMax, double vMin, double vMax) {


		renderTopFaceWithUV(buff, yMax, xMin, zMin, xMax, zMax, uMin, uMax,vMin, vMax);
		renderNorthFaceWithUV(buff, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
		renderSouthFaceWithUV(buff, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
		renderEastFaceWithUV(buff, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
		renderWestFaceWithUV(buff, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
		renderBottomFaceWithUV(buff, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
	}

	public static void renderCube(BufferBuilder buff, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {


		renderTopFace(buff, yMax, xMin, zMin, xMax, zMax);
		renderNorthFace(buff, zMin, xMin, yMin, xMax, yMax);
		renderSouthFace(buff, zMax, xMin, yMin, xMax, yMax);
		renderEastFace(buff, xMax, yMin, zMin, yMax, zMax);
		renderWestFace(buff, xMin, yMin, zMin, yMax, zMax);
		renderBottomFace(buff, yMin, xMin, zMin, xMax, zMax);
	}

	public static void renderItem(TileEntity tile, @Nonnull ItemStack itemstack, RenderItem dummyItem)
	{
		if (!itemstack.isEmpty())
		{
			EntityItem entityitem = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, itemstack);
			Item item = entityitem.getItem().getItem();
			entityitem.getItem().setCount(1);
			entityitem.hoverStart = 0.0F;
			GlStateManager.disableLighting();



			GlStateManager.scale(0.5F, 0.5F, 0.5F);

			if (!dummyItem.shouldRenderItemIn3D(entityitem.getItem()))
			{
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.pushAttrib();
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			dummyItem.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();



			GlStateManager.enableLighting();
		}
	}

	public static void renderItem(TileEntity tile, EntityItem entityitem, RenderItem dummyItem)
	{
		GlStateManager.disableLighting();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (!dummyItem.shouldRenderItemIn3D(entityitem.getItem()))
		{
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.pushAttrib();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		dummyItem.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();



		GlStateManager.enableLighting();

	}
}
