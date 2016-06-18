package pw.haze.client.management.value;

/**
 * |> Author: haze
 * |> Since: 4/2/16
 */
public class ValueHelper {
    private Object obj;
    private String name;

    public ValueHelper(String name, Object obj) {
        this.name = name;
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
