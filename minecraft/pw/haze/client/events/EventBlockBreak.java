package pw.haze.client.events;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 3/27/16
 */
public class EventBlockBreak extends Event {
    private BlockPos block;
    private EnumFacing face;

    public EventBlockBreak(BlockPos block, EnumFacing face) {
        this.block = block;
        this.face = face;
    }

    public BlockPos getBlock() {
        return block;
    }

    public void setBlock(BlockPos block) {
        this.block = block;
    }

    public EnumFacing getFace() {
        return face;
    }
}
