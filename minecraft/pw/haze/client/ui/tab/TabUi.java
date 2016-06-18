package pw.haze.client.ui.tab;

import net.minecraft.client.Minecraft;
import pw.haze.client.Client;
import pw.haze.client.events.EventHudDraw;
import pw.haze.client.events.EventTick;
import pw.haze.client.management.action.Action;
import pw.haze.client.management.action.Task;
import pw.haze.client.management.action.tasks.NavigateTask;
import pw.haze.client.management.module.Category;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.ui.GuiUtil;
import pw.haze.client.ui.tab.util.TabDirection;
import pw.haze.event.annotation.EventMethod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Haze on 6/20/2015.
 */
public class TabUi extends ToggleableModule {

    public int y;
    private List<Tab> tabList;
    private Tab activeTab;
    private ModuleContent moduleContent;


    public TabUi() {
        super("TabUI", "The Tab-UI", "NONE", Category.CORE);
        Client.getInstance().getActionManager().getMap()
                .put(new Action[]{Action.KEY_PRESS}, new Task[]{new NavigateTask(this)});
        initGui();
    }

    public void initGui() {
        int helper = 0;
        tabList = new CopyOnWriteArrayList<>();
        for (Category category : Category.values()) {
            if (category != Category.CORE && !Client.getInstance().getModuleManager().getModulesInCategory(category).isEmpty()) {
                try {
                    tabList.add(new Tab<>(Client.getInstance().getModuleManager().getModulesInCategory(category), category, helper));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                helper++;
            }
            if (tabList != null)
                activeTab = tabList.get(0);
        }
    }

    @EventMethod(EventTick.class)
    public void logic(EventTick e) {
        if (tabList != null) {
            if (!tabList.isEmpty()) {
                try {
                    Tab firstTab = tabList.get(0);
                    Tab lastTab = tabList.get(tabList.size() - 1);
                    if (activeTab.id > lastTab.id) {
                        activeTab = firstTab;
                    } else if (activeTab.id < firstTab.id) {
                        activeTab = lastTab;
                    }
                    if (!activeTab.isSelected()) {
                        for (Tab t : tabList) {
                            if (t == activeTab) {
                                t.setIsHover(true);
                            } else {
                                t.setIsHover(false);
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public void shiftTab(TabDirection dir) {
        if (tabList != null) {
            if (!tabList.isEmpty()) {
                Tab firstTab = tabList.get(0);
                Tab lastTab = tabList.get(tabList.size() - 1);
                ToggleableModule lastModule = null, firstModule = null;
                if (activeTab.isSelected()) {
                    lastModule = (ToggleableModule) activeTab.getTabContents().get(activeTab.getTabContents().size() - 1);
                    firstModule = (ToggleableModule) activeTab.getTabContents().get(0);
                }
                switch (dir) {
                    case UP:
                        if (!activeTab.isSelected()) {
                            if (activeTab.id == 0) {
                                activeTab = lastTab;
                            } else {
                                activeTab = tabList.get(activeTab.id - 1);
                            }
                            break;
                        } else {
                            if (moduleContent.getId() - 1 < 0) {
                                moduleContent.setId(activeTab.getTabContents().size() - 1);
                                moduleContent.setModule(lastModule);
                            } else {
                                moduleContent.setId(moduleContent.getId() - 1);
                                moduleContent.setModule((ToggleableModule) activeTab.getTabContents().get(moduleContent.getId()));
                            }
                            break;
                        }
                    case DOWN:
                        if (!activeTab.isSelected()) {
                            if (activeTab.id + 1 > tabList.size() - 1) {
                                activeTab = firstTab;
                            } else {
                                activeTab = tabList.get(activeTab.id + 1);
                            }
                            break;
                        } else {
                            if (moduleContent.getId() + 1 > activeTab.getTabContents().size() - 1) {
                                moduleContent.setId(0);
                                moduleContent.setModule(firstModule);
                            } else {
                                moduleContent.setId(moduleContent.getId() + 1);
                                moduleContent.setModule((ToggleableModule) activeTab.getTabContents().get(moduleContent.getId()));
                            }
                            break;
                        }
                    case RIGHT:
                        if (!activeTab.isSelected() && activeTab.getIsHover()) {
                            activeTab.setIsSelected(true);
                            moduleContent = new ModuleContent((ToggleableModule) activeTab.getTabContents().get(0), 0);
                        } else {
                            /* if is hovering, enable */
                            moduleContent.getModule().toggle();
                        }
                        break;
                    case LEFT:
                        if (activeTab.isSelected()) {
                            activeTab.setIsSelected(false);
                        }
                        break;
                }
            }
        }
    }

    @EventMethod(EventHudDraw.class)
    public void drawHud(EventHudDraw e) {
        if (tabList != null) {
            boolean tag = Client.getInstance().getModuleManager().tag.get();
            int y = (8 * tabList.size()) + (tag ? -2 : 11);
            this.y = y;
            int extendY = (8 * activeTab.getTabContents().size() + 1) + (tag ? -2 : 7);
            GuiUtil.drawSexyRect(2, tag ? 2 : 11, getMaxWidth(), y + (tag ? 6 : 2), 0x20232323, 0x40232323);
            for (Tab tab : tabList) {
                if (tab.isSelected()) {
                    GuiUtil.drawSexyRect(getMaxWidth() + 4, 8 * (tabList.indexOf(tab) + 1) + (tag ? -2 : 7), getMaxWidth() + 4 + getMaxWidthForTab(tab) + 4, 8 * (tabList.indexOf(tab) + 1) + 9 + extendY - 6, 0x20232323, 0x40232323);
                    for (Object module : tab.getTabContents()) {
                        {
                            if (module instanceof ToggleableModule) {
                                ToggleableModule mod = (ToggleableModule) module;
                                int modMaxY = (tag ? -9 : 0) + (8 * (tabList.indexOf(tab) + 1)) + 8 + (8 * tab.getTabContents().indexOf(module) + 1);
                                Minecraft.getMinecraft().fontRendererObj.func_175065_a(formatForModule(mod), getMaxWidth() + 6, modMaxY, mod.isRunning() ? 0xFFfcffd0 : 0xFFeee6ee, true);
                                if (modMaxY > y) {
                                    this.y = (tag ? -5 : 8) + (8 * (tabList.indexOf(tab) + 1)) + 8 + (8 * tab.getTabContents().indexOf(module) + 1);
                                }
                            }
                        }
                    }
                }
                Minecraft.getMinecraft().fontRendererObj.func_175065_a(formatTab(tab), 4, 8 * (tabList.indexOf(tab) + 1) + (tag ? -5 : 4), 0xFFeee6ee, true);
            }
        }
    }

    private float getMaxWidthForTab(Tab tab) {
        String longest = "n/a";
        for (Object o : tab.getTabContents()) {
            if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(formatForModule(((Module) o))) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(longest)) {
                longest = formatForModule(((Module) o));
            }
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(longest);
    }

    public String formatForModule(Module module) {
        return module.getName() + (module == moduleContent.getModule() ? " <" : "");
    }

    public String formatTab(Tab tab) {
        int helper = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : tab.getTab().toString().toCharArray()) {
            if (helper == 0) {
                builder.append(Character.toUpperCase(c));
            } else {
                builder.append(Character.toLowerCase(c));
            }
            helper++;
        }
        return builder.append(openString(tab)).toString();
    }

    public String openString(Tab t) {
        if (t != null) {
            return t.isSelected() ? " >" : t.getIsHover() ? " <" : "";
        }
        return "n/a";
    }

    public String longestString(Tab t) {
        String longest = "n/a";
        for (Object m : t.tabContents) {
            if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(((Module) m).getName()) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(longest)) {
                longest = ((Module) m).getName();
            }
        }
        return longest;
    }

    public float getMaxWidth() {
        String longest = "n/a";
        for (Tab t : tabList) {
            if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(t.getTab().toString()) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(longest)) {
                longest = formatTab(t);
            }
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(longest) + 8;
    }
}
