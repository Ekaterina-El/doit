package ka.el.doit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ka.el.doit.Adapter.ToDoAdapter;
import ka.el.doit.Model.ToDoTaskModel;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageButton singOut, searchFormBtn;
    private EditText searchFormInput;
    private FloatingActionButton fab;
    private RecyclerView tasksListView;
    private RelativeLayout emptyTodo;

    Dialog dialog;
    private ImageView preloader;
    private Animation anim_preloader;

    private List<ToDoTaskModel> tasks;
    private List<ToDoTaskModel> filteredList;
    private ToDoAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        isUnlogin();
        init();
        getDataInFB();
    }

    private void getDataInFB() {
        startPreloader();
        db.collection("todoLists")
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    filteredList.clear();
                                    tasks.clear();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {

                                        Map<String, Object> data = doc.getData();

                                        ToDoTaskModel item = new ToDoTaskModel();
                                        Long status = (Long) data.get("status");

                                        item.setId(doc.getId());
                                        item.setStatusTodo(status.intValue());
                                        item.setTextTodo((String) data.get("task"));

                                        tasks.add(item);
                                        filteredList.add(item);
                                    }
                                    tasksAdapter.setTasks(filteredList);
                                    tasksAdapter.notifyDataSetChanged();
                                    tasksListView.setAdapter(tasksAdapter);
                                    stopPreloader();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Getting todos: ERR", Toast.LENGTH_SHORT).show();
                                    stopPreloader();
                                }

                                if (filteredList.size() == 0) {
                                    showEmptyBox();
                                } else {
                                    invisibleEmptyBox();
                                }
                            }
                        }
                );
    }



    public void setTasks(List<ToDoTaskModel> todos) {
        this.tasks = todos;
        searchTodos(searchFormInput.getText().toString());
    }

    private void init() {
        // Empty box - начало
        emptyTodo = findViewById(R.id.emptyTodo);
        // Empty box - конец

        // Preloader dialog - начало
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setContentView(R.layout.preloader);

        preloader = dialog.findViewById(R.id.preloader);
        preloader.setVisibility(View.INVISIBLE);
        anim_preloader = AnimationUtils.loadAnimation(this, R.anim.preloader);
        anim_preloader.setRepeatCount(Animation.INFINITE);
        // Preloader dialog - конец

        // Работа с формой поиска - начало
        searchFormInput = findViewById(R.id.searchFormInput);

        searchFormBtn = findViewById(R.id.searchFormBtn);
        searchFormBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchTodos(searchFormInput.getText().toString());
                    }
                }
        );
        // Работа с формой поиска - конец

        // Фаб-кнопка для перехода на эркан создания новой задачи - начало
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goAddNewTask();
                    }
                }
        );
        // Фаб-кнопка для перехода на эркан создания новой задачи - конец

        // Начальна работа с опреативным хранилищем - начало
        tasks = new ArrayList<>();
        filteredList = new ArrayList<>();
        tasksAdapter = new ToDoAdapter(this);

        tasksListView = findViewById(R.id.recyclerToDoList);
        tasksListView.setLayoutManager(new LinearLayoutManager(this)); // !Important
        tasksListView.setAdapter(tasksAdapter);
        // Начальна работа с опреативным хранилищем - конец

        // Работа с кнопкой выхода из профиля - начало
        singOut = findViewById(R.id.singOut);
        singOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singOut();
                    }
                }
        );
        // Работа с кнопкой выхода из профиля - конец

        String uID = mAuth.getCurrentUser().getUid();
    }

    private void startPreloader() {
        preloader.startAnimation(anim_preloader);
        dialog.show();

    }

    private void stopPreloader() {
        dialog.dismiss();
    }


    private void searchTodos(String filter) {
        filteredList = new ArrayList<>();

        for (ToDoTaskModel task : tasks) {
            String text = task.getTextTodo();
            if (text.toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(task);
            }
        }

        if (filteredList.size() == 0) {
            
            showEmptyBoxSearch();
        } else {
            invisibleEmptyBox();
        }

        tasksAdapter.setTasks(filteredList);
        tasksAdapter.notifyDataSetChanged();
    }

    private void showEmptyBoxSearch() {
        TextView t = emptyTodo.findViewById(R.id.emptyText);
        t.setText(R.string.todo_list_empty_search);

        showEmptyBox();
    }

    private void invisibleEmptyBox() {
        TextView t = emptyTodo.findViewById(R.id.emptyText);
        t.setText(R.string.todo_list_empty);
        emptyTodo.setAlpha(0.0f);
    }

    private void showEmptyBox() {
        emptyTodo.setAlpha(1.0f);
    }

    private void goAddNewTask() {
        startActivity(
                new Intent(
                        ProfileActivity.this, AddNewTaskActivity.class
                )
        );
    }

    private void singOut() {
        mAuth.signOut();
        goLogin();
    }

    private void isUnlogin() {
        FirebaseUser cu = mAuth.getCurrentUser();
        if (cu == null) {
            goLogin();
        }
    }

    private void goLogin() {
        startActivity(
                new Intent(
                        ProfileActivity.this, LoginActivity.class
                )
        );
    }
}