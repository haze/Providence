package pw.haze.client.management.module.interfaces;

/**
 * Created by Haze on 6/4/2015.
 */
public interface Toggleable {

    void toggle();

    String getKey();

    void setKey(String par1Str);

    void onEnable();

    void onDisable();


}
