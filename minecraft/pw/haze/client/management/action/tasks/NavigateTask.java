package pw.haze.client.management.action.tasks;

import org.lwjgl.input.Keyboard;
import pw.haze.client.management.action.MultiKeyTask;
import pw.haze.client.ui.tab.TabUi;
import pw.haze.client.ui.tab.util.TabDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Haze on 6/20/2015.
 */
public class NavigateTask extends MultiKeyTask {

    protected TabUi tabUi;
    List<Integer> allowedKeys = new ArrayList<>(Arrays.asList(Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT));

    public NavigateTask(TabUi tabUi) {
        this.tabUi = tabUi;
    }

    @Override
    public boolean isKeyAllowed(int key) {
        return this.allowedKeys.contains(key);
    }

    @Override
    public void invoke(int invokedKey) {
        switch (invokedKey) {
            case Keyboard.KEY_RIGHT:
                tabUi.shiftTab(TabDirection.RIGHT);
                break;
            case Keyboard.KEY_LEFT:
                tabUi.shiftTab(TabDirection.LEFT);
                break;
            case Keyboard.KEY_UP:
                tabUi.shiftTab(TabDirection.UP);
                break;
            case Keyboard.KEY_DOWN:
                tabUi.shiftTab(TabDirection.DOWN);
                break;
        }
    }
}
