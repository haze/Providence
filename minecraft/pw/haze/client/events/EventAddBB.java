package pw.haze.client.events;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import pw.haze.event.Event;

/**
 * Created by Haze on 6/20/2015.
 */
public class EventAddBB extends Event {

    private Block block;
    private AxisAlignedBB boundingBox;
    private BlockPos blockPos;

    public EventAddBB(BlockPos blockPos, AxisAlignedBB boundingBox, Block block) {
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
