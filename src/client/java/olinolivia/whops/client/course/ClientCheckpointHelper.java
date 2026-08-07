package olinolivia.whops.client.course;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.networking.ServerboundReturnPayload;

public class ClientCheckpointHelper {

    public static void returnClient(LocalPlayer player, WhopsCheckpoint checkpoint, boolean onlysprintTriggered) {
        player.setPos(checkpoint.pos());
        player.setXRot(checkpoint.rot().x);
        player.setYRot(checkpoint.rot().y);
        player.setOldPosAndRot();
        player.setDeltaMovement(Vec3.ZERO);
        ClientPlayNetworking.send(new ServerboundReturnPayload(onlysprintTriggered));
    }

}
