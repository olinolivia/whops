package olinolivia.whops.client;

import net.fabricmc.api.ClientModInitializer;
import olinolivia.whops.client.course.ClientCheckpointHelper;
import olinolivia.whops.client.course.CourseTimer;
import olinolivia.whops.client.flag.ClientFlagHelper;
import olinolivia.whops.client.gamerule.ClientLegacyGameRuleTracker;
import olinolivia.whops.client.gui.CoursesScreen;
import olinolivia.whops.client.keybind.WhopsKeybinds;
import olinolivia.whops.gamerule.LegacyGameRules;

public class WhopsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		LegacyGameRules.setAccessor((_) -> ClientLegacyGameRuleTracker.legacyGameRules);

		ClientCheckpointHelper.init();
		CourseTimer.init();
		ClientFlagHelper.init();
		ClientLegacyGameRuleTracker.init();
		WhopsKeybinds.init();
		CoursesScreen.init_();

	}

}