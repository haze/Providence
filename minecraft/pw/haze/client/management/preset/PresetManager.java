package pw.haze.client.management.preset;

import org.apache.commons.io.FilenameUtils;
import pw.haze.client.Client;
import pw.haze.client.interfaces.Loadable;
import pw.haze.client.management.ListManager;
import pw.haze.client.management.command.Command;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.value.Value;

import java.io.*;
import java.util.LinkedList;
import java.util.Optional;

/**
 * |> Author: haze
 * |> Since: 4/2/16
 */
public class PresetManager extends ListManager<Preset> implements Loadable {


    private File presetFolder;

    public PresetManager() {
        super("Presets");
        this.presetFolder = new File(Client.PROVIDENCE_FILE + "/presets/");
    }


    @Override
    public void startup() {
        this.contents = new LinkedList<>();
    }

    public Optional<Preset> getPresetByName(String name) {
        return this.contents.stream().filter(p -> p.getName().equals(name)).findFirst();
    }


    @Command("prsm")
    public String savePresetModule(String name, String module, Optional<Boolean> def) {
        try {
            Optional<Module> mod = Client.getInstance().getModuleManager().getOptionalModuleName(module);
            if (mod.isPresent()) {
                save(name, def.isPresent() ? def.get() : false, mod.get());
            } else {
                return "Cannot find module " + module;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "Failed to save preset " + name + "[" + t.getCause() + "]";
        }
        return "Successfully saved preset " + name + " [" + module + "]";
    }

    @Command("prsa")
    public String savePreset(String name, Optional<Boolean> def) {
        try {
            saveAll(name, def.isPresent() ? def.get() : false);
        } catch (Throwable t) {
            t.printStackTrace();
            return "Failed to save preset " + name + "[" + t.getCause() + "]";
        }
        return "Successfully saved preset " + name + " [all]";
    }

    public void saveAll(String fileName, boolean def) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.presetFolder + "/" + fileName + ".txt")))) {
            if (def) {
                writer.write("$$DEFAULT$$");
                writer.newLine();
            }
            for (Module m : Client.getInstance().getModuleManager().getContents()) {
                Optional<Value[]> values = Client.getInstance().getValueManager().getValuesFromModule(m);
                if (values.isPresent()) {
                    for (Value v : values.get()) {
                        writer.write(m.getName() + "_" + v.getName() + "=" + v.get());
                        writer.newLine();
                    }
                }
            }
        }
    }

    public void save(String fileName, boolean def, Module module) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.presetFolder + "/" + fileName + ".txt")))) {
            if (def) {
                writer.write("$$DEFAULT$$");
                writer.newLine();
            }
            Optional<Value[]> values = Client.getInstance().getValueManager().getValuesFromModule(module);
            if (values.isPresent()) {
                for (Value v : values.get()) {
                    writer.write(module.getName() + "_" + v.getName() + "=" + v.get());
                    writer.newLine();
                }
            }
        }
    }

    @Command("pr")
    public String applyPreset(String presetName) {
        Optional<Preset> optionalPreset = getPresetByName(presetName);
        if (optionalPreset.isPresent()) {
            optionalPreset.get().apply();
            return "Successfully applied preset " + optionalPreset.get().getName();
        }
        return "Preset " + presetName + " not found";
    }

    @Override
    public void load() throws Exception {
        if (!this.presetFolder.exists()) {
            this.presetFolder.mkdirs();
        }
        if (this.presetFolder != null && presetFolder.listFiles() != null) {
            for (File preset : presetFolder.listFiles()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(preset))) {
                    String line = reader.readLine();
                    Preset curPreset = new Preset(FilenameUtils.getBaseName(preset.getName()));
                    boolean shouldApplyImmedietly = false;
                    while (line != null) {
                        if (line.equals("$$DEFAULT$$")) {
                            shouldApplyImmedietly = true;
                        } else {
                            String[] data = line.split("=");
                            String moduleName = data[0].split("_")[0];
                            Optional<Module> mod = Client.getInstance().getModuleManager().getOptionalModuleName(moduleName);
                            if (mod.isPresent()) {
                                shouldApplyImmedietly = false;
                                String valueName = data[0].split("_")[1];
                                Optional<Value> valueOptional = Client.getInstance().getValueManager().getValueFromModule(mod.get(), valueName);
                                if (valueOptional.isPresent())
                                    curPreset.getMap().put(valueOptional.get(), data[1]);
                            }
                        }
                        line = reader.readLine();
                    }
                    this.contents.add(curPreset);
                    if (shouldApplyImmedietly)
                        curPreset.apply();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
