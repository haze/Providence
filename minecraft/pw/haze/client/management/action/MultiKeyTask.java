package pw.haze.client.management.action;

import org.lwjgl.input.Keyboard;

/**
 * Created by Haze on 6/20/2015.
 */
public abstract class MultiKeyTask extends Task {

    public abstract boolean isKeyAllowed(int key);

    public abstract void invoke(int invokedKey);

    @Override
    public void invoke() {
        invoke(Keyboard.KEY_NONE);
    }
}
