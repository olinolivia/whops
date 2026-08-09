package olinolivia.whops.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public abstract class PadBlock extends Block {

    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 1.0F);

    public PadBlock(Properties props) {
        super(props);
    }

    abstract boolean shouldTrigger(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            ServerPlayer steppingPlayer
    );

    abstract void trigger(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            ServerPlayer steppingPlayer
    );

    @Override
    protected void entityInside(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Entity entity,
            @NonNull InsideBlockEffectApplier effectApplier,
            boolean isPrecise
    ) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
        if (entity instanceof ServerPlayer player && shouldTrigger(state, level, pos, player)) trigger(state, level, pos, player);
    }

    @Override
    protected @NonNull VoxelShape getShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected @NonNull VoxelShape getInteractionShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos
    ) {
        return SHAPE;
    }
}
