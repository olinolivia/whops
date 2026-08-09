package olinolivia.whops.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class CheckpointBlockEntity extends BlockEntity {

    public CheckpointBlockEntity(BlockPos pos, BlockState state) {
        super(WhopsBlockEntities.CHECKPOINT, pos, state);
    }

    private Vec3 pos = null;
    private Vec2 rot = null;

    public void store(Vec3 newPos, Vec2 newRot) {
        pos = newPos;
        rot = newRot;
        setChanged();
    }

    public Vec3 getPos() {
        return pos;
    }

    public Vec2 getRot() {
        return rot;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        if (pos != null) output.store("stored_position", Vec3.CODEC, pos);
        if (rot != null) output.store("stored_rotation", Vec2.CODEC, rot);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        pos = input.read("stored_position", Vec3.CODEC).orElse(null);
        rot = input.read("stored_rotation", Vec2.CODEC).orElse(null);
    }

}
