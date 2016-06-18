package pw.haze.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil extends Gui {

    private static Minecraft mc = Minecraft.getMinecraft();


    public static void drawSearchBlock(double r, double g, double b, AxisAlignedBB bb) {
        Methods.preRender();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glColor4d(r, g, b, 0.4);
        drawSearchBlock(bb);
        Methods.postRender();
    }

    public static void drawOutlineSearch(double r, double g, double b, AxisAlignedBB bb) {
        glColor4d(r, g, b, 0.7);
        glEnable(GL_LINES);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);

        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);

        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);

        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);

		/* done */

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);

        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);

        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);

		/* back? */

        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);

        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);

        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);

        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);


        glEnd();

    }

    public static void drawSearchBlock(AxisAlignedBB bb) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer world = Tessellator.getInstance().getWorldRenderer();
        world.startDrawingQuads();
//		world.startDrawing(GL_POLYGON);
        world.addVertex(bb.minX, bb.maxY, bb.minZ);
        world.addVertex(bb.maxX, bb.maxY, bb.minZ);
        world.addVertex(bb.maxX, bb.minY, bb.minZ);
        world.addVertex(bb.minX, bb.minY, bb.minZ);
        tess.draw();

        world.startDrawingQuads();
        world.addVertex(bb.minX, bb.maxY, bb.maxZ);
        world.addVertex(bb.minX, bb.maxY, bb.minZ);
        world.addVertex(bb.minX, bb.minY, bb.minZ);
        world.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.draw();

        world.startDrawingQuads();
        world.addVertex(bb.maxX, bb.maxY, bb.minZ);
        world.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        world.addVertex(bb.maxX, bb.minY, bb.maxZ);
        world.addVertex(bb.maxX, bb.minY, bb.minZ);
        tess.draw();

        world.startDrawingQuads();
        world.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        world.addVertex(bb.minX, bb.maxY, bb.maxZ);
        world.addVertex(bb.minX, bb.minY, bb.maxZ);
        world.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tess.draw();

        world.startDrawingQuads();
        world.addVertex(bb.maxX, bb.maxY, bb.minZ);
        world.addVertex(bb.minX, bb.maxY, bb.minZ);
        world.addVertex(bb.minX, bb.maxY, bb.maxZ);
        world.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        tess.draw();

        world.startDrawingQuads();
        world.addVertex(bb.minX, bb.minY, bb.minZ);
        world.addVertex(bb.maxX, bb.minY, bb.minZ);
        world.addVertex(bb.maxX, bb.minY, bb.maxZ);
        world.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.draw();

    }

    private static void drawOutlinedBoundingBoxESP(AxisAlignedBB axisalignedbb) {
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        glEnd();
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glEnd();
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        glEnd();
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glEnd();
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        glEnd();
        glBegin(GL_LINES);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        glEnd();
    }

}
