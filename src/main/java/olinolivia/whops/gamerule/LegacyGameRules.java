package olinolivia.whops.gamerule;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ClientboundLegacyGameRulesPayload;

import java.util.function.Function;

public record LegacyGameRules(
        double sneakHeight,
        double sprintLeniency,
        double minimumVelocity,
        boolean allowSwimming,
        boolean allowSprintSneak,
        boolean allowBounceBoost,
        boolean stepHeightLedges,
        boolean xzFix,
        boolean easyClimbing,
        boolean allowBlipUp,
        boolean smartOnPosition
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, LegacyGameRules> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, LegacyGameRules::sneakHeight,
            ByteBufCodecs.DOUBLE, LegacyGameRules::sprintLeniency,
            ByteBufCodecs.DOUBLE, LegacyGameRules::minimumVelocity,
            ByteBufCodecs.BOOL, LegacyGameRules::allowSwimming,
            ByteBufCodecs.BOOL, LegacyGameRules::allowSprintSneak,
            ByteBufCodecs.BOOL, LegacyGameRules::allowBounceBoost,
            ByteBufCodecs.BOOL, LegacyGameRules::stepHeightLedges,
            ByteBufCodecs.BOOL, LegacyGameRules::xzFix,
            ByteBufCodecs.BOOL, LegacyGameRules::easyClimbing,
            ByteBufCodecs.BOOL, LegacyGameRules::allowBlipUp,
            ByteBufCodecs.BOOL, LegacyGameRules::smartOnPosition,
            LegacyGameRules::new
    );

    public static final LegacyGameRules LATEST = new LegacyGameRules(
            1.5,
            0.13962634,
            0.003,
            true,
            true,
            true,
            true,
            true,
            true,
            false,
            true
    );

    public static final GameRule<Double> GAME_RULE_SNEAK_HEIGHT = numericLegacyGameRule("sneak_height", LegacyGameRules::sneakHeight);
    public static final GameRule<Double> GAME_RULE_SPRINT_LENIENCY= numericLegacyGameRule("sprint_leniency", LegacyGameRules::sprintLeniency);
    public static final GameRule<Double> GAME_RULE_MINIMUM_VELOCITY = numericLegacyGameRule("minimum_velocity", LegacyGameRules::minimumVelocity);
    public static final GameRule<Boolean> GAME_RULE_ALLOW_SWIMMING = booleanLegacyGameRule("allow_swimming", LegacyGameRules::allowSwimming);
    public static final GameRule<Boolean> GAME_RULE_ALLOW_SPRINT_SNEAK = booleanLegacyGameRule("allow_sprint_sneak", LegacyGameRules::allowSprintSneak);
    public static final GameRule<Boolean> GAME_RULE_ALLOW_BOUNCE_BOOST = booleanLegacyGameRule("allow_bounce_boost", LegacyGameRules::allowBounceBoost);
    public static final GameRule<Boolean> GAME_RULE_STEP_HEIGHT_LEDGES = booleanLegacyGameRule("step_height_ledges", LegacyGameRules::allowBounceBoost);
    public static final GameRule<Boolean> GAME_RULE_XZ_FIX = booleanLegacyGameRule("xz_fix", LegacyGameRules::allowBounceBoost);
    public static final GameRule<Boolean> GAME_RULE_EASY_CLIMBING = booleanLegacyGameRule("easy_climbing", LegacyGameRules::allowBounceBoost);
    public static final GameRule<Boolean> GAME_RULE_ALLOW_BLIP_UP = booleanLegacyGameRule("allow_blip_up", LegacyGameRules::allowBounceBoost);
    public static final GameRule<Boolean> GAME_RULE_SMART_ON_POSITION = booleanLegacyGameRule("smart_on_position", LegacyGameRules::allowBounceBoost);

    public static LegacyGameRules extract(MinecraftServer server) {
        GameRules gameRules = server.getGameRules();
        return new LegacyGameRules(
                gameRules.get(GAME_RULE_SNEAK_HEIGHT),
                gameRules.get(GAME_RULE_SPRINT_LENIENCY),
                gameRules.get(GAME_RULE_MINIMUM_VELOCITY),
                gameRules.get(GAME_RULE_ALLOW_SWIMMING),
                gameRules.get(GAME_RULE_ALLOW_SPRINT_SNEAK),
                gameRules.get(GAME_RULE_ALLOW_BOUNCE_BOOST),
                gameRules.get(GAME_RULE_STEP_HEIGHT_LEDGES),
                gameRules.get(GAME_RULE_XZ_FIX),
                gameRules.get(GAME_RULE_EASY_CLIMBING),
                gameRules.get(GAME_RULE_ALLOW_BLIP_UP),
                gameRules.get(GAME_RULE_SMART_ON_POSITION)
        );
    }

    static {
        GameRuleEvents.changeCallback(GAME_RULE_SNEAK_HEIGHT).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_SPRINT_LENIENCY).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_MINIMUM_VELOCITY).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_ALLOW_SWIMMING).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_ALLOW_SPRINT_SNEAK).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_ALLOW_BOUNCE_BOOST).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_STEP_HEIGHT_LEDGES).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_XZ_FIX).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_EASY_CLIMBING).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_ALLOW_BLIP_UP).register((ignored, server) -> syncToAll(server));
        GameRuleEvents.changeCallback(GAME_RULE_SMART_ON_POSITION).register((ignored, server) -> syncToAll(server));

        ServerPlayerEvents.JOIN.register(player -> sync(player.level().getServer(), player));
    }

    @FunctionalInterface
    public interface LegacyAccessor {

        LegacyGameRules get(Level level);

    }

    private static LegacyAccessor accessor;

    public static void setAccessor(LegacyAccessor accessor) {
        LegacyGameRules.accessor = accessor;
    }

    public static LegacyGameRules get(Level level) {
        return accessor.get(level);
    }

    public static void sync(MinecraftServer server, ServerPlayer player) {
        ServerPlayNetworking.send(player, new ClientboundLegacyGameRulesPayload(extract(server)));
    }

    public static void syncToAll(MinecraftServer server) {
        ClientboundLegacyGameRulesPayload payload = new ClientboundLegacyGameRulesPayload(extract(server));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) ServerPlayNetworking.send(player, payload);
    }

    private static GameRule<Double> numericLegacyGameRule(String name, Function<LegacyGameRules, Double> getter) {
        return GameRuleBuilder
                .forDouble(getter.apply(LATEST))
                .category(GameRuleCategory.PLAYER)
                .buildAndRegister(Whops.id("legacy/" + name));
    }

    private static GameRule<Boolean> booleanLegacyGameRule(String name, Function<LegacyGameRules, Boolean> getter) {
        return GameRuleBuilder
                .forBoolean(getter.apply(LATEST))
                .category(GameRuleCategory.PLAYER)
                .buildAndRegister(Whops.id("legacy/" + name));
    }

    public static void init() {}

}

