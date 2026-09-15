package olinolivia.whops.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import olinolivia.whops.Whops;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.client.course.ClientCheckpointHelper;

public class WhopsKeybinds {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Whops.id("whops")
    );

    public static final KeyMapping RETURN_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.whops.return",
                    InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_B,
                    CATEGORY
            )
    );

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (RETURN_KEY.consumeClick()) {
                    LocalPlayer player = client.player;
                    if (player != null) {
                        WhopsCheckpoint checkpoint = player.getAttached(WhopsCheckpoint.CHECKPOINT_ATTACHMENT);
                        if (checkpoint != null) {
                            ClientCheckpointHelper.returnClient(player, checkpoint, false);
                        } else player.sendOverlayMessage(Component.literal("No checkpoint to return to!").withColor(0xFF5555));
                    }
                }
        });
    }

    public static void init() {}

}
