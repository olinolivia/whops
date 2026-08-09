package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ClientboundFinishFeedbackPayload(
        boolean playSound
) implements CustomPacketPayload {

    public static final Identifier FINISH_FEEDBACK_PAYLOAD_ID = Whops.id("finish_feedback");

    public static final Type<ClientboundFinishFeedbackPayload> TYPE = new Type<>(FINISH_FEEDBACK_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundFinishFeedbackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundFinishFeedbackPayload::playSound,
            ClientboundFinishFeedbackPayload::new
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
