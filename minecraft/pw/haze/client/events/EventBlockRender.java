package pw.haze.client.events;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import pw.haze.event.Event;

/**
 * Created by Haze on 6/27/2015.
 */
public class EventBlockRender extends Event {

    private Block block;
    private BlockPos pos;

    public EventBlockRender(Block block, BlockPos pos) {
        this.block = block;
        this.pos = pos;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}
