package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import olinolivia.whops.gamerule.LegacyGameRules;
import org.jspecify.annotations.NonNull;

public record ClientboundLegacyGameRulesPayload(
        LegacyGameRules legacyGameRules
) implements CustomPacketPayload {

    public static final Identifier LEGACY_SETTINGS_PAYLOAD_ID = Whops.id("legacy_settings");

    public static final CustomPacketPayload.Type<ClientboundLegacyGameRulesPayload> TYPE = new CustomPacketPayload.Type<>(LEGACY_SETTINGS_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLegacyGameRulesPayload> CODEC = StreamCodec.composite(
            LegacyGameRules.STREAM_CODEC, ClientboundLegacyGameRulesPayload::legacyGameRules,
            ClientboundLegacyGameRulesPayload::new
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
