package olinolivia.whops.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import olinolivia.whops.course.WhopsCourse;
import olinolivia.whops.networking.ClientboundListCoursesPayload;
import olinolivia.whops.networking.ServerboundPlayCoursePayload;
import olinolivia.whops.networking.ServerboundRequestCoursesPayload;
import org.jspecify.annotations.NonNull;

public class CoursesScreen extends Screen {

    private final Screen PARENT;
    private Button[] courseButtons;
    private int page = 0;

    private Button previousPage;
    private Button nextPage;

    private static final int BUTTON_WIDTH = 250;
    private static final int ROWS = 8;

    public CoursesScreen(Screen parent) {
        super(Component.literal("Courses"));
        PARENT = parent;
    }

    private static CoursesScreen waiting;

    private Button playCourseButton(String courseName, int height) {
        return Button.builder(Component.literal(courseName), _ -> {
            ClientPlayNetworking.send(new ServerboundPlayCoursePayload(courseName));
            minecraft.gui.setScreen(null);
        }).bounds((width - BUTTON_WIDTH) / 2, this.height / 2 - ROWS * 10 + height * 20, BUTTON_WIDTH, 20).build();
    }

    @Override
    protected void init() {
        waiting = this;
        ClientPlayNetworking.send(new ServerboundRequestCoursesPayload(true));
    }

    private void updateLayout() {
        for (Button courseButton : courseButtons) courseButton.visible = false;
        for (int j = 0; j < ROWS; j++) {
            if (page*ROWS+j < courseButtons.length) courseButtons[page*ROWS+j].visible = true;
        }
        previousPage.active = page > 0;
        nextPage.active = (page+1) * ROWS < courseButtons.length;
    }

    private void onReceiveCourseNames(String[] courseNames) {
        waiting = null;
        assert minecraft.player != null;
        courseButtons = new Button[courseNames.length];
        int i = 0;
        for (String courseName : courseNames) {
            courseButtons[i] = playCourseButton(courseName, i % ROWS);
            if (i >= ROWS) courseButtons[i].visible = false;
            if (courseName.equals(minecraft.player.getAttached(WhopsCourse.CURRENT_COURSE_ATTACHMENT))) courseButtons[i].active = false;
            i++;
        }
        previousPage = Button.builder(Component.literal("Previous"), _ -> {
            page--;
            updateLayout();
        }).bounds((width - BUTTON_WIDTH) / 2 - 100, height / 2 - 10, 100, 20).build();
        nextPage = Button.builder(Component.literal("Next"), _ -> {
            page++;
            updateLayout();
        }).bounds((width + BUTTON_WIDTH) / 2, height / 2 - 10, 100, 20).build();
        for (Button courseButton : courseButtons) addRenderableWidget(courseButton);
        addRenderableWidget(previousPage);
        addRenderableWidget(nextPage);
        updateLayout();
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
        waiting = null;
    }

    static {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundListCoursesPayload.TYPE, (payload, _) -> {
            if (waiting == null) return;
            waiting.onReceiveCourseNames(payload.courseNames().isEmpty() ? new String[0] : payload.courseNames().split("\n"));
        });
    }

    public static void init_() {}

}
