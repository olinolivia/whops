package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ServerboundRequestCoursesPayload(boolean gui) implements CustomPacketPayload {

    public static final Identifier PLAY_COURSE_PAYLOAD_ID = Whops.id( "play_course");

    public static final CustomPacketPayload.Type<ServerboundRequestCoursesPayload> TYPE = new CustomPacketPayload.Type<>(PLAY_COURSE_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestCoursesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundRequestCoursesPayload::gui,
            ServerboundRequestCoursesPayload::new
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

