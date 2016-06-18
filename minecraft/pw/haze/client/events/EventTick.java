package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * Created by Haze on 6/20/2015.
 */
public class EventTick extends Event {
    private EnumStage enumStage;

    public EventTick(EnumStage enumStage) {
        this.enumStage = enumStage;
    }

    public EnumStage getEnumStage() {
        return enumStage;
    }

    public void setEnumStage(EnumStage enumStage) {
        this.enumStage = enumStage;
    }

    public enum EnumStage {
        PRE, POST
    }
}
