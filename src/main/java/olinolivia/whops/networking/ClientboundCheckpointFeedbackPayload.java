package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ClientboundCheckpointFeedbackPayload(
        boolean playSound
) implements CustomPacketPayload {

    public static final Identifier CHECKPOINT_FEEDBACK_PAYLOAD_ID = Whops.id("checkpoint_feedback");

    public static final Type<ClientboundCheckpointFeedbackPayload> TYPE = new Type<>(CHECKPOINT_FEEDBACK_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCheckpointFeedbackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundCheckpointFeedbackPayload::playSound,
            ClientboundCheckpointFeedbackPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);
    }

    public static void init() {}

}
