package com.example.to_do;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private Context context;
    private ArrayList<TodoItem> todos;
    private OnDataChangeListener listener;
    private OnItemActionListener actionListener;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    public interface OnItemActionListener {
        void onDelete(TodoItem item);
    }

    public TodoAdapter(Context context, ArrayList<TodoItem> todos, OnDataChangeListener listener, OnItemActionListener actionListener) {
        this.context = context;
        this.todos = todos;
        this.listener = listener;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todo = todos.get(position);
        holder.todoText.setText(todo.getText());
        holder.todoCheck.setOnCheckedChangeListener(null); // prevent multiple triggers
        holder.todoCheck.setChecked(todo.isCompleted());   // set initial state

        holder.todoCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setCompleted(isChecked);
            saveTodos();
            notifyItemChanged(holder.getAdapterPosition());
        });

        if (todo.isCompleted()) {
            holder.todoText.setPaintFlags(holder.todoText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.todoText.setTextColor(Color.GRAY);
        } else {
            holder.todoText.setPaintFlags(holder.todoText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.todoText.setTextColor(Color.BLACK);
        }

        holder.editBtn.setOnClickListener(v -> {
            EditText input = new EditText(context);
            input.setText(todo.getText());

            // Wrap EditText in a FrameLayout with padding
            FrameLayout container = new FrameLayout(context);
            int margin = (int) (20 * context.getResources().getDisplayMetrics().density); // 20dp to pixels
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(margin, margin, margin, margin);
            input.setLayoutParams(params);
            container.addView(input);

            new AlertDialog.Builder(context)
                    .setTitle("Edit Todo")
                    .setView(container)
                    .setPositiveButton("OK", (dialog, which) -> {
                        todo.setText(input.getText().toString());
                        notifyDataSetChanged();
                        saveTodos();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        holder.deleteBtn.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(todo);
            }
        });

    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox todoCheck;
        TextView todoText;
        Button editBtn, deleteBtn;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoCheck = itemView.findViewById(R.id.todoCheck);
            todoText = itemView.findViewById(R.id.todoText);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    private void saveTodos() {
        SharedPreferencesManager.saveTodoList(context, todos);
    }
}
