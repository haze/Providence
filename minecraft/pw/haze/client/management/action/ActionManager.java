package pw.haze.client.management.action;

import org.lwjgl.input.Keyboard;
import pw.haze.client.Client;
import pw.haze.client.events.EventAction;
import pw.haze.client.management.MapManager;
import pw.haze.client.management.action.tasks.FriendTask;
import pw.haze.client.management.action.tasks.SearchTask;
import pw.haze.client.management.action.tasks.ToggleTask;
import pw.haze.client.management.module.ToggleableModule;
import pw.haze.client.ui.tab.TabUi;
import pw.haze.event.EventManager;
import pw.haze.event.annotation.EventMethod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Created by Haze on 6/4/2015.
 */
public class ActionManager extends MapManager<Action[], Task[]> {

    public ActionManager() {
        EventManager.getInstance().registerAll(this);
    }

    private Predicate<ToggleableModule> doesKeyMatch(int newKey) {
        return m -> m.getKey().equalsIgnoreCase(Keyboard.getKeyName(newKey));
    }

    @EventMethod(EventAction.class)
    public void listenForEvents(EventAction action) {
        switch (action.getAction()) {
            case KEY_PRESS:
                if (action.getKey() != -1) {
                    for (Task taskx : getAllTasksForAction(Action.KEY_PRESS)) {
                        if (taskx instanceof MultiKeyTask) {
                            if (Client.getInstance().getModuleManager().getOptionalModule(TabUi.class).isPresent()) {
                                if (((ToggleableModule) Client.getInstance().getModuleManager().getOptionalModule(TabUi.class).get()).isRunning()) {
                                    getAllTasksForAction(Action.KEY_PRESS).stream()
                                            .filter(task -> task instanceof MultiKeyTask)
                                            .filter(mktx -> ((MultiKeyTask) mktx).isKeyAllowed(action.getKey()))
                                            .forEach(mkt -> ((MultiKeyTask) mkt).invoke(action.getKey()));
                                    return;
                                }
                            }
                        } else if (taskx != null) {
                            Optional<Task> taskX = getAllTasksForAction(Action.KEY_PRESS).stream()
                                    .filter(task -> task instanceof ToggleTask)
                                    .filter(task1 -> Keyboard.getKeyIndex(((ToggleTask) task1).getModule().getKey()) == action.getKey())
                                    .findFirst();

                            Optional<Task> taskZ = getAllTasksForAction(Action.KEY_PRESS).stream()
                                    .filter(task -> task instanceof SearchTask)
                                    .filter(task1 -> Keyboard.KEY_END == action.getKey())
                                    .findFirst();

                            if (taskZ.isPresent()) {
                                taskZ.get().invoke();
                                return;
                            }

                            if (taskX.isPresent()) {
                                taskX.get().invoke();
                                return;
                            }
                        }
                    }

                    /*Arrays.asList(this.map.get(action.getAction())).stream()
                            .filter(task -> task instanceof ToggleTask)
                            .filter(task1 -> Keyboard.getKeyIndex(((ToggleTask) task1).getModule().getKey()) == action.getKey())
                                    .forEach(Task::invoke);*/
                }
                break;
            case MIDDLE_CLICK:
                getAllTasksForAction(Action.MIDDLE_CLICK).stream()
                        .filter(task -> task instanceof FriendTask)
                        .forEach(Task::invoke);
                break;
        }
    }


    public List<Task> getAllTasksForAction(Action action) {
        List<Task> taskList = new ArrayList<>();
        for (Map.Entry<Action[], Task[]> entry : this.map.entrySet()) {
            for (Action action1 : entry.getKey()) {
                if (action1 == action) {
                    taskList.addAll(Arrays.asList(entry.getValue()));
                }
            }
        }
        return taskList;
    }

    @Override
    public void startup() {
        this.map = new ConcurrentHashMap<>();
    }
}
