package pw.haze.client.ui.gui.theme;

import org.lwjgl.input.Keyboard;
import pw.haze.client.Client;
import pw.haze.client.management.module.Category;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.management.module.interfaces.Displayable;
import pw.haze.client.ui.GuiUtil;
import pw.haze.client.ui.gui.Node;
import pw.haze.client.ui.gui.Rectangle;
import pw.haze.client.ui.gui.SubNode;
import pw.haze.client.util.Methods;
import pw.haze.client.util.NahrFont;

import java.util.List;
import java.util.Objects;

import static pw.haze.client.util.Methods.formatForCategory;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class FlatTheme implements GUITheme {


    private NahrFont font = new NahrFont("Hack Bold", 14);

    @Override
    public void drawDescription(ToggleableModule module, float mouseX, float mouseY) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            String desc = module.getDescription();
            if (!Objects.equals(desc.substring(desc.length() - 1), "."))
                desc += ".";
            float max = font.getStringWidth(desc);
            GuiUtil.drawRect(mouseX, mouseY - 3, mouseX + 8.5F + max, mouseY - 15, 0x801c1c1c);
            GuiUtil.drawRect(mouseX + 1, mouseY - 4, mouseX + 7.5F + max, mouseY - 14, 0x80383838);
            font.drawString(desc, mouseX + 4, mouseY - 15.5F, 0xFFEEEEEE, 0xFF1c1c1c);
        }
    }

    @Override
    public void drawCategories(List<Node> categories, Node node, float mouseX, float mouseY) {
        Rectangle top = generateRectangleTOP(node);
        Rectangle bottom = generateRectangleBOTTOM(node);
        Category cat = (Category) node.item;

        if (node.isExtended) {
            GuiUtil.drawRect(top.x, top.y, top.width, top.height, 0xFF232323);
            GuiUtil.drawRect(bottom.x, bottom.y, bottom.width, bottom.height - 1, 0xFF171717);
        } else {
            GuiUtil.drawRect(top.x, top.y, top.width, top.height, 0xFF232323);
            GuiUtil.drawRect(bottom.x, bottom.y, top.width, top.height - 1, 0xFF171717);
        }

        font.drawString(formatForCategory(cat.name()), node.x + 2, node.y - 1F, 0xFFEEEEEE, 0x20171717);
        if (node.isExtended)
            drawModules(categories, node, mouseX, mouseY);
    }

    @Override
    public Rectangle generateButton(SubNode snode) {
        Node<Category> parent = Client.getInstance().getGui().getParentNodeFor(snode);
        int indexIn = parent.subset.indexOf(snode) + 1;
        return new Rectangle(parent.x, parent.y + (indexIn * 10) + 1, parent.x + boxWidth - 1, parent.y + (indexIn * 10) + 11);
    }

    @Override
    public Rectangle generateRectangleBOTTOM(Node node) {
        return new Rectangle(node.x, node.y + 10, node.x + boxWidth, node.y + findLongestHeight(node) /* + boxHeight + 15 */);
    }

    @Override
    public Rectangle generateRectangleTOP(Node node) {
        return new Rectangle(node.x, node.y, node.x + boxWidth, node.y + 10);
    }

    private float findLongestHeight(Node<Category> node) {
        return (node.subset.size() * 10) + 13;
    }


    @Override
    public void drawModules(List<Node> categories, Node node, float mouseX, float mouseY) {
        float y = node.y + 11;
        for (SubNode<Module> snode : (List<SubNode<Module>>) node.subset) {
            ToggleableModule module = (ToggleableModule) snode.item;
            Rectangle button = generateButton(snode);
            if (Methods.inBounds(mouseX, mouseY, button)) {
                GuiUtil.drawRect(button.x, button.y, button.width, button.height, 0x601c1c1c);
            }
            font.drawString(module.getName(), node.x + 3F, y - 1, module.isRunning() ? ((Displayable) module).getColor() : 0xFFEEEEEE, 0x20171717);
            if (Methods.inBounds(mouseX, mouseY, button) && categories.indexOf(node) == categories.size() - 1)
                drawDescription(module, mouseX, mouseY);
            y += 10;
        }
    }
}