package olinolivia.whops.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.level.Level;
import olinolivia.whops.client.gamerule.ClientLegacyGameRuleTracker;
import olinolivia.whops.client.keybind.WhopsKeybinds;
import olinolivia.whops.gamerule.LegacyGameRules;

public class WhopsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		LegacyGameRules.setAccessor((_) -> ClientLegacyGameRuleTracker.legacyGameRules);

		ClientLegacyGameRuleTracker.init();
		WhopsKeybinds.init();

	}
}