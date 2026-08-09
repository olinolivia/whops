package olinolivia.whops.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import olinolivia.whops.Whops;
import org.jspecify.annotations.NonNull;

public record ServerboundEditCommandPadPayload(String command, BlockPos pos) implements CustomPacketPayload {

    public static final Identifier EDIT_COMMAND_PAD = Whops.id( "edit_command_pad");

    public static final Type<ServerboundEditCommandPadPayload> TYPE = new Type<>(EDIT_COMMAND_PAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEditCommandPadPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundEditCommandPadPayload::command,
            BlockPos.STREAM_CODEC, ServerboundEditCommandPadPayload::pos,
            ServerboundEditCommandPadPayload::new
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
