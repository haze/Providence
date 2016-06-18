package pw.haze.client.util;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import pw.haze.client.Client;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.management.module.modules.Jesus;
import pw.haze.client.ui.gui.Rectangle;

import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;

public class Methods extends Gui {

    public static Minecraft mc = Minecraft.getMinecraft();
    private Gson gson = new Gson();

    public static void addPacket(Packet packet) {
        mc.thePlayer.sendQueue.addToSendQueueNoEvent(packet);
    }

    public static void sendPacket(Packet packet) {
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));

        }
        return baseSpeed;
    }

    public static void sendPlayerPacket(boolean onGround) {
        sendPlayerLookPacket(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, onGround);
    }

    public static boolean isStandingStill() {
        return mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0;
    }

    public static Block getBlockAbovePlayer(double y) {
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int yz = MathHelper.floor_double(mc.thePlayer.posY + y);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        return getBlock(x, yz, z);
    }

    public static Block getBlockAbovePlayer() {
        for (int i = 0; i < 256; i++) {
            int x = MathHelper.floor_double(mc.thePlayer.posX);
            int yz = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY + i);
            int z = MathHelper.floor_double(mc.thePlayer.posZ);
            if (getBlock(x, yz, z) != Blocks.air) {
                return getBlock(x, yz, z);
            }
        }
        return Blocks.air;
    }

    public static Block getBlockBelowPlayer() {
        for (int i = 0; i < 256; i++) {
            int x = MathHelper.floor_double(mc.thePlayer.posX);
            int yz = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY - i);
            int z = MathHelper.floor_double(mc.thePlayer.posZ);
            if (getBlock(x, yz, z) != Blocks.air) return getBlock(x, yz, z);
        }
        return Blocks.air;
    }

    public static boolean checkMovementInput() {
        return mc.gameSettings.keyBindForward.getIsKeyPressed() || mc.gameSettings.keyBindBack.getIsKeyPressed() || mc.gameSettings.keyBindLeft.getIsKeyPressed() || mc.gameSettings.keyBindRight.getIsKeyPressed() || mc.gameSettings.keyBindSneak.getIsKeyPressed();
    }

    public static Block getBlockBelowPlayer(double y) {
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int yz = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY - y);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        return getBlock(x, yz, z);
    }

    public static int getBlockMetadata(int x, int y, int z) {
        return mc.theWorld.getChunkFromBlockCoords(new BlockPos(x, y > 0 ? y : 1, z)).getBlockMetadata(new BlockPos(x, y > 0 ? y : 1, z));
    }


    public static boolean isModOn(Class<?> c) {
        Optional<Module> mod = Client.getInstance().getModuleManager().getOptionalModule(c);
        if (mod.isPresent()) {
            return ((ToggleableModule) mod.get()).isRunning();
        }
        return false;
    }

    public static Block getColliding(int radius) {
        if (!isModOn(Jesus.class)) {
            return null;
        }
        int mx = radius;
        int mz = radius;
        int max = radius;
        int maz = radius;

        if (mc.thePlayer.motionX > 0.01) {
            mx = 0;
        } else if (mc.thePlayer.motionX < -0.01) {
            max = 0;
        }
        if (mc.thePlayer.motionZ > 0.01) {
            mz = 0;
        } else if (mc.thePlayer.motionZ < -0.01) {
            maz = 0;
        }

        int xmin = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX - mx);
        int ymin = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY - 1);
        int zmin = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ - mz);
        int xmax = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX + max);
        int ymax = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY + 1);
        int zmax = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ + maz);
        for (int x = xmin; x <= xmax; ++x) {
            for (int y = ymin; y <= ymax; ++y) {
                for (int z = zmin; z <= zmax; ++z) {
                    Block block = getBlock(x, y, z);
                    if (block instanceof BlockLiquid && !mc.thePlayer.isInsideOfMaterial(Material.lava) && !mc.thePlayer.isInsideOfMaterial(Material.water)) {
                        return block;
                    }
                }
            }
        }
        return null;
    }

    public static boolean inLiquid() {
        if (mc.theWorld.handleMaterialAcceleration(mc.thePlayer.getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D), Material.water, mc.thePlayer)
                || mc.theWorld.handleMaterialAcceleration(mc.thePlayer.getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D), Material.lava, mc.thePlayer) || mc.thePlayer.isInWater()) {
            return true;
        }
        return false;
    }

    public static void damage(int hearts) {
        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        double[] d = {0.2D, 0.26D};
        for (int a = 0; a < hearts; a++) {
            for (int i = 0; i < d.length; i++) {
                addPlayerOffsetPacket(0, d[i], 0, false);
            }
        }
        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
    }

    public static boolean inBounds(float x, float y, float ix, float iy, float ibx, float iby) {
        return (x > ix && x < ibx) && (y > iy && y < iby);
    }

    public static boolean inBounds(float x, float y, Rectangle rect) {
        return inBounds(x, y, rect.x, rect.y, rect.width, rect.height);
    }

    public static void offset(double x, double y, double z) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
    }

    public static String formatForCategory(String str) {
        return String.valueOf(str.charAt(0)) +
                str.substring(1).toLowerCase();
    }

    public static void scissor(ScaledResolution res, double x, double y, double w, double h) {
        float factor = res.getScaleFactor();
        double x2 = x + w, y2 = y + h;
        GL11.glScissor((int) (x * factor), (int) ((res.getScaledHeight() - y2) * factor), (int) (w * factor), (int) (h * factor));
    }

    public static void addPlayerOffsetPacket(double x, double y, double z, boolean ground) {
        addPlayerLookPacket(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch, ground);
    }

    public static int getBlockID(Block block) {
        return Block.getIdFromBlock(block);
    }

    public static void addPlayerLookPacket(double posX, double posY, double posZ, float rotyaw, float rotpitch, boolean ground) {
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(posX, posY, posZ, rotyaw, rotpitch, ground));
    }

    public static Block getBlock(int x, int y, int z) {
        return mc.theWorld.getChunkFromBlockCoords(new BlockPos(x, y, z)).getBlock(new BlockPos(x, y, z));
    }

    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getChunkFromBlockCoords(pos).getBlock(pos);
    }

    public static Block getBlock(double x, double y, double z) {
        x = MathHelper.floor_double(x);
        y = MathHelper.floor_double(y);
        z = MathHelper.floor_double(z);
        return mc.theWorld.getChunkFromBlockCoords(new BlockPos(x, y, z)).getBlock(new BlockPos(x, y, z));
    }

    public static void sendPlayerLookPacket(double x, double y, double z, float yaw, float pitch, boolean ground) {
        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
    }

    public static void sendLookPacket(float yaw, float pitch, boolean b) {
        sendPlayerLookPacket(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, yaw, pitch, b);
    }

    public static boolean isContainerEmpty(Container container) {
        for (int i = 0, slotAmount = container.inventorySlots.size() == 90 ? 54 : 27; i < slotAmount; i++) {
            if (container.getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }

    public static int getNextSlotInContainer(Container container) {
        for (int i = 0, slotAmount = container.inventorySlots.size() == 90 ? 54 : 27; i < slotAmount; i++) {
            if (container.getInventory().get(i) != null) {
                return i;
            }
        }
        return -1;
    }

    public static void drawNukerESP(double x, double y, double z, float r, float g, float b) {
        double d = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX)
                * mc.timer.renderPartialTicks;
        double d1 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY)
                * mc.timer.renderPartialTicks;
        double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ)
                * mc.timer.renderPartialTicks;
        double d3 = x - d;
        double d4 = y - d1;
        double d5 = z - d2;
        preRender();
        GL11.glLineWidth(0.2F);
        GL11.glColor4f(MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * 1F,
                MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * 1F,
                MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * 1F, 0.2F);
        drawBox(d3, d4, d5, d3 + 1, d4 + 1, d5 + 1);
        GL11.glColor4f(MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * -1F,
                MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * -1F,
                MathHelper.sin((float) Minecraft.getSystemTime() / 300.0F) * -1F, 1F);
        drawOutlinedBox(d3, d4, d5, d3 + 1, d4 + 1, d5 + 1, 1.6F);
        postRender();
    }

    public static void drawSearchBlock(double x, double y, double z, double r, double g, double b) {
        double d = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX)
                * mc.timer.renderPartialTicks;
        double d1 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY)
                * mc.timer.renderPartialTicks;
        double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ)
                * mc.timer.renderPartialTicks;
        double d3 = x - d;
        double d4 = y - d1;
        double d5 = z - d2;
        preRender();
        GL11.glColor4d(r, g, b, 0.15);
        GL11.glLineWidth(0.2F);
        GL11.glColor3d(r, g, b);
        drawOutlinedBoundingBoxESP(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
        postRender();
    }

    public static void drawTracer(TileEntity e, double d, double f, double g) {
        double renderX = e.getPos().getX() - mc.getRenderManager().renderPosX;
        double renderY = e.getPos().getY() - mc.getRenderManager().renderPosY;
        double renderZ = e.getPos().getZ() - mc.getRenderManager().renderPosZ;
        preRender();
        prePointRender();
        GL11.glColor3d(d, f, g);
        GL11.glLineWidth(1.5F);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(0.0D, mc.thePlayer.isSneaking() ? 1.5395 : 1.6195D);
        GL11.glVertex3d(renderX, renderY, renderZ);
        GL11.glEnd();

        postPointRender();
        postRender();
    }

    public static void drawBox(double x, double y, double z, double x2, double y2, double z2) {
        glBegin(GL_QUADS);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z2);
        glEnd();

        glBegin(GL_QUADS);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z2);
        glEnd();

        glBegin(GL_QUADS);
        glVertex3d(x, y2, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y2, z2);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y2, z);
        glVertex3d(x, y2, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y2, z);
        glEnd();

        glBegin(GL_QUADS);
        glVertex3d(x, y, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y, z);
        glVertex3d(x, y, z2);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y, z);
        glEnd();

        glBegin(GL_QUADS);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z2);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z);
        glEnd();

        glBegin(GL_QUADS);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z2);
        glEnd();

    }

    public static void orientCamera() {
        float p_78467_1_ = mc.timer.elapsedPartialTicks;
        Entity var2 = mc.func_175606_aa();
        float var3 = var2.getEyeHeight();
        double var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * p_78467_1_;
        double var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * p_78467_1_ + var3;
        double var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * p_78467_1_;

        if (var2 instanceof EntityLivingBase && ((EntityLivingBase) var2).isPlayerSleeping()) {
            var3 = (float) (var3 + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);

            if (!mc.gameSettings.debugCamEnable) {
                BlockPos var27 = new BlockPos(var2);
                IBlockState var11 = mc.theWorld.getBlockState(var27);
                Block var29 = var11.getBlock();

                if (var29 == Blocks.bed) {
                    int var30 = ((EnumFacing) var11.getValue(BlockDirectional.AGE)).getHorizontalIndex();
                    GlStateManager.rotate(var30 * 90, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F,
                        -1.0F, 0.0F);
                GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, -1.0F,
                        0.0F, 0.0F);
            }
        } else if (mc.gameSettings.thirdPersonView > 0) {
            double var28 = 4 + (4 - 4) * p_78467_1_;

            if (mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0F, 0.0F, (float) (-var28));
            } else {
                float var12 = var2.rotationYaw;
                float var13 = var2.rotationPitch;

                if (mc.gameSettings.thirdPersonView == 2) {
                    var13 += 180.0F;
                }

                double var14 = -MathHelper.sin(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)
                        * var28;
                double var16 = MathHelper.cos(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)
                        * var28;
                double var18 = (-MathHelper.sin(var13 / 180.0F * (float) Math.PI)) * var28;

                for (int var20 = 0; var20 < 8; ++var20) {
                    float var21 = (var20 & 1) * 2 - 1;
                    float var22 = (var20 >> 1 & 1) * 2 - 1;
                    float var23 = (var20 >> 2 & 1) * 2 - 1;
                    var21 *= 0.1F;
                    var22 *= 0.1F;
                    var23 *= 0.1F;
                    MovingObjectPosition var24 = mc.theWorld.rayTraceBlocks(new Vec3(var4 + var21, var6 + var22,
                            var8 + var23), new Vec3(var4 - var14 + var21 + var23, var6 - var18 + var22, var8 - var16 + var23));

                    if (var24 != null) {
                        double var25 = var24.hitVec.distanceTo(new Vec3(var4, var6, var8));

                        if (var25 < var28) {
                            var28 = var25;
                        }
                    }
                }

                if (mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(var2.rotationPitch - var13, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(var2.rotationYaw - var12, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float) (-var28));
                GlStateManager.rotate(var12 - var2.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(var13 - var2.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
        }

        if (!mc.gameSettings.debugCamEnable) {
            GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, 1.0F, 0.0F,
                    0.0F);

            if (var2 instanceof EntityAnimal) {
                EntityAnimal var281 = (EntityAnimal) var2;
                GlStateManager.rotate(var281.prevRotationYawHead + (var281.rotationYawHead - var281.prevRotationYawHead) * p_78467_1_
                        + 180.0F, 0.0F, 1.0F, 0.0F);
            } else {
                GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F,
                        1.0F, 0.0F);
            }
        }

        GlStateManager.translate(0.0F, -var3, 0.0F);
        var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * p_78467_1_;
        var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * p_78467_1_ + var3;
        var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * p_78467_1_;
        // mc.entityRenderer.cloudFog = mc.renderGlobal.hasCloudFog(var4, var6, var8,
        // p_78467_1_);
    }

    public static void preRender() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        RenderHelper.disableStandardItemLighting();

    }

    public static void preRenderWithoutDepth() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        RenderHelper.disableStandardItemLighting();

    }

    public static void prePointRender() {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Methods.orientCamera();
    }

    public static void postPointRender() {
        // GL11.glColor3d(1, 1, 1);
        GlStateManager.popMatrix();
    }

    public static void postRender() {
        GL11.glColor3d(1, 1, 1);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.enableStandardItemLighting();
    }

    public static void postRenderWithoutDepth() {
        GL11.glColor3d(1, 1, 1);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.enableStandardItemLighting();
    }

    public static void drawOutlinedBox(double x, double y, double z, double x2, double y2, double z2, float l1) {
        glLineWidth(l1);

        glBegin(GL_LINES);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z2);
        glEnd();

        glBegin(GL_LINES);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z2);
        glEnd();

        glBegin(GL_LINES);
        glVertex3d(x, y2, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y2, z2);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y2, z);
        glVertex3d(x, y2, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y2, z);
        glEnd();

        glBegin(GL_LINES);
        glVertex3d(x, y, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y, z);
        glVertex3d(x, y, z2);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y, z);
        glEnd();

        glBegin(GL_LINES);
        glVertex3d(x, y, z);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z2);
        glVertex3d(x2, y, z2);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z);
        glEnd();

        glBegin(GL_LINES);
        glVertex3d(x, y2, z2);
        glVertex3d(x, y, z2);
        glVertex3d(x, y2, z);
        glVertex3d(x, y, z);
        glVertex3d(x2, y2, z);
        glVertex3d(x2, y, z);
        glVertex3d(x2, y2, z2);
        glVertex3d(x2, y, z2);
        glEnd();
    }

    public static void drawOutlinedBoundingBoxESP(AxisAlignedBB axisalignedbb) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glEnd();
    }

}
