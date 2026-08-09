package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ClientboundReplaceTimerPayload(
        long timer
) implements CustomPacketPayload {

    public static final Identifier REPLACE_TIMER_PAYLOAD_ID = Whops.id("replace_timer");

    public static final Type<ClientboundReplaceTimerPayload> TYPE = new Type<>(REPLACE_TIMER_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundReplaceTimerPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, ClientboundReplaceTimerPayload::timer,
            ClientboundReplaceTimerPayload::new
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
