package pw.haze.client.management.action.tasks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import pw.haze.client.Client;
import pw.haze.client.management.action.Task;
import pw.haze.client.management.module.Module;
import pw.haze.client.management.module.modules.Search;
import pw.haze.client.util.Methods;

import java.util.Optional;

/**
 * |> Author: haze
 * |> Since: 4/3/16
 */
public class SearchTask extends Task {


    @Override
    public void invoke() {
        if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Optional<Module> searchOptional = Client.getInstance().getModuleManager().getOptionalModule(Search.class);
            if (searchOptional.isPresent()) {
                Search search = (Search) searchOptional.get();
                Block look = Methods.getBlock(Minecraft.getMinecraft().objectMouseOver.func_178782_a());
                int id = Block.getIdFromBlock(look);
                if (search.blocks.contains(id)) {
                    search.removeBlockID(id);
                } else {
                    search.searchAddID(id);
                }
            }
        }
    }


}
