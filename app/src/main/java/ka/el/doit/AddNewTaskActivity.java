package ka.el.doit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

import ka.el.doit.Utils.FirebaseHandler;

public class AddNewTaskActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private EditText newTaskText;
    private MaterialButton saveTaskBtn;

    private FirebaseHandler fh = new FirebaseHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        getSupportActionBar().hide();

        init();
    }

    private void init() {
        newTaskText = findViewById(R.id.taskText);

        saveTaskBtn = findViewById(R.id.saveTaskBtn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = (String) extras.get("id"), text = (String) extras.get("task");
            newTaskText.setText(text);
        }

        saveTaskBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (extras != null) {
                            String id = (String) extras.get("id");
                            editTask(id, newTaskText.getText().toString());
                        } else {
                            saveTask();
                        }
                    }
                }
        );

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goHome();
                    }
                }
        );
    }

    private void editTask(String id, String text) {
        if (text.equals("")) {
            Toast.makeText(this, "The text box cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            fh.getRefDocById(id).update("task", text)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddNewTaskActivity.this, "Task edited", Toast.LENGTH_SHORT).show();
                                    goHome();
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddNewTaskActivity.this, "Task not edited: something went wrong...", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        }

    }

    private void saveTask() {
        String task = newTaskText.getText().toString();

        if (task.length() <= 0) {
            Toast.makeText(this, "The text box cannot be empty", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<Object, Object> taskItem = new HashMap<>();
            taskItem.put("task", task);
            taskItem.put("status", 0);

            fh.getDb()
                    .add(taskItem)
                    .addOnSuccessListener(
                            new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddNewTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                                    goHome();
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddNewTaskActivity.this, "Task not added: something went wrong...", Toast.LENGTH_SHORT).show();

                                }
                            }
                    );
        }
    }

    private void goHome() {
        startActivity(
                new Intent(
                        AddNewTaskActivity.this, ProfileActivity.class
                )
        );
        finish();
    }
}