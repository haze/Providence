package pw.haze.client.events;

import pw.haze.client.management.action.Action;
import pw.haze.event.Event;

/**
 * Created by Haze on 6/4/2015.
 */
public class EventAction extends Event {

    private Action action;
    private int key;

    public EventAction(Action action, int key) {
        if (action == Action.KEY_PRESS) {
            this.key = key;
            this.action = action;
        }
    }

    public EventAction(Action action) {
        this.action = action;
        this.key = -1;
    }

    public int getKey() {
        return key;
    }

    public Action getAction() {
        return action;
    }


}
