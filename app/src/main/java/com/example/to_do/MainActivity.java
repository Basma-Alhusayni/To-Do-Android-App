package com.example.to_do;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText todoInput, searchBox;
    private Button addBtn;
    private TextView counter;
    private RecyclerView todoList;
    private ArrayList<TodoItem> todos;
    private ArrayList<TodoItem> filteredTodos;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoInput = findViewById(R.id.todoInput);
        searchBox = findViewById(R.id.searchBox);
        addBtn = findViewById(R.id.addBtn);
        counter = findViewById(R.id.todoCounter);
        todoList = findViewById(R.id.todoList);

        todos = SharedPreferencesManager.loadTodoList(this);
        filteredTodos = new ArrayList<>(todos);

        adapter = new TodoAdapter(this, filteredTodos, this::updateCounter, todo -> {
            todos.remove(todo);
            filterTodos(searchBox.getText().toString());
            SharedPreferencesManager.saveTodoList(this, todos);
            updateCounter();
        });

        todoList.setLayoutManager(new LinearLayoutManager(this));
        todoList.setAdapter(adapter);

        // Add via button click
        addBtn.setOnClickListener(v -> addTodo());

        // Add via Enter key (keyboard)
        todoInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                addTodo();
                return true;
            }
            return false;
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTodos(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        updateCounter();
    }

    private void addTodo() {
        String newTodo = todoInput.getText().toString().trim();
        if (!newTodo.isEmpty()) {
            todos.add(new TodoItem(newTodo, false));
            filterTodos(searchBox.getText().toString());
            SharedPreferencesManager.saveTodoList(this, todos);
            todoInput.setText("");
            updateCounter();
        }
    }

    private void filterTodos(String query) {
        filteredTodos.clear();
        for (TodoItem t : todos) {
            if (t.getText().toLowerCase().contains(query.toLowerCase())) {
                filteredTodos.add(t);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateCounter() {
        counter.setText("Total To-Dos: " + todos.size());
    }
}
