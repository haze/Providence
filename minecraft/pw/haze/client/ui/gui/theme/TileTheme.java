package pw.haze.client.ui.gui.theme;

import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.ui.gui.Node;
import pw.haze.client.ui.gui.Rectangle;
import pw.haze.client.ui.gui.SubNode;

import java.util.List;

/**
 * Created by apteryx on 4/3/2016.
 */
public class TileTheme implements GUITheme {
    @Override
    public void drawCategories(List<Node> categories, Node node, float mouseX, float mouseY) {

    }

    @Override
    public void drawModules(List<Node> categories, Node node, float mouseX, float mouseY) {

    }

    @Override
    public void drawDescription(ToggleableModule module, float mouseX, float mouseY) {

    }

    @Override
    public Rectangle generateButton(SubNode snode) {
        return null;
    }

    @Override
    public Rectangle generateRectangleBOTTOM(Node node) {
        return null;
    }

    @Override
    public Rectangle generateRectangleTOP(Node node) {
        return null;
    }
}
