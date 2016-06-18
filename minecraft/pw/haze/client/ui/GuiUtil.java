package pw.haze.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Haze on 6/20/2015.
 */
public class GuiUtil {


    public static void drawGradientRect(double x1, double z1, double x2, double z2, int par5, int par6) {
        float var7 = (par5 >> 24 & 255) / 255.0F;
        float var8 = (par5 >> 16 & 255) / 255.0F;
        float var9 = (par5 >> 8 & 255) / 255.0F;
        float var10 = (par5 & 255) / 255.0F;
        float var11 = (par6 >> 24 & 255) / 255.0F;
        float var12 = (par6 >> 16 & 255) / 255.0F;
        float var13 = (par6 >> 8 & 255) / 255.0F;
        float var14 = (par6 & 255) / 255.0F;
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glShadeModel(GL_SMOOTH);
        WorldRenderer tessellator = Tessellator.getInstance().getWorldRenderer();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(var8, var9, var10, var7);
        tessellator.addVertex(x2, z1, 0.0);
        tessellator.addVertex(x1, z1, 0.0);
        tessellator.setColorRGBA_F(var12, var13, var14, var11);
        tessellator.addVertex(x1, z2, 0.0);
        tessellator.addVertex(x2, z2, 0.0);
        Tessellator.getInstance().draw();
        glShadeModel(GL_FLAT);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawSexyRect(double posX, double posY, double posX2, double posY2, int col1, int col2) {
        drawRect(posX, posY, posX2, posY2, col2);

        float alpha = (float) (col1 >> 24 & 255) / 255.0F;
        float red = (float) (col1 >> 16 & 255) / 255.0F;
        float green = (float) (col1 >> 8 & 255) / 255.0F;
        float blue = (float) (col1 & 255) / 255.0F;


        Tessellator tess = Tessellator.getInstance();
        WorldRenderer world = tess.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        glPushMatrix();
        glLineWidth(2);
        glBegin(GL_LINES);
        glVertex2d(posX, posY);
        glVertex2d(posX, posY2);
        glVertex2d(posX2, posY2);
        glVertex2d(posX2, posY);
        glVertex2d(posX, posY);
        glVertex2d(posX2, posY);
        glVertex2d(posX, posY2);
        glVertex2d(posX2, posY2);
        glEnd();
        glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        double var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        var10.startDrawingQuads();
        var10.addVertex((double) left, (double) bottom, 0.0D);
        var10.addVertex((double) right, (double) bottom, 0.0D);
        var10.addVertex((double) right, (double) top, 0.0D);
        var10.addVertex((double) left, (double) top, 0.0D);
        var9.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(double x, double y, double x1, double y1, double size, int borderC, int insideC) {
        drawRect(x + size, y + size, x1 - size, y1 - size, insideC);
        drawRect(x + size, y + size, x1, y, borderC);
        drawRect(x, y, x + size, y1, borderC);
        drawRect(x1, y1, x1 - size, y + size, borderC);
        drawRect(x + size, y1 - size, x1 - size, y1, borderC);
    }

    public static void drawTexturedRectangle(ResourceLocation resourceLocation, double posX, double posY, float width, float height,
                                             float r, float g, float b) {
        float u = 1, v = 1, uWidth = 1, vHeight = 1, textureWidth = 1, textureHeight = 1;
        glEnable(GL_BLEND);
        glColor4f(r, g, b, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        glBegin(GL_QUADS);
        glTexCoord2d(u / textureWidth, v / textureHeight);
        glVertex2d(posX, posY);
        glTexCoord2d(u / textureWidth, (v + vHeight) / textureHeight);
        glVertex2d(posX, posY + height);
        glTexCoord2d((u + uWidth) / textureWidth, (v + vHeight) / textureHeight);
        glVertex2d(posX + width, posY + height);
        glTexCoord2d((u + uWidth) / textureWidth, v / textureHeight);
        glVertex2d(posX + width, posY);
        glEnd();
        glDisable(GL_BLEND);
    }


}
