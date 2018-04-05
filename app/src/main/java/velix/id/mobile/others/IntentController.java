package velix.id.mobile.others;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Debam on 5/15/2017.
 */

public class IntentController {

    public static void sendIntent(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static <T extends Object> void sendIntent(Context context, Class<?> cls, Map<String, T> map){
        Intent intent = new Intent(context, cls);
        for (Map.Entry<String, T> entry: map.entrySet()){
            try {
                intent.putExtra(entry.getKey(), Serializer.serialize(entry.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Object receiveIntent(Intent intent, String key){
        try {
            return Serializer.deserialize(intent.getByteArrayExtra(key));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class CustomHashMap extends HashMap {
        public <T extends Object> IntentController.CustomHashMap put(String key, T value) {
            super.put(key, value);
            return CustomHashMap.this;
        }
    }

}
