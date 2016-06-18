package pw.haze.client.ui.gui;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class Rectangle {
    public float x, y, width, height;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public Rectangle add(float x, float y, float width, float height) {
        return new Rectangle(this.x + x, this.y + y, this.width + width, this.height + height);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
