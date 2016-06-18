package pw.haze.client.management.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import pw.haze.client.Client;
import pw.haze.client.events.EventSendChatMessage;
import pw.haze.client.management.MapManager;
import pw.haze.client.management.command.utility.DigitClamp;
import pw.haze.client.management.command.utility.LengthClamp;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.management.value.Value;
import pw.haze.client.management.value.ValueHelper;
import pw.haze.event.EventManager;
import pw.haze.event.annotation.EventMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static pw.haze.client.util.Methods.mc;

/**
 * @author Haze
 * @version 2.3BETA
 * @since 9/24/2015
 */
public class CommandManager extends MapManager<Method, Object> {

    /**
     * How the manager detects commands. Can be changed
     */
    public Value<String> catalyst;
    private List<ToggleableModule> toggledModules;

    /**
     * Sets the default catalyst
     */
    public CommandManager() {
        this.catalyst = new Value<>(",", "catalyst");
        Client.getInstance().getValueManager().getMap().put(new ValueHelper("commands", this), new Value[]{catalyst});
        EventManager.getInstance().registerAll(this);
        this.toggledModules = new ArrayList<>();

    }


    @SuppressWarnings("unchecked")
    @Command({"clear", "cc"})
    public void clearChat() {
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        List<ChatLine> tempList = new ArrayList<>();
        for (ChatLine c : (ArrayList<ChatLine>) mc.ingameGUI.persistantChatGUI.field_146253_i) {
            if (c.getChatComponent().getFormattedText().contains(Client.NAME)) {
                tempList.add(c);
            }
            for (Method m : getMap().keySet()) {
                Command command = m.getAnnotation(Command.class);
                if (c.getChatComponent().getFormattedText().contains(command.value()[0]) && (c.getChatComponent().getFormattedText().contains("]") || c.getChatComponent().getFormattedText().contains("]") || c.getChatComponent().getFormattedText().contains(","))) {
                    tempList.add(c);
                }
            }

        }
        mc.ingameGUI.persistantChatGUI.field_146253_i.removeAll(tempList);
    }

    @Command({"help", "ls"})
    public String help(Optional<String> moduleName) {
        if (moduleName.isPresent()) {
            /*module or command detail listing.*/
            Optional<Module> module = Client.getInstance().getModuleManager().getOptionalModuleName(moduleName.get());
            if (module.isPresent()) {
                Module mod = module.get();
                if (ToggleableModule.class.isAssignableFrom(mod.getClass())) {
                    ToggleableModule tmod = (ToggleableModule) mod;
                    Client.addChat(String.format("Module: %s, key \"%s\"", tmod.getName(), tmod.getKey()));
                    Client.addChat(String.format("Description: %s", mod.getDescription().replaceAll("\\.", "")));
                    if (getAllCommandsInClass(module.get().getClass()).size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        List<Command> commands = getAllCommandsInClass(mod.getClass());
                        for (Command c : commands) {
                            if (commands.indexOf(c) + 1 == commands.size())
                                builder.append(formatFor(c));
                            else
                                builder.append(formatFor(c)).append(", ");
                        }
                        Client.addChat(String.format("Commands [%d]: %s", getAllCommandsInClass(mod.getClass()).size(), builder.toString()));
                    }
                    return String.format("Type: %s", tmod.getCategory().name().toLowerCase());
                } else {
                    Client.addChat(String.format("Module: %s", module.get().getName()));
                    if (getAllCommandsInClass(module.get().getClass()).size() > 0) {
                        StringBuilder builder = new StringBuilder(String.format("Commands [%d]: ", getAllCommandsInClass(module.get().getClass()).size()));
                        List<Command> commands = getAllCommandsInClass(mod.getClass());
                        for (Command c : commands) {
                            if (commands.indexOf(c) + 1 == commands.size())
                                builder.append(formatFor(c));
                            else
                                builder.append(formatFor(c) + ", ");
                        }
                        Client.addChat(String.format("Commands [%d]: %s", getAllCommandsInClass(module.get().getClass()).size(), builder.toString()));
                    }
                    return String.format("Description: %s", mod.getDescription().replaceAll("\\.", ""));
                }
            } else {
                Optional<Command> command = getOptionalCommandName(moduleName.get());
                if (command.isPresent()) {
                    return String.format("Command %s [%s], usage = \"%s\"", Arrays.toString(command.get().value()), getMethodFromCommand(command.get()).getDeclaringClass().getSimpleName(), getUsage(getMethodFromCommand(command.get())));
                }
            }
            return String.format("Command / Module %s not found", moduleName.get());
        } else {
            StringBuilder builder = new StringBuilder();
            List<Command> commands = getCommands();
            // commands.sort((c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(c1.value()[0], c2.value()[0]));
            // for (Command c : commands) builder.append(formatFor(c, getMethodFromCommand(c).getDeclaringClass())).append(", ");
            Map<Class, List<Command>> allClasses = new HashMap<>();
            /*
                All Classes -> With an array of commands.
             */
            for (Command c : commands) {
                Class cz = getMethodFromCommand(c).getDeclaringClass();
                if (!allClasses.keySet().contains(cz))
                    allClasses.put(cz, getAllCommandsInClass(cz));
            }

            for (Map.Entry<Class, List<Command>> entry : allClasses.entrySet()) {
                builder.append(String.format("\2477[\247c%s\2477]\247r ", entry.getKey().getSimpleName().replace("Manager", "")));
                for (Command c : entry.getValue()) {
                    if (entry.getValue().indexOf(c) == entry.getValue().size())
                        builder.append(formatFor(c) + ".");
                    else
                        builder.append(formatFor(c) + ", ");
                }
            }

            builder.replace(builder.length() - 2, builder.length(), "");
            return builder.toString();
        }
    }

    public List<Command> getAllCommandsInClass(Class cz) {
        List<Command> commands = new ArrayList<>();
        this.map.keySet().stream().filter(m -> m.getDeclaringClass() == cz)
                .filter(m -> m.isAnnotationPresent(Command.class))
                .forEach(m -> commands.add(m.getAnnotation(Command.class)));
        return commands;
    }

    public String formatFor(Command c) {
        StringBuilder stringBuilder = new StringBuilder(String.format("\247f%s\247r", c.value()[0]));
        if (c.value().length != 1) {
            stringBuilder.append("\2477[");
            List<String> aliases = Arrays.asList(c.value());
            for (String str : aliases.subList(1, aliases.size())) {
                if (aliases.indexOf(str) == aliases.size() - 1)
                    stringBuilder.append(str).append("\247r");
                else
                    stringBuilder.append(str).append("\247r, \2477");
            }
            //catalyst[cataly, cally,
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
            stringBuilder.append("\2477]\247r");
        }
        return stringBuilder.toString();
    }

    private Optional<Command> getOptionalCommandName(String str) {
        for (Command c : this.getCommands())
            for (String name : c.value())
                if (str.equalsIgnoreCase(name)) return Optional.of(c);
        return Optional.empty();
    }


    @Command("client")
    public void setClientState() {
        if (Client.getInstance().isEnabled) {
            toggledModules.addAll(Client.getInstance().getModuleManager().getEnabledModules());
            Client.getInstance().getModuleManager().getEnabledModules().stream().filter(module -> module.isRunning()).forEach(module -> {
                module.setState(false, false, true);
            });
            clearChat();
        }
        Client.getInstance().isEnabled = !Client.getInstance().isEnabled;
        if (Client.getInstance().isEnabled) {
            for (ToggleableModule mod : toggledModules) {
                mod.setState(true, false, true);
            }
            toggledModules.clear();
        }
    }


    /**
     * Gets a list of command annotations
     *
     * @return the list of command annotations
     */
    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();
        this.map.keySet().stream().forEach(cmd -> commands.add(cmd.getAnnotation(Command.class)));
        return commands;
    }

    /**
     * Use this to get a method from a command.
     *
     * @param command the command
     * @return the commands method
     */
    public Method getMethodFromCommand(Command command) {
        for (Method m : this.map.keySet())
            if (Arrays.equals(m.getAnnotation(Command.class).value(), command.value()))
                return m;
        return null;
    }

    @Command("prefix")
    public String changeCommandCatalyst(String newCatalyst) {
        this.catalyst.setValue(newCatalyst);
        return String.format("Successfully changed the command prefix to %s", newCatalyst);
    }


    @EventMethod(EventSendChatMessage.class)
    public void listenToChat(EventSendChatMessage eventSendChatMessage) {
        if (eventSendChatMessage.getMessage().startsWith(catalyst.get()) && !eventSendChatMessage.getMessage().trim().equals(this.catalyst.get().trim())) {
            eventSendChatMessage.setCancelled(true);
            StringBuilder stringBuilder = new StringBuilder(eventSendChatMessage.getMessage());
            String begin = stringBuilder.delete(this.catalyst.get().length(), stringBuilder.length()).toString();
            String cmd = eventSendChatMessage.getMessage().replace(begin, "").split(" ")[0];
            String[] arguments = getArguments(eventSendChatMessage.getMessage().substring(catalyst.get().length()));
            for (Map.Entry<Method, Object> entry : this.map.entrySet()) {
                Command annotation = entry.getKey().getAnnotation(Command.class);
                if (isValidAnnotation(annotation, cmd)) {
                    List<String> args = Arrays.asList(arguments).stream().skip(1).collect(Collectors.toList());
                    Client.addChat(invokeCommand(entry.getKey(), entry.getValue(), args.toArray(new String[args.size()])));
                    return;
                }
            }
            if (!Objects.equals(cmd.trim(), ""))
                Client.addChat(String.format("Command %s not found", cmd));
        }
    }

    /**
     * Checks if the annotation is valid
     *
     * @param c         the command annotation
     * @param qualifier the string to qualify the command by
     * @return whether or not the annotation is valid
     */
    private boolean isValidAnnotation(Command c, String qualifier) {
        for (String str : c.value())
            if (qualifier.equalsIgnoreCase(str)) return true;
        return false;
    }

    /**
     * Uses a simple regex string to format arguments between spaces, and allows spaces in arguments with the use of quotes.
     *
     * @param message the message
     * @return a array of arguments
     */
    private String[] getArguments(String message) {
        List<String> arguments = new ArrayList<>();
        Matcher matcher = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'").matcher(message);
        switch (matcher.groupCount()) {
            case 1: {
                arguments.add(toString().substring(this.catalyst.get().length()));
                break;
            }
            default: {
                while (matcher.find()) {
                    arguments.add(matcher.group().replaceAll("\"", ""));
                }
            }
        }
        return arguments.toArray(new String[arguments.size()]);
    }

    /**
     * Used to get the usage of a command from the method itself.
     *
     * @param method the command method
     * @return the usage
     */
    @SuppressWarnings("unchecked")
    public String getUsage(Method method) {
        StringJoiner joiner = new StringJoiner(" ");
        try {
            for (Class<?> type : method.getParameterTypes()) {
                if (Optional.class.isAssignableFrom(type)) {
                    Class<Optional<?>> optionalClass = (Class<Optional<?>>) type;
                    joiner.add(String.format("Optional<%s>", optionalClass.getTypeName().getClass().getSimpleName()));
                } else {
                    joiner.add(String.format("(%s)", type.getSimpleName()));
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return joiner.toString();
    }

    /**
     * Used to parse argument types to their respective classes / object instances
     *
     * @param method           the command method
     * @param arguments        the arguments the user provided
     * @param excludeOptionals whether or not to use optionals
     * @return the fixed argument list.
     */
    private List<Object> getActualArguments(Method method, String[] arguments, Boolean excludeOptionals) {
        List<Object> actualArguments = new ArrayList<>();
        for (int i = 0; i < (excludeOptionals ? sizeOfParamsExcludingOptionals(method) : arguments.length); i++) {
            if (arguments[i] != null) {
                Class<?> type = method.getParameterTypes()[i];
                Parameter param = method.getParameters()[i];
                if (isBoolean(arguments[i]) && (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))) {
                    actualArguments.add(Boolean.parseBoolean(arguments[i]));
                } else if (isFloat(arguments[i]) && (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))) {
                    Float float0 = Float.parseFloat(arguments[i]);
                    if (param.isAnnotationPresent(DigitClamp.class)) {
                        DigitClamp clamp = param.getAnnotation(DigitClamp.class);
                        float0 = digitClamp(clamp, float0);
                    }
                    actualArguments.add(float0);
                } else if (isDigit(arguments[i]) && (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))) {
                    Integer int0 = Integer.parseInt(arguments[i]);
                    if (param.isAnnotationPresent(DigitClamp.class)) {
                        DigitClamp clamp = param.getAnnotation(DigitClamp.class);
                        int0 = digitClamp(clamp, int0);
                    }
                    actualArguments.add(int0);
                } else if (String.class.isAssignableFrom(type)) {
                    String str = arguments[i];
                    if (param.isAnnotationPresent(LengthClamp.class)) {
                        LengthClamp clamp = param.getAnnotation(LengthClamp.class);
                        if (str.length() > clamp.value()) {
                            StringBuilder builder = new StringBuilder(str);
                            builder.replace(clamp.value(), str.length(), "");
                            str = builder.toString();
                        }
                    }
                    actualArguments.add(str);
                } else if (Optional.class.isAssignableFrom(type)) {
                    String str = arguments[i];
                    if (isBoolean(arguments[i])) {
                        actualArguments.add(Optional.of(Boolean.parseBoolean(str)));
                    } else if (isFloat(arguments[i])) {
                        Float digit = Float.parseFloat(arguments[i]);
                        if (param.isAnnotationPresent(DigitClamp.class)) {
                            digit = digitClamp(param.getAnnotation(DigitClamp.class), str);
                        }
                        actualArguments.add(Optional.of(digit));
                    } else if (isDigit(arguments[i])) {
                        Integer digit = Integer.parseInt(arguments[i]);
                        if (param.isAnnotationPresent(DigitClamp.class)) {
                            digit = Math.round(digitClamp(param.getAnnotation(DigitClamp.class), str));
                        }
                        actualArguments.add(Optional.of(digit));
                    } else {
                        if (param.isAnnotationPresent(LengthClamp.class)) {
                            str = strClamp(param.getAnnotation(LengthClamp.class), str);
                        }
                        actualArguments.add(Optional.of(str));
                    }
                }
            }
        }
        return actualArguments;
    }


    /**
     * Clamps the parsed string to the clamps specifications
     *
     * @param clamp the clamp annotation
     * @param str   the string to parse
     * @return the finalized (clamped) digit
     */
    public Float digitClamp(DigitClamp clamp, String str) {
        return digitClamp(clamp, Float.parseFloat(str));
    }

    private int digitClamp(DigitClamp clamp, int int0) {
        DecimalFormat df = new DecimalFormat("##");
        df.setRoundingMode(RoundingMode.DOWN);
        int min = Integer.parseInt(df.format(clamp.min()));
        int max = Integer.parseInt(df.format(clamp.max()));
        if (int0 > max)
            int0 = max;
        if (int0 < min)
            int0 = min;
        return int0;
    }

    private Float digitClamp(DigitClamp clamp, Float float0) {
        float min = clamp.min();
        float max = clamp.max();
        if (float0 > max)
            float0 = max;
        if (float0 < min)
            float0 = min;
        return float0;
    }

    /**
     * Clamps the string length
     *
     * @param clamp the clamp annotation
     * @param str   the string to parse
     * @return the finalized (clamped) string
     */
    public String strClamp(LengthClamp clamp, String str) {
        if (str.length() > clamp.value()) {
            StringBuilder builder = new StringBuilder(str);
            builder.replace(clamp.value(), str.length(), "");
            return builder.toString();
        }
        return str;
    }


    /**
     * Gets the generic super-type of a optional
     *
     * @param optional the optional
     * @return the generic super-type
     */
    private Class<?> getOptionalType(Optional<?> optional) {
        if (optional.isPresent())
            return optional.get().getClass();
        return null;
    }

    /**
     * Invokes a command.
     *
     * @param method    the method to invoke
     * @param instance  the class instance
     * @param arguments the arguments the user provided
     */
    private String invokeCommand(Method method, Object instance, String[] arguments) {
        String usage = getUsage(method);
        try {
            int sizeOfParamsExcludingOptionals = sizeOfParamsExcludingOptionals(method);
            List<Object> actualArguments = getActualArguments(method, arguments, method.getParameterCount() != arguments.length && (sizeOfParamsExcludingOptionals == arguments.length));
            if (method.getParameterCount() == arguments.length) {
                if (String.class.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(instance, actualArguments.toArray()).toString();
                } else {
                    method.invoke(instance, actualArguments.toArray());
                }
            } else if (sizeOfParamsExcludingOptionals == arguments.length) {
                actualArguments = getActualArguments(method, arguments, true);
                if (String.class.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(instance, fixArgumentsForOptionals(method, actualArguments).toArray()).toString();
                } else {
                    method.invoke(instance, fixArgumentsForOptionals(method, actualArguments).toArray());
                }
            } else {
                if (doParametersContainOptional(method)) {
                    return String.format("Expected %s or %s arguments(s), you turned in %s. Usage \"%s\"", sizeOfParamsExcludingOptionals(method), method.getParameterCount(), arguments.length, usage);
                } else {
                    return String.format("Expected %s argument(s), you turned in %s. Usage \"%s\"", method.getParameterCount(), arguments.length, usage);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return String.format("Unexpected type(s), Expected usage: %s args = \"%s\"", usage, Arrays.toString(arguments));
        }
        return null;
    }

    /**
     * Returns the fixed argument list.
     *
     * @param method the method
     * @param given  the "actual" arguments
     * @return the arguments without optionals attached
     */
    private List<Object> fixArgumentsForOptionals(Method method, List<Object> given) {
        List<Object> newArguments = new ArrayList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (given.size() <= method.getParameterCount() && Optional.class.isAssignableFrom(type)) {
                newArguments.add(Optional.empty());
            } else if (given.size() <= method.getParameterCount()) {
                newArguments.add(given.get(i));
            }
        }
        return newArguments;
    }

    /**
     * returns the size of parameters in the method excluding the optionals
     *
     * @param method the method
     * @return the size of parameters in the method excluding the optionals
     */
    private int sizeOfParamsExcludingOptionals(Method method) {
        return Arrays.asList(method.getParameters()).stream().filter(param -> !Optional.class.isAssignableFrom(param.getType())).collect(Collectors.toList()).size();
    }

    /**
     * Checks whether or not the parameters contains optionals
     *
     * @param method the method.
     * @return whether or not the parameters contains optionals
     */
    private boolean doParametersContainOptional(Method method) {
        return Arrays.asList(method.getParameters()).stream().filter(param -> Optional.class.isAssignableFrom(param.getType())).findAny().isPresent();
    }

    /**
     * Determines whether or not a string is a proper boolean
     *
     * @param str the string
     * @return whether or not the string is a proper boolean
     */
    public boolean isBoolean(String str) {
        return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false");
    }

    /**
     * Determines whether or not a string is a proper digit
     *
     * @param str the string
     * @return whether or not the string is a proper digit
     */
    public boolean isDigit(String str) {
        boolean pass = true;
        for (char c : str.toCharArray()) {
            pass = Character.isDigit(c);
        }
        return pass;
    }

    /**
     * Determines whether or not a string is a proper float
     *
     * @param str the string
     * @return whether or not the string is a proper float
     */
    public boolean isFloat(String str) {
        boolean pass = true;
        for (char c : str.toCharArray()) {
            if (c != '.')
                pass = Character.isDigit(c);
        }
        return pass;
    }

    /**
     * Registers a class instance without debugging.
     *
     * @param o the class instance
     */
    public void register(Object o) {
        register(o, false);
    }

    /**
     * Registers a class instance with optional debug
     *
     * @param o     the class instance
     * @param debug whether or not to print debug messages to console.
     */
    public void register(Object o, Boolean debug) {
        if (debug)
            System.out.printf("Registering class %s.\n", o.getClass().getSimpleName());
        for (Method m : o.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                if (debug)
                    System.out.printf("Found command method %s, object instance = %s.\n", m.getName(), o);
                this.map.put(m, o);
            }
        }
    }

    private String[] convertForInvoking(Object[] arguments) {
        String[] newArray = new String[arguments.length];
        int helper = 0;
        for (Object o : arguments) {
            newArray[helper] = o.toString();
            helper++;
        }
        return newArray;
    }


    @Override
    public void startup() {
        this.map = new HashMap<>();
    }
}
