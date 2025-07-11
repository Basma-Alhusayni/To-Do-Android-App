package com.example.to_do;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "todo_prefs";
    private static final String KEY_TODOS = "todos";

    public static void saveTodoList(Context context, ArrayList<TodoItem> todos) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(todos);
        editor.putString(KEY_TODOS, json);
        editor.apply();
    }

    public static ArrayList<TodoItem> loadTodoList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_TODOS, null);
        Type type = new TypeToken<ArrayList<TodoItem>>() {}.getType();
        return json == null ? new ArrayList<>() : new Gson().fromJson(json, type);
    }
}
