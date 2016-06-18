package pw.haze.client.ui.gui.theme;

import pw.haze.client.Client;
import pw.haze.client.management.module.Category;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.ui.GuiUtil;
import pw.haze.client.ui.gui.Node;
import pw.haze.client.ui.gui.Rectangle;
import pw.haze.client.ui.gui.SubNode;
import pw.haze.client.util.Methods;
import pw.haze.client.util.NahrFont;

import java.util.List;
import java.util.Objects;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class FalconTheme implements GUITheme {

    private NahrFont font = new NahrFont("Helvetica Neue", 14);

    @Override
    public void drawDescription(ToggleableModule module, float mouseX, float mouseY) {
        String desc = module.getDescription();
        if (!Objects.equals(desc.substring(desc.length() - 1), "."))
            desc += ".";
        float max = font.getStringWidth(desc);
        GuiUtil.drawBorderedRect(mouseX + 2, mouseY - 12, mouseX + max + 6, mouseY - 2.2, 0.5, 0x20555555, 0xaf000000);
        GuiUtil.drawGradientRect(mouseX + 1.8, mouseY - 12.2, mouseX + max + 6.2, mouseY - 2, 0x40626262, 0x208A8A8A);
        font.drawString(desc, mouseX + 4F, mouseY - 13.7F, NahrFont.FontType.SHADOW_THIN, 0xFFEEEEEE, 0xFF1c1c1c);
    }

    @Override
    public void drawCategories(List<Node> categories, Node node, float mouseX, float mouseY) {
        Rectangle top = generateRectangleTOP(node);
        Rectangle bottom = generateRectangleBOTTOM(node);
        Category cat = (Category) node.item;
        // GuiUtil.drawRect(top.x, top.y, top.width, top.height, 0xFF232323);
        // GuiUtil.drawRect(bottom.x, bottom.y, bottom.width, bottom.height - 1, 0xFF171717);
        if (node.isExtended) {
            GuiUtil.drawBorderedRect(top.x - 0.5, top.y - 0.5, top.width + 0.5, bottom.height + 0.5, 0.2, 0x20EEEEEE, 0x50555555);
            GuiUtil.drawBorderedRect(top.x, top.y, top.width, bottom.height, 0.5, 0x60000000, 0xaf000000);
        } else {
            GuiUtil.drawBorderedRect(top.x - 0.5, top.y - 0.5, top.width + 0.5, top.height + 0.5, 0.2, 0x20EEEEEE, 0x50555555);
            GuiUtil.drawBorderedRect(top.x, top.y, top.width, top.height, 0.5, 0x60000000, 0xaf000000);
        }
        font.drawString(cat.name(), node.x + 3, node.y - 1F, 0xFFEEEEEE, 0x20171717);
        if (node.isExtended)
            drawModules(categories, node, mouseX, mouseY);
    }

    @Override
    public Rectangle generateRectangleBOTTOM(Node node) {
        return new Rectangle(node.x, node.y + 10, node.x + boxWidth, node.y + findLongestHeight(node) - 1 /* + boxHeight + 15 */);
    }

    @Override
    public Rectangle generateRectangleTOP(Node node) {
        return new Rectangle(node.x, node.y, node.x + boxWidth, node.y + 10);
    }

    private float findLongestHeight(Node<Category> node) {
        return (node.subset.size() * 11) + 13;
    }

    @Override
    public void drawModules(List<Node> categories, Node node, float mouseX, float mouseY) {
        float y = node.y + 11;
        for (SubNode<Module> snode : (List<SubNode<Module>>) node.subset) {
            ToggleableModule module = (ToggleableModule) snode.item;
            Rectangle button = generateButton(snode);
            GuiUtil.drawBorderedRect(button.x - 0.2, button.y - 0.2, button.width + 0.2, button.height + 0.2, 0.5, 0x20555555, 0xaf000000);
            if (module.isRunning()) {
                GuiUtil.drawGradientRect(button.x, button.y, button.width, button.height, 0x60626262, 0x40717171);
            } else if (Methods.inBounds(mouseX, mouseY, button)) {
                //0x30555555
                GuiUtil.drawGradientRect(button.x, button.y, button.width, button.height, 0x40777777, 0x208A8A8A);
            } else {
                GuiUtil.drawGradientRect(button.x, button.y, button.width, button.height, 0x40626262, 0x208A8A8A);
            }
            font.drawCenteredString(module.getName(), button.x + ((boxWidth - 3) / 2), y - 1.5F, NahrFont.FontType.SHADOW_THICK, module.isRunning() ? 0xffff5555 : 0xff666666, 0xFF171717);
            if (Methods.inBounds(mouseX, mouseY, button) && categories.indexOf(node) == categories.size() - 1)
                drawDescription(module, mouseX, mouseY);
            y += 11;
        }
    }

    @Override
    public Rectangle generateButton(SubNode snode) {
        Node<Category> parent = Client.getInstance().getGui().getParentNodeFor(snode);
        int indexIn = parent.subset.indexOf(snode) + 1;
        return new Rectangle(parent.x + 2, parent.y + (indexIn * 11) - 0.5F, parent.x + boxWidth - 2, parent.y + (indexIn * 11) + 10);

    }
}
