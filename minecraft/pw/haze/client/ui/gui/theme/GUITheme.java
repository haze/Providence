package pw.haze.client.ui.gui.theme;

import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.ui.gui.Node;
import pw.haze.client.ui.gui.Rectangle;
import pw.haze.client.ui.gui.SubNode;

import java.util.List;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public interface GUITheme {

    float boxWidth = 80, boxHeight = 90;

    int DISABLED = 0xFFA40214;
    int DISABLED_TRANS = 0x20A40214;

    int ENABLED = 0xFF618A40;
    int ENABLED_TRANS = 0x20618A40;


    void drawCategories(List<Node> categories, Node node, float mouseX, float mouseY);

    void drawModules(List<Node> categories, Node node, float mouseX, float mouseY);

    void drawDescription(ToggleableModule module, float mouseX, float mouseY);

    Rectangle generateButton(SubNode snode);

    Rectangle generateRectangleBOTTOM(Node node);

    Rectangle generateRectangleTOP(Node node);


}
