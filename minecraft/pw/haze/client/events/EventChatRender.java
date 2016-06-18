package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 4/3/16
 */
public class EventChatRender extends Event {
    private String text;

    public EventChatRender(String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }

}
