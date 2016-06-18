package pw.haze.client.ui.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import pw.haze.client.Client;
import pw.haze.client.management.alt.Account;
import pw.haze.client.ui.GuiUtil;
import pw.haze.client.ui.alt.addon.GuiScreenAddAlt;
import pw.haze.client.ui.gui.Rectangle;
import pw.haze.client.util.Methods;
import pw.haze.client.util.NahrFont;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class AccountItem {
    public float x, y;
    public Account account;
    public boolean isSelected;

    public AccountItem(float x, float y, Account account) {
        this.x = x;
        this.y = y;
        this.account = account;
    }

    public Rectangle getBounds() {

        return new Rectangle(this.x, this.y, new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight).getScaledWidth() - 30, this.y + 25);
    }

    public boolean isHovering(float mouseX, float mouseY) {
        return Methods.inBounds(mouseX, mouseY, getBounds());
    }
}

/**
 * |> Author: haze
 * |> Since: 3/19/16
 */
public class GUIAltScreen extends GuiScreen {

    private final float ACC_ITEM_START_X = 25;
    private final float ACC_ITEM_START_Y = 25;
    private Minecraft mc;
    private float add = 0;
    private GuiButton addButton;
    private GuiButton deleteButton;

    private ScaledResolution res;
    private NahrFont font;
    private boolean needsToUpdate = false;
    private List<AccountItem> accountItemList;

    public GUIAltScreen(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.drawDefaultBackground();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GuiUtil.drawSexyRect(18, 18, res.getScaledWidth() - 18, res.getScaledHeight() - 48, 0xFF232323, 0xF2b9b9b9);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Methods.scissor(res, 18, 19, res.getScaledWidth() - 18, res.getScaledHeight() - 67);
        //GuiUtil.drawBorderedRect(20, 20, res.getScaledWidth() - 20, res.getScaledHeight() - 40, 0.2, 0xFF232323, 0x80171717);
        // this.scrollbar.draw(mouseX, mouseY);

        int dWheel = Mouse.getDWheel();
        // shiftList(dWheel / 17F);


        for (AccountItem item : this.accountItemList) {
            Rectangle bounds = item.getBounds();
            if (item.isSelected) {
                GuiUtil.drawBorderedRect(bounds.x - 1, bounds.y - 1 + add, bounds.width, bounds.height + add, 0.5, 0xFF232323, 0xD9FFFFFF);
                font.drawString(item.account.isOffline() ? "Offline" : "Premium", item.x + 2, item.y + add + (25 / 3), NahrFont.FontType.SHADOW_THIN, item.account.isOffline() ? 0xFFca1a0e : 0xFF618a40, 0x80000000);
                font.drawString(item.account.getUsername(), item.x + 2, item.y + add + ((25 / 3) * -0.2F), NahrFont.FontType.SHADOW_THIN, 0xFFFFCF79, 0x80000000);
            } else {
                if (item.isHovering(mouseX, mouseY)) {
                    GuiUtil.drawBorderedRect(bounds.x - 1, bounds.y - 1 + add, bounds.width, bounds.height + add, 0.2, 0xD9EEEEEE, 0xD9FFFFFF);
                    font.drawString(item.account.isOffline() ? "Offline" : "Premium", item.x + 1, item.y + add + (25 / 3) + 1, NahrFont.FontType.SHADOW_THIN, item.account.isOffline() ? 0xFFca1a0e : 0xFF618a40, 0x80000000);
                    font.drawString(item.account.getUsername(), item.x + 1, item.y + add + ((25 / 3) * -0.2F), NahrFont.FontType.SHADOW_THIN, 0xFFFFCF79, 0x80000000);
                } else {
                    GuiUtil.drawBorderedRect(bounds.x, bounds.y + add, bounds.width, bounds.height + add, 0.2, 0xD9EEEEEE, 0xD9d5d9d8);
                    font.drawString(item.account.isOffline() ? "Offline" : "Premium", item.x + 2, item.y + add + (25 / 3), NahrFont.FontType.SHADOW_THIN, item.account.isOffline() ? 0xFFca1a0e : 0xFF618a40, 0x80000000);
                    font.drawString(item.account.getUsername(), item.x + 2, item.y + add + ((25 / 3) * -0.2F), NahrFont.FontType.SHADOW_THIN, 0xFFFFCF79, 0x80000000);
                }
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    public boolean isOneSelected() {
        return this.accountItemList.stream().filter(ai -> ai.isSelected).findAny().isPresent();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (AccountItem item : this.accountItemList) {
                if (item.isHovering(mouseX, mouseY)) {
                    item.isSelected = !item.isSelected;
                    this.accountItemList.stream().filter(ai -> ai != item).forEach(ai -> ai.isSelected = false);
                    break;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiScreenAddAlt(this));
                break;
            case 1:
                Optional<AccountItem> selectedItem = getSelectedAccountItem();
                if (selectedItem.isPresent()) {
                    AccountItem item = selectedItem.get();
                    this.accountItemList.remove(item);
                    Client.getInstance().getAccountManager().removeAccount(item.account);
                }
                break;
        }
        needsToUpdate = true;
        super.actionPerformed(button);
    }

    public void update(boolean bypass) {
        if (bypass || needsToUpdate) {
            float x = ACC_ITEM_START_X;
            float y = ACC_ITEM_START_Y;
            this.accountItemList.clear();
            for (Account acc : Client.getInstance().getAccountManager().getContents()) {
                this.accountItemList.add(new AccountItem(x, y, acc));
                y += 30;
            }
            needsToUpdate = false;
        }
    }

    public void update() {
        update(false);
    }

    public Optional<AccountItem> getSelectedAccountItem() {
        return this.accountItemList.stream().filter(ai -> ai.isSelected).findFirst();
    }


    @Override
    public void initGui() {
        this.font = new NahrFont("Helvetica Neue Bold", 20);
        this.accountItemList = new ArrayList<>();
        this.res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.addButton = new GuiButton(0, 20, res.getScaledHeight() - 30, 150, 20, "Add Account");
        this.deleteButton = new GuiButton(1, 20, res.getScaledHeight() - 30, 150, 20, "Delete Account");
        deleteButton.visible = false;
        this.buttonList.add(addButton);
        this.buttonList.add(deleteButton);
        update(true);
        super.initGui();
    }

    @Override
    public void updateScreen() {
        if (isOneSelected()) {
            addButton.visible = false;
            deleteButton.visible = true;
        } else {
            addButton.visible = true;
            deleteButton.visible = false;
        }

        update();

        super.updateScreen();
    }
}
