package olinolivia.whops.course;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import olinolivia.whops.Whops;
import olinolivia.whops.networking.ClientboundListCoursesPayload;
import olinolivia.whops.networking.ServerboundPlayCoursePayload;
import olinolivia.whops.networking.ServerboundRequestCoursesPayload;

import java.util.HashMap;
import java.util.Map;

public class WorldCourseData extends SavedData {

    private final HashMap<String, WhopsCourse> COURSES = new HashMap<>();

    private WorldCourseData(Map<String, WhopsCourse> map) {
        COURSES.putAll(map);
    }

    private HashMap<String, WhopsCourse> getCourses() {
        return COURSES;
    }

    public WorldCourseData() {}

    public void addCourse(String name, WhopsCourse course) {
        COURSES.put(name, course);
        setDirty();
    }

    public void removeCourse(String name) {
        COURSES.remove(name);
        setDirty();
    }

    public WhopsCourse getCourse(String name) {
        return COURSES.get(name);
    }

    public String[] getCourseNames() {
        return COURSES.keySet().toArray(new String[0]);
    }

    public boolean courseExists(String name) {
        return COURSES.containsKey(name);
    }

    public static final Codec<WorldCourseData> CODEC = Codec.unboundedMap(Codec.STRING, WhopsCourse.CODEC).xmap(WorldCourseData::new, WorldCourseData::getCourses);

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<WorldCourseData> TYPE = new SavedDataType<>(
            Whops.id("course_data"),
            WorldCourseData::new,
            CODEC,
            null
    );

    public static WorldCourseData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    static {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestCoursesPayload.TYPE, (_, context) ->
            ServerPlayNetworking.send(context.player(), new ClientboundListCoursesPayload(String.join("\n", WorldCourseData.get(context.player().level()).getCourseNames())))
        );
        ServerPlayNetworking.registerGlobalReceiver(ServerboundPlayCoursePayload.TYPE, ((payload, context) -> {
            WorldCourseData worldCourseData = WorldCourseData.get(context.player().level());
            if (worldCourseData.courseExists(payload.courseName())) WhopsCourse.switchCourses(context.player(), payload.courseName());
        }));
    }

    public static void init() {}

}
