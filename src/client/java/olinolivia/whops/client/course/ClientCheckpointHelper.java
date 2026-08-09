package olinolivia.whops.client.course;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.networking.ClientboundCheckpointFeedbackPayload;
import olinolivia.whops.networking.ClientboundFinishFeedbackPayload;
import olinolivia.whops.networking.ServerboundReturnPayload;
import olinolivia.whops.util.EffectHelper;

public class ClientCheckpointHelper {

    public static void returnClient(LocalPlayer player, WhopsCheckpoint checkpoint, boolean onlysprintTriggered) {
        player.setPos(checkpoint.pos());
        player.setXRot(checkpoint.rot().x);
        player.setYRot(checkpoint.rot().y);
        player.setOldPosAndRot();
        player.setDeltaMovement(Vec3.ZERO);
        ClientPlayNetworking.send(new ServerboundReturnPayload(onlysprintTriggered));
        EffectHelper.applyEffectMap(checkpoint.effects(), player);
    }

    public static void checkpointFeedback(boolean playSound) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player) {
            player.sendSystemMessage(Component.literal("You've hit a checkpoint!").withColor(0x55ff55));
            if (playSound) player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
        }
    }

    public static void finishFeedback(boolean playSound) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player) {
            player.sendSystemMessage(Component.literal("You've cleared the course! GG!").withColor(0x55ff55));
            if (playSound) player.playSound(SoundEvents.PLAYER_LEVELUP);
        }
    }

    static {

        ClientPlayNetworking.registerGlobalReceiver(ClientboundCheckpointFeedbackPayload.TYPE, (payload, _) ->
            checkpointFeedback(payload.playSound())
        );

        ClientPlayNetworking.registerGlobalReceiver(ClientboundFinishFeedbackPayload.TYPE, (payload, _) ->
                finishFeedback(payload.playSound())
        );

    }

    public static void init() {}

}
