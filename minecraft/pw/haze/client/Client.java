package pw.haze.client;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;
import pw.haze.client.interfaces.Loadable;
import pw.haze.client.interfaces.Savable;
import pw.haze.client.management.ListManager;
import pw.haze.client.management.MapManager;
import pw.haze.client.management.action.Action;
import pw.haze.client.management.action.ActionManager;
import pw.haze.client.management.action.Task;
import pw.haze.client.management.action.tasks.FriendTask;
import pw.haze.client.management.alt.AccountManager;
import pw.haze.client.management.command.CommandManager;
import pw.haze.client.management.command.Exploits;
import pw.haze.client.management.friend.FriendManager;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ModuleManager;
import pw.haze.client.management.module.modules.GuiToggle;
import pw.haze.client.management.preset.PresetManager;
import pw.haze.client.management.value.ValueManager;
import pw.haze.client.ui.gui.CustomGUI;
import pw.haze.client.util.NahrFont;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Haze on 6/4/2015.
 */
public class Client implements Loadable, Savable {

    public final static String NAME = "Providence";
    public final static Double VERSION = 2D;
    public final static File PROVIDENCE_FILE = new File(System.getProperty("user.home") + "/Videos/Providence/");
    private final static Client inst = new Client();
    protected static Minecraft mc = Minecraft.getMinecraft();
    private static CommandManager commandManager;
    private static ModuleManager moduleManager;
    private static FriendManager friendManager;
    private static ValueManager valueManager;
    private static AccountManager accountManager;
    private static ActionManager actionManager;
    private static PresetManager presetManager;
    public boolean isEnabled;
    private CustomGUI gui;
    private NahrFont guiFont;

    public Client() {

        // Shutdown hook
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        // LOGIN form FILE
    }

    public static Client getInstance() {
        return inst;
    }

    public static void login(String username, String password) {
        try {
            YggdrasilUserAuthentication auth = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID
                    .randomUUID().toString()), Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword(password);
            auth.logIn();
            Session session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
                    auth.getAuthenticatedToken(), Session.Type.MOJANG.name());
            Minecraft.getMinecraft().session = session;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addChat(String text) {
        if(mc.thePlayer == null)
            System.out.println(String.format("[%s7]: %s.", NAME, text));
        if (text != null)
            mc.thePlayer.addChatMessage(new ChatComponentText(String.format("\2477[\247c%s\2477]\247r: %s.", NAME, text)));
    }

    public String getInfo() {
        return "\247c" + NAME + "\247r b4116";
    }

    public NahrFont getGuiFont() {
        if (this.guiFont == null)
            this.guiFont = new NahrFont("Hack Bold", 14);
        return this.guiFont;
    }

    public CustomGUI getGui() {
        Optional<Module> toggle = getModuleManager().getOptionalModule(GuiToggle.class);
        if (this.gui == null && toggle.isPresent()) {
            this.gui = new CustomGUI(mc, ((GuiToggle) toggle.get()));
        }
        return this.gui;
    }

    public AccountManager getAccountManager() {
        if (accountManager == null)
            accountManager = new AccountManager();
        return accountManager;
    }

    public PresetManager getPresetManager() {
        if (presetManager == null)
            presetManager = new PresetManager();
        return presetManager;
    }


    public ActionManager getActionManager() {
        if (actionManager == null)
            actionManager = new ActionManager();
        return actionManager;
    }

    public FriendManager getFriendManager() {
        if (friendManager == null)
            friendManager = new FriendManager();
        return friendManager;
    }

    public ModuleManager getModuleManager() {
        if (moduleManager == null)
            moduleManager = new ModuleManager();
        return moduleManager;
    }

    public ValueManager getValueManager() {
        if (valueManager == null)
            valueManager = new ValueManager();
        return valueManager;
    }

    public CommandManager getCommandManager() {
        if (commandManager == null)
            commandManager = new CommandManager();
        return commandManager;
    }

    private void finalizeFiles() {
        if (!PROVIDENCE_FILE.exists()) {
            PROVIDENCE_FILE.mkdirs();
        }
    }



    @Override
    public void load() throws Exception {
        isEnabled = true;
        getAccountManager().startup();
        getFriendManager().startup();
        getValueManager().startup();
        getActionManager().startup();
        getCommandManager().startup();
        getModuleManager().startup();
        getPresetManager().startup();
        getModuleManager().registerModulesForCommands();
        getCommandManager().register(getCommandManager(), false);
        getCommandManager().register(getModuleManager(), false);
        getCommandManager().register(getFriendManager(), false);
        getCommandManager().register(getPresetManager(), false);
        getCommandManager().register(new Exploits(), false);
        getActionManager().getMap().put(new Action[]{Action.MIDDLE_CLICK}, new Task[]{new FriendTask()});
        finalizeFiles();
        /* now read */
        getAccountManager().load();
        getFriendManager().load();
        getModuleManager().load();
        getPresetManager().load();
        try {
            List<String> details = Files.readAllLines(new File(PROVIDENCE_FILE + "/details.txt").toPath());
            String[] data = details.get(0).split(":");
            login(data[0], data[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ListManager> T getListManagerFromClass(Class<T> clazz) {
        if (clazz.getSimpleName().toLowerCase().contains("module")) {
            return (T) getModuleManager();
        } else if (clazz.getSimpleName().toLowerCase().contains("friend")) {
            return (T) getFriendManager();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends MapManager> T getMapManagerFromClass(Class<T> clazz) {
        if (clazz.getSimpleName().toLowerCase().contains("command")) {
            return (T) getCommandManager();
        } else if (clazz.getSimpleName().toLowerCase().contains("action")) {
            return (T) getActionManager();
        } else if (clazz.getSimpleName().toLowerCase().contains("value")) {
            return (T) getValueManager();
        } else {
            return null;
        }
    }

    @Override
    public void save() throws Exception {
        getModuleManager().save();
        getFriendManager().save();
    }
}
