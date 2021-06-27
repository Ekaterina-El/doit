package ka.el.doit.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import ka.el.doit.AddNewTaskActivity;
import ka.el.doit.Model.ToDoTaskModel;
import ka.el.doit.ProfileActivity;
import ka.el.doit.R;
import ka.el.doit.Utils.FirebaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoTaskModel> todoList = new ArrayList<>();
    private final ProfileActivity activity;
    private final FirebaseHandler fh = new FirebaseHandler();

    public ToDoAdapter(ProfileActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoTaskModel taskItem = todoList.get(position);
        holder.task.setText(taskItem.getTextTodo());
        holder.task.setChecked(taskItem.getStatusTodo() != 0);

        DocumentReference doc = fh.getRefDocById(taskItem.getId());


        holder.task.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int nextStatus = taskItem.getStatusTodo() == 0 ? 1 : 0;

                        doc.update(
                                "status", nextStatus
                        ).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        taskItem.setStatusTodo(nextStatus);
                                        holder.task.setChecked(nextStatus != 0);
                                    }
                                }
                        );


                    }
                }
        );

        // Обработчик кнопки редактирования - начало
        holder.editBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, AddNewTaskActivity.class);
                        intent.putExtra("id", taskItem.getId());
                        intent.putExtra("task", taskItem.getTextTodo());
                        activity.startActivity(intent);
                    }
                }
        );
        // Обработчик кнопки редактирования - конец

        // Обработчик кнопки удаленя - начало
        holder.deleteBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doc.delete()
                                .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(activity, "Task \"" + taskItem.getTextTodo() + "\" has been deleted",
                                                        Toast.LENGTH_SHORT).show();

                                                todoList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                )
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, "Task\"" + taskItem.getTextTodo() + "\" wasn`t deleted: something went " +
                                                                "wrong",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                    }
                }
        );
        // Обработчик кнопки удаленя - конец

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTasks(List<ToDoTaskModel> todoList) {
        this.todoList = todoList;
    }

    public void editItem(int position) {
        // Edit item
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        ImageButton deleteBtn, editBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.task = itemView.findViewById(R.id.taskCheckbox);
            this.deleteBtn = itemView.findViewById(R.id.deleteTask);
            this.editBtn = itemView.findViewById(R.id.editTask);
        }
    }
}
