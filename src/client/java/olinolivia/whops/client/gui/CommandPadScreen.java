package olinolivia.whops.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.block.blockentity.CommandPadBlockEntity;
import olinolivia.whops.networking.ServerboundEditCommandPadPayload;
import org.jspecify.annotations.NonNull;

public class CommandPadScreen extends AbstractCommandBlockEditScreen {

    private final CommandPadBlockEntity blockEntity;

    public CommandPadScreen(CommandPadBlockEntity blockEntity) {
        super();
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        super.init();
        commandEdit.setValue(blockEntity.getCommand());
    }

    @Override
    protected @NonNull BaseCommandBlock getCommandBlock() {
        return new BaseCommandBlock() {
            @Override
            public void onUpdated(@NonNull ServerLevel level) {}

            @Override
            public @NonNull CommandSourceStack createCommandSourceStack(@NonNull ServerLevel level, @NonNull CommandSource source) {
                return new CommandSourceStack(source, Vec3.atCenterOf(blockEntity.getBlockPos()), Vec2.ZERO, level, LevelBasedPermissionSet.GAMEMASTER, this.getName().getString(), this.getName(), level.getServer(), null);
            }

            @Override
            public boolean isValid() {
                return true;
            }
        };
    }

    @Override
    protected int getPreviousY() {
        return 135;
    }

    @Override
    protected void populateAndSendPacket() {
        ClientPlayNetworking.send(new ServerboundEditCommandPadPayload(commandEdit.getValue(), blockEntity.getBlockPos()));
    }

}
