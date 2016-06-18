package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * Created by Haze on 6/6/2015.
 */
public class EventSendChatMessage extends Event {

    private String message;

    public EventSendChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
