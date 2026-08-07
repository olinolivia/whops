package olinolivia.whops.client.course;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import olinolivia.whops.Whops;
import olinolivia.whops.course.WhopsCourse;
import olinolivia.whops.networking.ClientboundReplaceTimerPayload;

public class CourseTimer {

    public static long timeElapsed = 0;

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT) == null) return;

        long milliseconds = (timeElapsed * 50) % 1000;
        long seconds = (timeElapsed / 20) % 60;
        long minutes = (timeElapsed / 1200) % 60;
        long hours = (timeElapsed / 72000);
        String text = (hours > 0 ? String.format("%02d:", hours) : "") + String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);

        graphics.text(minecraft.font, text, (graphics.guiWidth() - minecraft.font.width(text)) / 2, graphics.guiHeight() - minecraft.font.lineHeight - 64, 0xFFFFFFFF);
    }

    static {

        ClientPlayNetworking.registerGlobalReceiver(ClientboundReplaceTimerPayload.TYPE, (payload, _) -> timeElapsed = payload.timer());
        ClientTickEvents.END_LEVEL_TICK.register(_ -> timeElapsed++);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Whops.id("timer"), CourseTimer::extract);

    }

    public static void init() {}

}
