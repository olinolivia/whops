package olinolivia.whops.block;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import olinolivia.whops.block.blockentity.CommandPadBlockEntity;
import olinolivia.whops.block.blockentity.WhopsBlockEntities;
import olinolivia.whops.gui.BlockGUIOpener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class CommandPadBlock extends PadBlock implements EntityBlock {

    public CommandPadBlock(Properties props) {
        super(props);
    }

    @Override
    boolean shouldTrigger(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, ServerPlayer steppingPlayer) {
        if (!(level.getBlockEntity(pos) instanceof CommandPadBlockEntity blockEntity)) return false;
        return !blockEntity.isPlayerIn(steppingPlayer);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
        return type == WhopsBlockEntities.COMMAND_PAD ? CommandPadBlockEntity::tick : null;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull BlockHitResult hitResult
    ) {
        if (!player.isCreative() || !(level.getBlockEntity(pos) instanceof CommandPadBlockEntity blockEntity)) return InteractionResult.PASS;
        if (player.isLocalPlayer()) BlockGUIOpener.openCommandPadGUI(blockEntity);
        return InteractionResult.SUCCESS;
    }

    @Override
    void trigger(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, ServerPlayer steppingPlayer) {
        if (!(level.getBlockEntity(pos) instanceof CommandPadBlockEntity blockEntity)) return;
        blockEntity.addPlayer(steppingPlayer);
        String command = blockEntity.getCommand();
        CommandSourceStack source = new CommandSourceStack(
                CommandSource.NULL,
                steppingPlayer.position(),
                new Vec2(steppingPlayer.getXRot(), steppingPlayer.getYRot()),
                steppingPlayer.level(),
                PermissionSet.ALL_PERMISSIONS,
                steppingPlayer.getPlainTextName(),
                steppingPlayer.getName(),
                Objects.requireNonNull(level.getServer()),
                steppingPlayer
        );
        Objects.requireNonNull(level.getServer()).getCommands().performCommand(
                source.dispatcher().parse(command, source),
                command
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CommandPadBlockEntity(pos, state);
    }

}
