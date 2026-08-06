package olinolivia.whops.client.gamerule;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import olinolivia.whops.gamerule.LegacyGameRules;
import olinolivia.whops.networking.ClientboundLegacyGameRulesPayload;

public class ClientLegacyGameRuleTracker {

    public static LegacyGameRules legacyGameRules = LegacyGameRules.LATEST;

    static {
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((ignored1, ignored2) -> legacyGameRules = LegacyGameRules.LATEST);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundLegacyGameRulesPayload.TYPE, (payload, ignored) -> legacyGameRules = payload.legacyGameRules());
    }

    public static void init() {}

}
