package olinolivia.whops.client.flag;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import olinolivia.whops.client.course.ClientCheckpointHelper;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.flag.WhopsFlags;

public abstract class ClientFlagHelper {

    static {

        ClientTickEvents.END_LEVEL_TICK.register(_ -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && (!player.isSprinting() || player.isCrouching()) && player.getAttachedOrCreate(WhopsFlags.FLAGS_ATTACHMENT).onlySprint()) {
                try {
                    WhopsCheckpoint checkpoint = player.getAttachedOrThrow(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
                    if (player.distanceToSqr(checkpoint.pos()) > 1.0e-5) ClientCheckpointHelper.returnClient(player, checkpoint, true);
                } catch (NullPointerException ignored) {}
            }
        });

    }

    public static void init() {}

}
