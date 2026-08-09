package olinolivia.whops.block;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.block.blockentity.CheckpointBlockEntity;
import olinolivia.whops.component.StoredPositionComponent;
import olinolivia.whops.component.WhopsComponents;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.item.WhopsItems;
import olinolivia.whops.networking.ClientboundCheckpointFeedbackPayload;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static olinolivia.whops.course.CourseHelper.getCheckpoint;
import static olinolivia.whops.course.CourseHelper.setCheckpoint;

public class    CheckpointBlock extends PadBlock implements EntityBlock {

    public CheckpointBlock(Properties props) {
        super(props);
    }

    private static boolean isPosInBlock(Vec3 pos, BlockPos blockPos) {
        return Math.floor(pos.x) == blockPos.getX() && Math.floor(pos.y) == blockPos.getY() && Math.floor(pos.z) == blockPos.getZ();
    }

    private static boolean hasRot(ServerPlayer player, Vec2 rot) {
        return rot.x == player.getXRot() && rot.y == player.getYRot();
    }

    @Override
    boolean shouldTrigger(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            ServerPlayer steppingPlayer
    ) {
        WhopsCheckpoint playerCheckpoint = getCheckpoint(steppingPlayer);
        if (!(level.getBlockEntity(pos) instanceof CheckpointBlockEntity blockEntity)) return false;
        if (playerCheckpoint == null) return true;
        if (blockEntity.getPos() != null && blockEntity.getRot() != null)
            return !(playerCheckpoint.pos().equals(blockEntity.getPos())) || !(playerCheckpoint.rot().equals(blockEntity.getRot()));
        return !(isPosInBlock(playerCheckpoint.pos(), pos) && hasRot(steppingPlayer, playerCheckpoint.rot()));
    }

    @Override
    void trigger(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            ServerPlayer steppingPlayer
    ) {
        if (!(level.getBlockEntity(pos) instanceof CheckpointBlockEntity blockEntity)) return;
        WhopsCheckpoint oldCheckpoint = getCheckpoint(steppingPlayer);
        if (blockEntity.getPos() == null || blockEntity.getRot() == null) {
            WhopsCheckpoint newCheckpoint = WhopsCheckpoint.fromPlayer(steppingPlayer, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), new Vec2(steppingPlayer.getXRot(), steppingPlayer.getYRot()), level.dimension());
            setCheckpoint(steppingPlayer, newCheckpoint);
            if (oldCheckpoint == null || !isPosInBlock(oldCheckpoint.pos(), pos))
                ServerPlayNetworking.send(steppingPlayer, new ClientboundCheckpointFeedbackPayload(true));
        }
        else {
            WhopsCheckpoint newCheckpoint = WhopsCheckpoint.fromPlayer(steppingPlayer, blockEntity.getPos(), blockEntity.getRot(), level.dimension());
            setCheckpoint(steppingPlayer, newCheckpoint);
            if (oldCheckpoint == null || !(oldCheckpoint.pos().equals(newCheckpoint.pos()) && oldCheckpoint.rot().equals(newCheckpoint.rot())))
                ServerPlayNetworking.send(steppingPlayer, new ClientboundCheckpointFeedbackPayload(true));
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CheckpointBlockEntity(pos, state);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(
            @NonNull ItemStack itemStack,
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull InteractionHand hand,
            @NonNull BlockHitResult hitResult
    ) {
        if (level.isClientSide() || !(itemStack.is(WhopsItems.POSITION_SNAPSHOT) && player.isCreative())) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof CheckpointBlockEntity blockEntity)) return InteractionResult.PASS;
        StoredPositionComponent position = player.getItemInHand(hand).get(WhopsComponents.STORED_POSITION_COMPONENT_TYPE);
        if (position == null) return InteractionResult.PASS;
        blockEntity.store(position.pos(), position.rot());
        player.sendSystemMessage(Component.literal("Set checkpoint position!"));
        return InteractionResult.SUCCESS;
    }
}
