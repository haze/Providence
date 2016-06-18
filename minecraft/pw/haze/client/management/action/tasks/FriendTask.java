package pw.haze.client.management.action.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import pw.haze.client.Client;
import pw.haze.client.management.action.Task;

import java.util.Optional;

/**
 * Created by Haze on 6/25/2015.
 */
public class FriendTask extends Task {

    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void invoke() {
        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            String name = mc.objectMouseOver.entityHit.getName();
            if (Client.getInstance().getFriendManager().getFriendUsername(name).isPresent()) {
                Client.getInstance().getFriendManager().getContents().remove(Client.getInstance().getFriendManager().getFriendUsername(name).get());
            } else if (Client.getInstance().getFriendManager().getFriendAlias(name).isPresent()) {
                Client.getInstance().getFriendManager().getContents().remove(Client.getInstance().getFriendManager().getFriendAlias(name).get());
            } else {
                Client.addChat(Client.getInstance().getFriendManager().friendAdd(name, Optional.empty()));
                System.out.println("added " + name);
            }
        }
    }

}
