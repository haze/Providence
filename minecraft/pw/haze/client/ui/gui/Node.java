package pw.haze.client.ui.gui;

import java.util.List;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class Node<T> extends SubNode {
    public float x = -1, y = -1;
    public boolean isExtended;
    public float lastX, lastY;
    public boolean isBeingDragged;


    Node(T item, List<SubNode> subset, float x, float y) {
        super(item, subset);
        this.x = x;
        this.y = y;
    }
}
