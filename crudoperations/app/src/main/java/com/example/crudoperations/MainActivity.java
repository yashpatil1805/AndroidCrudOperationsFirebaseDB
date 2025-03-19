package com.example.crudoperations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FirestoreCRUD";
    private FirebaseFirestore db;
    private EditText etName, etEmail, etAge;
    private Button btnAdd, btnUpdate, btnDelete;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind UI Elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // Fetch users
        fetchUsers();

        // Add User
        btnAdd.setOnClickListener(view -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            int age = Integer.parseInt(etAge.getText().toString());

            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("age", age);

            db.collection("users").add(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(MainActivity.this, "User added!", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding document", e));
        });

        // Update User
        btnUpdate.setOnClickListener(view -> {
            String name = etName.getText().toString();
            db.collection("users").whereEqualTo("name", name).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("email", etEmail.getText().toString(), "age", Integer.parseInt(etAge.getText().toString()));
                        }
                        fetchUsers();
                    });
        });

        // Delete User
        btnDelete.setOnClickListener(view -> {
            String name = etName.getText().toString();
            db.collection("users").whereEqualTo("name", name).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        fetchUsers();
                    });
        });
    }

    private void fetchUsers() {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            userList.clear(); // Clear existing data

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                User user = document.toObject(User.class); // Convert document to User
                userList.add(user);
            }

            userAdapter.notifyDataSetChanged(); // Notify RecyclerView of new data
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching users", e));
    }

}