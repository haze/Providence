package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 4/3/16
 */
public class EventStep extends Event {

    private float stepHeight;
    private EnumStep state;

    public EventStep(float stepHeight, EnumStep state) {
        this.stepHeight = stepHeight;
        this.state = state;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public EnumStep getState() {
        return state;
    }

    public enum EnumStep {
        PRE, POST
    }

}
