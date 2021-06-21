package zmaster587.libVulpes.entity.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class FxErrorBlock extends Particle {

	public static final ResourceLocation icon = new ResourceLocation("libvulpes:textures/fx/x.png");

	public FxErrorBlock(World world, double x,
			double y, double z) {
		super(world, x, y, z);
		this.prevPosX = this.posX = x + 0.5;
		this.prevPosY = this.posY = y + 0.5;
		this.prevPosZ = this.posZ = z + 0.5;

		this.particleMaxAge = 100;
	}

	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {

		
		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		float f10 = 0.25F * this.particleScale;

        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        //worldRendererIn.finishDrawing();
        GlStateManager.disableDepth();
        worldRendererIn.finishDrawing();
        worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        
		worldRendererIn.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
	
		Tessellator.getInstance().draw();
		worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		
		GlStateManager.enableDepth();
	}
	
	
	/*@Override
	public void renderParticle(Tessellator tess, float x1,
			float y1, float z1, float rotationYZ,
			float y2, float rotationXZ) {


		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
		//Finish current draw, we need to kill depth test
		//tess.draw();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		// Draw the box
		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x1 - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x1 - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x1 - interpPosZ);
		float f10 = 0.25F * this.particleScale;

		tess.startDrawingQuads();
		tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1f);
		tess.addVertexWithUV((double)(f11 - y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 - rotationYZ * f10 - rotationXZ * f10), 1, 1);
		tess.addVertexWithUV((double)(f11 - y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 - rotationYZ * f10 + rotationXZ * f10), 1, 0);
		tess.addVertexWithUV((double)(f11 + y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 + rotationYZ * f10 + rotationXZ * f10), 0, 0);
		tess.addVertexWithUV((double)(f11 + y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 + rotationYZ * f10 - rotationXZ * f10), 0, 1);
		tess.draw();

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		//continue happily
		//tess.startDrawingQuads();
	}*/

	@Override
	public int getFXLayer() {
		return 2;
	}	

	@Override
	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
	}

}
