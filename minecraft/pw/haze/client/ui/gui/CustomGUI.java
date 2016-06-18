package pw.haze.client.ui.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import pw.haze.client.Client;
import pw.haze.client.management.module.Category;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.management.module.modules.GuiToggle;
import pw.haze.client.management.value.Value;
import pw.haze.client.ui.gui.theme.GUITheme;
import pw.haze.client.util.Methods;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


/**
 * |> Author: haze
 * |> Since: 3/16/16
 */
public class CustomGUI extends GuiScreen {

    private final float boxWidth = 80, boxHeight = 90;
    public GuiToggle toggle;
    private Minecraft mc;
    private ScaledResolution res;
    private List<Node> mainNodes;

    public CustomGUI(Minecraft mc, GuiToggle toggle) {
        this.mc = mc;
        this.toggle = toggle;
        if (this.mc != null)
            this.res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
    }

    public GUITheme getTheme() {
        return toggle.style;
    }

    @Override
    public void initGui() {
        this.mainNodes = new CopyOnWriteArrayList<>();
        float x = 15, y = 15;
        for (Category c : Category.values()) {
            if (c == Category.CORE) continue;
            if (x + boxWidth + 5 > res.getScaledWidth()) {
                y += (boxHeight + 20);
                x = 15;
            } else {
                if (Arrays.asList(Category.values()).indexOf(c) != 0)
                    x += (boxWidth + 5);
            }
            mainNodes.add(new Node<>(c,
                    Client.getInstance().getModuleManager().getModulesInCategory(c).stream()
                            .filter(mod -> mod instanceof ToggleableModule)
                            .map(this::moduleToNode)
                            .collect(Collectors.toList()), x, y));
        }
        load();
        super.initGui();
    }


    public boolean isAnyNodeBeingDragged() {
        return this.mainNodes.stream().anyMatch(n -> n.isBeingDragged);
    }

    public Node getTopNode() {
        return this.mainNodes.get(this.mainNodes.size() - 1);
    }

    private void setTopNode(Node suggested) {
        if (this.mainNodes.contains(suggested)) {
            this.mainNodes.remove(suggested);
            this.mainNodes.add(suggested);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        this.toggle.setState(false, false, false);
        super.onGuiClosed();
    }

    public void load() {
        Optional<Module> toggleModule = Client.getInstance().getModuleManager().getOptionalModule(GuiToggle.class);
        if (toggleModule.isPresent()) {
            GuiToggle toggle = (GuiToggle) toggleModule.get();
            for (String str : toggle.savePos.get().split(":")) {
                String data[] = str.split("/");
                if (Objects.equals(data[0].trim(), "")) return;
                Category category = Category.valueOf(data[0]);
                String floatData[] = data[1].split("\\|");
                boolean isExtended = Boolean.parseBoolean(data[2]);
                float x = Float.parseFloat(floatData[0]);
                float y = Float.parseFloat(floatData[1]);
                this.mainNodes.stream().filter(node -> node.item == category).forEach(node -> {
                    node.x = x;
                    node.y = y;
                    node.isExtended = isExtended;
                });
            }
        }
    }

    public String getPos() {
        StringBuilder builder = new StringBuilder();
        if (this.mainNodes != null) {
            for (Node<Category> node : this.mainNodes) {
                builder.append(String.format("%s/%sF|%sF/%s:", ((Category) node.item).name(), node.x, node.y, node.isExtended));
            }
        }
        return builder.toString();
    }

    private void updateTopNode() {
        Node top = null;

        for (Node node : this.mainNodes) {
            if (node.isBeingDragged) {
                top = node;
                break;
            }
        }

        setTopNode(top);

    }

    private void fixNodesForResize() {
        for (Node<Category> node : this.mainNodes) {
            node.y *= res.getScaleFactor();
            node.x *= res.getScaleFactor();
        }
    }

    private void drawNode(Node<Category> node, float mouseX, float mouseY) {
        /*
        1) DRAW NAME
        2) DRAW NODES
        3) DRAW NODES -> NODES ???
         */
        updateTopNode();

        if (node.isBeingDragged) {
            // panel.dragX = (x - panel.lastX);
            // panel.dragY = (y - panel.lastY);
            node.x = (mouseX - node.lastX);
            node.y = (mouseY - node.lastY);
        }

         /* if (node.x < 0 || node.x > res.getScaledWidth())
            node.x = 15;
        if (node.y < 0 || node.y > res.getScaledHeight()) {
            node.y = lastSetY + boxHeight + 20;
            this.lastSetY = node.y;
        } */


        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        getTheme().drawCategories(this.mainNodes, node, mouseX, mouseY);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        // GuiUtil.drawSexyRect(top.x, top.y, top.width, top.height, 0x20171717, 0xFFEEEEEE);
        // GuiUtil.drawSexyRect(bottom.x, bottom.y, bottom.width, bottom.height, 0x20171717, 0xFFEEEEEE);

    }

    private SubNode<ToggleableModule> moduleToNode(ToggleableModule m) {
        List<SubNode<?>> subset = new ArrayList<>();
        Optional<Value[]> values = Client.getInstance().getValueManager().getValuesFromModule(m);
        if (values.isPresent()) {
            subset = Arrays.stream(values.get())
                    .map(this::valueToNode)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new SubNode<>(m, subset);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            List<Node> mainNodes1 = this.mainNodes;
            for (int i = mainNodes1.size() - 1; 0 <= i; i--) {
                Node<Category> node = mainNodes1.get(i);
                Rectangle top = this.getTheme().generateRectangleTOP(node);
                Rectangle bottom = this.getTheme().generateRectangleBOTTOM(node);

                if (Methods.inBounds(mouseX, mouseY, top)) {
                    node.isBeingDragged = true;
                    node.lastX = mouseX - node.x;
                    node.lastY = mouseY - node.y;
                    break;
                }

                if (Methods.inBounds(mouseX, mouseY, bottom) && node.isExtended) {
                    setTopNode(node);

                    for (SubNode snode : (List<SubNode>) node.subset) {
                        Rectangle button = getTheme().generateButton(snode);
                        if (Methods.inBounds(mouseX, mouseY, button)) {
                            ToggleableModule module = (ToggleableModule) snode.item;
                            module.toggle();
                            return;
                        }
                    }
                }
            }
        } else if (mouseButton == 1) {
            List<Node> mainNodes1 = this.mainNodes;
            for (int i = mainNodes1.size() - 1; 0 <= i; i--) {
                Node<Category> node = mainNodes1.get(i);
                Rectangle top = getTheme().generateRectangleTOP(node);

                if (Methods.inBounds(mouseX, mouseY, top)) {
                    node.isExtended = !node.isExtended;
                    break;
                }
            }
        }
    }

    public <T> Node<T> getParentNodeFor(SubNode node) {
        Optional<Node> parent = this.mainNodes.stream().filter((n) -> n.subset.contains(node)).findAny();
        return parent.isPresent() ? parent.get() : null;
    }

    @Override
    public void func_175273_b(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
        super.func_175273_b(mcIn, p_175273_2_, p_175273_3_);
        res = new ScaledResolution(mcIn, mc.displayWidth, mc.displayHeight);
        fixNodesForResize();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (Node<Category> category : this.mainNodes) {
            category.isBeingDragged = false;
        }
    }

    private SubNode<Value> valueToNode(Value v) {
        return new SubNode<>(v, new ArrayList<>());
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mainNodes.stream().forEach((node) -> drawNode(node, mouseX, mouseY));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
