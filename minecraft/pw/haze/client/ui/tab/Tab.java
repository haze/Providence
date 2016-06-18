package pw.haze.client.ui.tab;

import java.util.List;

/**
 * Created by Haze on 6/20/2015.
 */
public class Tab<T, V> {

    protected List<V> tabContents;
    protected T value;
    protected int id;

    private Boolean isSelected = false, isHover = false;

    public Tab(List<V> tabContents, T value, int id) {
        this.tabContents = tabContents;
        this.value = value;
        this.id = id;
    }

    public Tab(T value, int id) {
        this.tabContents = null;
        this.value = value;
        this.id = id;
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public Boolean getIsHover() {
        return isHover;
    }

    public void setIsHover(Boolean isHover) {
        this.isHover = isHover;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public List<V> getTabContents() {
        return tabContents;
    }

    public T getTab() {
        return value;
    }
}
