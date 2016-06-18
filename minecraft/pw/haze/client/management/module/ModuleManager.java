package pw.haze.client.management.module;

import pw.haze.client.Client;
import pw.haze.client.events.EventModuleToggle;
import pw.haze.client.events.EventShutdown;
import pw.haze.client.interfaces.Loadable;
import pw.haze.client.interfaces.Savable;
import pw.haze.client.management.ListManager;
import pw.haze.client.management.command.Command;
import pw.haze.client.management.module.modules.*;
import pw.haze.client.management.value.Value;
import pw.haze.client.management.value.ValueHelper;
import pw.haze.client.ui.tab.TabUi;
import pw.haze.event.EventManager;
import pw.haze.event.annotation.EventMethod;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Haze on 6/4/2015.
 */
public class ModuleManager extends ListManager<Module> implements Loadable, Savable {


    public Value<Boolean> coords;
    public Value<Boolean> tag;
    public Value<Boolean> right;
    private List<String> core;

    public ModuleManager() {
        super("Modules");
        EventManager.getInstance().registerAll(this);
        this.coords = new Value<>(true, "coordinates");
        this.tag = new Value<>(true, "tag");
        this.right = new Value<>(true, "right");
        Client.getInstance().getValueManager().getMap().put(new ValueHelper("Misc", this), new Value[]{coords, tag, right});
        this.core = new ArrayList<>();
        this.core.add("key");
        this.core.add("state");
        this.core.add("visible");
    }

    @Command("coords")
    public String toggleMobs(Optional<Boolean> bool) {
        this.coords.setValue(bool.isPresent() ? bool.get() : !this.coords.get());
        return String.format("Successfully turned Coordinates %s", this.coords.get());
    }

    @Command("tag")
    public String toggleTag(Optional<Boolean> bool) {
        this.tag.setValue(bool.isPresent() ? bool.get() : !this.tag.get());
        return String.format("Successfully turned tag %s", !this.tag.get() ? "on" : "off");
    }

    @Command("arr")
    public String toggleRight(Optional<Boolean> bool) {
        this.right.setValue(bool.isPresent() ? bool.get() : !this.right.get());
        return String.format("Successfully rendering modules %s", !this.right.get() ? "on the right" : "on the left");
    }

    @Command("load")
    public String loadClient() {
        try {
            load();
            return "Successfully re-loaded.";
        } catch (Throwable t) {
            t.printStackTrace();
            return "Failed to load [" + t.getClass().getSimpleName() + "]";
        }
    }

    @Command({"toggle", "t"})
    public String toggle(String module) {
        Optional<Module> optional = getOptionalModuleName(module);
        if (optional.isPresent()) {
            if (optional.get() instanceof ToggleableModule) {
                ToggleableModule actualModule = (ToggleableModule) optional.get();
                actualModule.toggle();
                return String.format("Toggled module %s %s", module, actualModule.isRunning() ? "on" : "off");
            } else {
                return String.format("Module %s is not toggleable", module);
            }
        } else {
            return String.format("Module %s is not present", module);
        }
    }

    public List<ToggleableModule> getEnabledModules() {
        return this.getContents().stream().filter(x -> x instanceof ToggleableModule)
                .filter(z -> ((ToggleableModule) z).isRunning())
                .map(m -> (ToggleableModule) m)
                .collect(Collectors.toList());
    }

    @Command("disp")
    public String disp(String module) {
        Optional<Module> opModule = getOptionalModuleName(module);
        if (opModule.isPresent()) {
            Module mod = opModule.get();
            if (mod instanceof ToggleableModule) {
                ToggleableModule toggleableModule = (ToggleableModule) mod;
                toggleableModule.setVisible(!toggleableModule.isVisible());
                try {
                    save();
                } catch (Throwable t) {
                    t.printStackTrace();
                    return "Failed saving modules! " + t.getMessage().replaceAll("\\.", "");
                }
                return module + " is now " + (toggleableModule.isVisible() ? "shown" : "hidden");
            } else {
                return String.format("Module %s is not toggleable", module);
            }
        } else {
            return String.format("Module %s not found", module);
        }
    }

    @Command("bind")
    public String bind(String module, String newKey) {
        Optional<Module> opModule = getOptionalModuleName(module);
        if (opModule.isPresent()) {
            Module mod = opModule.get();
            if (mod instanceof ToggleableModule) {
                ToggleableModule toggleableModule = (ToggleableModule) mod;
                toggleableModule.setKey(newKey.toUpperCase());
                try {
                    save();
                } catch (Throwable t) {
                    t.printStackTrace();
                    return "Failed saving binds! " + t.getMessage().replaceAll("\\.", "");
                }
                return String.format("Set %s's key to %s", toggleableModule.getName(), newKey);
            } else {
                return String.format("Module %s is not bindable", module);
            }
        } else {
            return String.format("Module %s not found", module);
        }
    }

    public Optional<Module> getOptionalModule(Class<?> clazz) {
        return getContents().stream().filter(clazz::isInstance)
                .findAny();
    }

    public Optional<Module> getOptionalModuleName(String name) {
        return getContents().stream().filter(m -> name.equalsIgnoreCase(m.getName()))
                .findAny();
    }

    public List<ToggleableModule> getToggleableModules() {
        return getContents().stream().filter(x -> x instanceof ToggleableModule)
                .map(x -> (ToggleableModule) x).collect(Collectors.toList());
    }

    public Optional<ToggleableModule> getOptionalToggleableModuleName(String name) {
        return getToggleableModules().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findAny();
    }


    public Module getModuleByName(String name) {
        for (Module m : getContents()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    @EventMethod(EventModuleToggle.class)
    public void onModuleToggle(EventModuleToggle eventModuleToggle) {
        if (!Client.getInstance().isEnabled) {
            eventModuleToggle.setCancelled(true);
        }
    }

    public List<ToggleableModule> getModulesInCategory(Category category) {
        List<ToggleableModule> list = new ArrayList<>();
        this.contents.stream().filter(m -> m instanceof ToggleableModule).forEach(m -> {
            ToggleableModule module = (ToggleableModule) m;
            if (module.getCategory() == category) {
                list.add(module);
            }
        });
        return list;
    }


    public void registerModulesForCommands() {
        for (Module m : this.contents) {
            Client.getInstance().getCommandManager().register(m, false);
        }
    }

    @Override
    public void startup() {
        this.contents = new ArrayList<>();
        this.contents.add(new AntiVanish());
        this.contents.add(new AutoFish());
        this.contents.add(new Killaura());
        this.contents.add(new Logger());
        this.contents.add(new Phase());
        this.contents.add(new NoSlowdown());
        this.contents.add(new Fullbright());
        this.contents.add(new Nametags());
        this.contents.add(new AutoTool());
        this.contents.add(new AntiVelocity());
        this.contents.add(new ESP());
        this.contents.add(new Step());
        this.contents.add(new Search());
        this.contents.add(new Freecam());
        this.contents.add(new Speedmine());
        this.contents.add(new Revive());
        this.contents.add(new ChestStealer());
        this.contents.add(new NoRotate());
        this.contents.add(new Chams());
        this.contents.add(new NoFall());
        this.contents.add(new AutoFarm());
        this.contents.add(new Spam());
        this.contents.add(new AliasReplace());
        this.contents.add(new Speed());
        this.contents.add(new Regen());
        this.contents.add(new Jesus());
        this.contents.add(new Blink());
        this.contents.add(new CivBreak());
        this.contents.add(new Sneak());
        this.contents.add(new PredictiveChat());
        this.contents.add(new Flight());
        this.contents.add(new AutoSoup());
        this.contents.add(new GuiToggle());
        this.contents.add(new TabUi());
    }

    @EventMethod(EventShutdown.class)
    public void onShudown(EventShutdown shutdown) {
        try {
            Client.getInstance().save();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void load() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile("toml")))) {
            String ln = "";
            List<String> foundTomlMods = new LinkedList<>();
            ToggleableModule curModule = null;
            boolean isReadingSettings = false;
            while (ln != null) {

                if (ln.isEmpty()) {
                    ln = reader.readLine();
                    continue;
                }

                /*Should Change? */
                if(ln.startsWith("[")){
                    isReadingSettings = ln.endsWith(".settings]");
                }

                if (curModule == null || (ln.startsWith("[") && !ln.contains(".settings"))) {
                    String unWrapped = ln.replaceAll("\\[", "").replaceAll("\\]", "");
                    if (!foundTomlMods.contains(unWrapped)) {
                        Optional<ToggleableModule> optionalMod = getOptionalToggleableModuleName(unWrapped);
                        if (optionalMod.isPresent()) {
                            System.out.println("Reading new module " + optionalMod.get().getName());
                            curModule = optionalMod.get();
                            foundTomlMods.add(unWrapped);
                            isReadingSettings = false;
                            // next lines correspond to this module now.
                        } else {
                            System.out.println("Not setting curTomlModule! Cannot find module " + unWrapped);
                        }
                    } else {
                        System.out.println("Found a repeating module: " + unWrapped);
                    }
                } else if(!ln.endsWith(".settings]")) {
                    // infer value
                    String[] data = ln.split("=");
                    String dVal = data[0].trim();
                    String aVal = data[1].trim();
                    // escape "'s
                    if (aVal.contains("\""))
                        aVal = aVal.replaceAll("\"", "");

                    if(!isReadingSettings) {
                        switch (dVal) {
                            case "key":
                                curModule.setKey(aVal);
                                break;
                            case "state":
                                curModule.setState(Boolean.parseBoolean(aVal), false, false);
                                break;
                            case "visible":
                                curModule.setVisible(Boolean.parseBoolean(aVal));
                                break;
                            default:
                                System.out.println("Found alien object, " + dVal);
                                break;
                        }
                    } else {
                        Optional<Value> modVal = Client.getInstance().getValueManager().getValueFromModule(curModule, dVal);
                        if(modVal.isPresent()){
                            Value modValReal = modVal.get();
                            System.out.println("Setting " + modValReal.getName() + "[" + curModule.getName() + "] to " + aVal);
                            if (Float.class.isAssignableFrom(modValReal.getTypeClass())) {
                                modValReal.setValue(Float.parseFloat(aVal));
                            } else if(Integer.class.isAssignableFrom(modValReal.getTypeClass())) {
                                modValReal.setValue(Integer.parseInt(aVal));
                            } else if(Boolean.class.isAssignableFrom(modValReal.getTypeClass())){
                                modValReal.setValue(Boolean.parseBoolean(aVal));
                            } else {
                                modValReal.setValue(aVal);
                            }
                        }
                    }
                }
                ln = reader.readLine();
            }
            Client.getInstance().getValueManager().load();
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    @Override
    public void save() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile("toml")))) {
            getToggleableModules().forEach(m -> {
                try {
                    writer.write("[" + m.getName() + "]\n");
                    writer.write("key = \"" + m.getKey() + "\"\n");
                    writer.write("state = " + m.isRunning() + "\n");
                    writer.write("visible = " + m.isVisible() + "\n");
                    writer.newLine();
                    Optional<Value[]> values = Client.getInstance().getValueManager().getValuesFromModule(m);
                    if (values.isPresent()) {
                        writer.write("[" + m.getName() + ".settings]\n");
                        Arrays.stream(values.get())
                                .forEach(v -> {
                                    try {
                                        if (Float.class.isAssignableFrom(v.getTypeClass())
                                                || Integer.class.isAssignableFrom(v.getTypeClass())
                                                || Boolean.class.isAssignableFrom(v.getTypeClass())) {
                                            writer.write(v.getName() + " = " + v.get() + "\n");
                                        } else {
                                            writer.write(v.getName() + " = \"" + v.get() + "\"\n");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                        writer.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Client.getInstance().getValueManager().save();
        }
    }
}
