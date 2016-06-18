package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 3/12/16
 */
public class EventRecieveChatMessage extends Event {
    private String message;

    public EventRecieveChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
