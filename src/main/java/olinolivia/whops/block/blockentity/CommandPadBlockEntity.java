package olinolivia.whops.block.blockentity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import olinolivia.whops.networking.ServerboundEditCommandPadPayload;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public class CommandPadBlockEntity extends BlockEntity {

    public CommandPadBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WhopsBlockEntities.COMMAND_PAD, worldPosition, blockState);
    }

    private String command = "";
    private final ArrayList<Player> playersIn = new ArrayList<>();
    private final ArrayList<Player> playersJustIn = new ArrayList<>();

    public void addPlayer(Player player) {
        playersIn.add(player);
    }

    public boolean isPlayerIn(Player player) {
        return playersIn.contains(player) || playersJustIn.contains(player);
    }

    public static <T extends BlockEntity> void tick(Level ignoredLevel, BlockPos blockPos, BlockState ignoredBlockState, T entity) {
        if (!(entity instanceof CommandPadBlockEntity blockEntity)) return;
        blockEntity.playersJustIn.clear();
        blockEntity.playersIn.removeIf(player -> {
            blockEntity.playersJustIn.add(player);
            return !player.getBoundingBox().intersects(blockPos);
        });
    }

    public void setCommand(String newCommand) {
        command = newCommand;
        setChanged();
    }

    public String getCommand() {
        return command;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putString("command", command);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        command = input.getString("command").orElse("");
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null) return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    static {

        ServerPlayNetworking.registerGlobalReceiver(ServerboundEditCommandPadPayload.TYPE, (payload, context) -> {
            if (context.player().level().getBlockEntity(payload.pos()) instanceof CommandPadBlockEntity blockEntity && context.player().isCreative())
                blockEntity.setCommand(payload.command());
        });

    }

    public static void init() {}

}
