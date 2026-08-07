package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ClientboundListCoursesPayload(
        String courseNames
) implements CustomPacketPayload {

    public static final Identifier LEGACY_SETTINGS_PAYLOAD_ID = Whops.id("list_courses");

    public static final Type<ClientboundListCoursesPayload> TYPE = new Type<>(LEGACY_SETTINGS_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundListCoursesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientboundListCoursesPayload::courseNames,
            ClientboundListCoursesPayload::new
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
