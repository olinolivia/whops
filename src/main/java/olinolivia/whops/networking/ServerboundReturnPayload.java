package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ServerboundReturnPayload(boolean onlysprintTriggered) implements CustomPacketPayload {

    public static final Identifier RETURN_PAYLOAD_ID = Whops.id( "return");

    public static final CustomPacketPayload.Type<ServerboundReturnPayload> TYPE = new CustomPacketPayload.Type<>(RETURN_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundReturnPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundReturnPayload::onlysprintTriggered,
            ServerboundReturnPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        PayloadTypeRegistry.serverboundPlay().register(TYPE, CODEC);
    }

    public static void init() {}

}
