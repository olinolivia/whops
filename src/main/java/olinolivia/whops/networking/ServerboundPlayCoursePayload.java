package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ServerboundPlayCoursePayload(String courseName) implements CustomPacketPayload {

    public static final Identifier PLAY_COURSE_PAYLOAD_ID = Whops.id( "request_courses");

    public static final CustomPacketPayload.Type<ServerboundPlayCoursePayload> TYPE = new CustomPacketPayload.Type<>(PLAY_COURSE_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPlayCoursePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundPlayCoursePayload::courseName,
            ServerboundPlayCoursePayload::new
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
