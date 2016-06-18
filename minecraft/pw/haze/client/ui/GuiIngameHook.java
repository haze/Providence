package pw.haze.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import pw.haze.client.Client;
import pw.haze.client.events.EventHudDraw;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.management.module.interfaces.Displayable;
import pw.haze.client.ui.tab.TabUi;
import pw.haze.event.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Haze on 6/4/2015.
 */
public class GuiIngameHook extends GuiIngame {

    boolean right;
    boolean tag;
    boolean tabui;
    private Minecraft mc;
    private ScaledResolution resoultion;

    public GuiIngameHook(Minecraft minecraft) {
        super(minecraft);
        this.mc = minecraft;
    }

    @Override
    public void func_175180_a(float p_175180_1_) {

        this.right = Client.getInstance().getModuleManager().right.get();
        this.tag = Client.getInstance().getModuleManager().tag.get();
        Optional<Module> tabui = Client.getInstance().getModuleManager().getOptionalModule(TabUi.class);
        this.tabui = tabui.isPresent() && ((TabUi) tabui.get()).isRunning();

        super.func_175180_a(p_175180_1_);
        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        if (Client.getInstance().isEnabled) {
            if (mc.gameSettings.showDebugInfo) {
                return;
            }
            resoultion = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();

            drawWaterMartkAndCoordinates();
            EventManager.getInstance().fire(new EventHudDraw());
            drawArrayList();

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
    }

    private void drawWaterMartkAndCoordinates() {
        /* watermark */
        if (!Client.getInstance().getModuleManager().tag.get()) {
            mc.fontRendererObj.func_175063_a(Client.NAME, 2, 2, 0xFFfffae8);
        }
        /* coordinates */
        if (Client.getInstance().getModuleManager() != null) {
            if (Client.getInstance().getModuleManager().coords.get()) {
                String coordinates = String.format("(%s,%s,%s)", (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                mc.fontRendererObj.func_175063_a(coordinates, 2, resoultion.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2 - (this.getChatGUI().getChatOpen() ? 12 : 0), 0xFFfffae8);
            }
        }
    }

    private void drawArrayList() {
        int y = 2;
        if (right) {

            Optional<Module> tabui = Client.getInstance().getModuleManager().getOptionalModule(TabUi.class);
            int tabuiY = 0;
            if (tabui.isPresent()) {
                tabuiY = ((TabUi) tabui.get()).y;
            }

            y += this.tabui ? tabuiY : 0;
            y += !tag ? this.tabui ? 2 : 9 : this.tabui ? 6 : 0;
        }
        List<ToggleableModule> modules = new ArrayList<>(Client.getInstance().getModuleManager().getEnabledModules());
        modules = modules.stream().filter(m -> m instanceof Displayable).collect(Collectors.toList());
        modules.sort((m1, m2) -> mc.fontRendererObj.getStringWidth(m2.getName() + ((Displayable) m2).getExtras()) - mc.fontRendererObj.getStringWidth(m1.getName() + ((Displayable) m1).getExtras()));
        for (ToggleableModule module : modules) {
            if (module instanceof Displayable && module.isVisible()) {
                mc.fontRendererObj.func_175065_a(module.getName() + "\2477" + ((Displayable) module).getExtras(), right ? 2 : resoultion.getScaledWidth() - mc.fontRendererObj.getStringWidth(module.getName() + ((Displayable) module).getExtras()) - 2, y, ((Displayable) module).getColor(), true);
                y += 10;
            }
        }
    }

}
