package olinolivia.whops.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import olinolivia.whops.client.gui.widget.CourseSelectionList;
import olinolivia.whops.networking.ClientboundListCoursesPayload;
import olinolivia.whops.networking.ServerboundPlayCoursePayload;
import olinolivia.whops.networking.ServerboundRequestCoursesPayload;
import org.jspecify.annotations.NonNull;

import static olinolivia.whops.course.CourseHelper.*;

public class CoursesScreen extends Screen {

    private final Screen PARENT;
    private CourseSelectionList courseList;
    private Button playButton;

    private static final int CONTENT_WIDTH = 250;
    private static final int CONTENT_HEIGHT = 250;

    public CoursesScreen(Screen parent) {
        super(Component.literal("Courses"));
        PARENT = parent;
    }

    private static CoursesScreen waiting;

    @Override
    protected void init() {
        waiting = this;
        ClientPlayNetworking.send(new ServerboundRequestCoursesPayload(true));
    }

    private void onReceiveCourseNames(String[] courseNames) {
        waiting = null;
        courseList = new CourseSelectionList(minecraft, CONTENT_WIDTH, CONTENT_HEIGHT, (width - CONTENT_WIDTH) / 2, (height - CONTENT_HEIGHT) / 2, 16, courseNames);
        playButton = Button.builder(Component.literal("Play"), _ -> {
            minecraft.gui.setScreen(null);
            waiting = null;
            if (courseList.getSelected() != null) ClientPlayNetworking.send(new ServerboundPlayCoursePayload(courseList.getSelected().COURSE_NAME));
        }).bounds((width - CONTENT_WIDTH) / 2, (height + CONTENT_HEIGHT) / 2, CONTENT_WIDTH, 20).build();
        playButton.active = false;
        addRenderableWidget(courseList);
        addRenderableWidget(playButton);
    }

    @Override
    public void tick() {
        super.tick();
        if (playButton != null && courseList != null) playButton.active = courseList.getSelected() != null && Minecraft.getInstance().player != null && !courseList.getSelected().COURSE_NAME.equals(getCurrentCourseName(Minecraft.getInstance().player));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        if (waiting == this) {
            String text = "Fetching courses...";
            graphics.text(font, text, (width - font.width(text)) / 2, (height - font.lineHeight) / 2, 0xFFFFFFFF, true);
        }
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(PARENT);
    }

    static {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundListCoursesPayload.TYPE, (payload, _) -> {
            if (waiting == null) return;
            waiting.onReceiveCourseNames(payload.courseNames().isEmpty() ? new String[0] : payload.courseNames().split("\n"));
        });
    }

    public static void init_() {}

}
