package olinolivia.whops.client.gui.widget;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import olinolivia.whops.networking.ServerboundPlayCoursePayload;
import org.jspecify.annotations.NonNull;

import static olinolivia.whops.course.CourseHelper.*;

public class CourseSelectionList extends ObjectSelectionList<CourseSelectionList.CourseOption> {

    public CourseSelectionList(Minecraft minecraft, int width, int height, int x, int y, int defaultEntryHeight, String[] courseNames) {
        super(minecraft, width, height, y, defaultEntryHeight);
        setX(x);
        for (String courseName : courseNames) addEntry(new CourseOption(courseName));
    }

    public static class CourseOption extends ObjectSelectionList.Entry<CourseOption> {

        public final String COURSE_NAME;

        public CourseOption(String courseName) {
            super();
            COURSE_NAME = courseName;
            setHeight(16);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            Font font = Minecraft.getInstance().font;
            int color = Minecraft.getInstance().player != null && COURSE_NAME.equals(getCurrentCourseName(Minecraft.getInstance().player)) ? 0xffffff00 : 0xffffffff;
            graphics.text(font, COURSE_NAME, getContentX(), getContentYMiddle() - font.lineHeight / 2, color);
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.literal(COURSE_NAME);
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (doubleClick && Minecraft.getInstance().player != null && !COURSE_NAME.equals(getCurrentCourseName(Minecraft.getInstance().player))) {
                Minecraft.getInstance().gui.setScreen(null);
                ClientPlayNetworking.send(new ServerboundPlayCoursePayload(COURSE_NAME));
            }
            return super.mouseClicked(event, doubleClick);
        }

    }

}
