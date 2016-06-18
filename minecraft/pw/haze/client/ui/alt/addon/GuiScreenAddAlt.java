package pw.haze.client.ui.alt.addon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import pw.haze.client.Client;
import pw.haze.client.management.alt.Account;

import java.io.IOException;

/**
 * |> Author: haze
 * |> Since: 3/19/16
 */
public class GuiScreenAddAlt extends GuiScreen {

    public GuiScreen parent;

    private GuiTextField usernameField;
    private GuiTextField passwordField;

    private GuiButton doneButton;

    public GuiScreenAddAlt(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        this.usernameField = new GuiTextField(0, this.fontRendererObj, (this.width / 2) - 100, 66, 200, 20);
        this.passwordField = new GuiTextField(0, this.fontRendererObj, (this.width / 2) - 100, 108, 200, 20);
        this.usernameField.setFocused(true);
        this.doneButton = new GuiButton(0, (this.width / 2) - 100, 138, 200, 20, "Add");
        this.buttonList.add(this.doneButton);
        this.buttonList.add(new GuiButton(1, (this.width / 2) - 100, 168, 200, 20, "Cancel"));
        super.initGui();
    }

    @Override
    public void updateScreen() {
        this.doneButton.enabled = this.usernameField.getText().length() != 0;
        this.usernameField.updateCursorCounter();
        this.passwordField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        this.usernameField.textboxKeyTyped(typedChar, keyCode);
        this.passwordField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 15) {
            this.usernameField.setFocused(!this.usernameField.isFocused());
            this.passwordField.setFocused(!this.passwordField.isFocused());
        }

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }

        if (keyCode == 1)
            mc.displayGuiScreen(parent);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                Client.getInstance().getAccountManager().addAccount(new Account(this.usernameField.getText(), this.passwordField.getText()));
            case 1:
                mc.displayGuiScreen(parent);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.fontRendererObj.func_175063_a("Username", (this.width / 2) - 100, this.usernameField.yPosition - 11, 0xFFEEEEEE);
        this.fontRendererObj.func_175063_a("Password", (this.width / 2) - 100, this.passwordField.yPosition - 11, 0xFFEEEEEE);
        this.passwordField.drawTextBox();
        this.usernameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
