package manager.utils;

import com.google.gson.*;
import manager.FileBackedTaskManager;
import manager.HttpTaskManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.interfaces.HistoryManager;
import manager.interfaces.TaskManager;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(8078);
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }


    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(localDateTime));
            }
        });
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }
        });
        return gsonBuilder.create();
    }
}
