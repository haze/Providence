package pw.haze.client.ui.gui;

import java.util.List;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class SubNode<T> {
    public T item;
    public List<SubNode<?>> subset;

    SubNode(T item, List<SubNode<?>> subset) {
        this.item = item;
        this.subset = subset;
    }
}
