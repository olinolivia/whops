package olinolivia.whops.flag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import olinolivia.whops.Whops;

import java.util.Arrays;

public record WhopsFlags(
        boolean onlySprint,
        boolean noSprint,
        boolean noJump
) {

    public static final WhopsFlags DEFAULT = new WhopsFlags(
            false,
            false,
            false
    );

    public static final String[] flagNames = new String[]{
            "only_sprint",
            "no_sprint",
            "no_jump"
    };

    public static void set(ServerPlayer player, String flagName, boolean value) throws NoSuchFieldException {
        if (!Arrays.stream(flagNames).toList().contains(flagName)) throw new NoSuchFieldException();
        player.modifyAttached(FLAGS_ATTACHMENT, flags -> switch (flagName) {
            case "only_sprint" -> new WhopsFlags(value, flags.noSprint, flags.noJump);
            case "no_sprint" -> new WhopsFlags(flags.onlySprint, value, flags.noJump);
            case "no_jump" -> new WhopsFlags(flags.onlySprint, flags.noSprint, value);
            default -> flags;
        });
    }

    public static final Codec<WhopsFlags> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("only_sprint").forGetter(WhopsFlags::onlySprint),
            Codec.BOOL.fieldOf("no_sprint").forGetter(WhopsFlags::noSprint),
            Codec.BOOL.fieldOf("no_jump").forGetter(WhopsFlags::noJump)
    ).apply(i, WhopsFlags::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WhopsFlags> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, WhopsFlags::onlySprint,
            ByteBufCodecs.BOOL, WhopsFlags::noSprint,
            ByteBufCodecs.BOOL, WhopsFlags::noJump,
            WhopsFlags::new
    );

    public static WhopsFlags get(Player player) {
        return player.getAttached(FLAGS_ATTACHMENT);
    }

    public static final AttachmentType<WhopsFlags> FLAGS_ATTACHMENT = AttachmentRegistry.create(
            Whops.id("flags"),
            builder -> builder
                    .initializer(() -> DEFAULT)
                    .syncWith(STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
                    .persistent(CODEC)
                    .copyOnDeath()
    );

}
