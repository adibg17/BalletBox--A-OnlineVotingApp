package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button adminLogin, userLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        adminLogin = findViewById(R.id.adminLogin);
        userLogin = findViewById(R.id.userLogin);

        dbHelper = new DBHelper(this);

        // Add default admin and user accounts
        dbHelper.insertUser("admin", "admin", "admin");
        dbHelper.insertUser("user", "user", "user");

        adminLogin.setOnClickListener(v -> login("admin"));
        userLogin.setOnClickListener(v -> login("user"));
    }

    private void login(String role) {
        String user = username.getText().toString();
        String pass = password.getText().toString();

        Cursor res = dbHelper.checkUser(user, pass);

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();
            String userRole = res.getString(3);
            if (userRole.equals(role)) {
                if (role.equals("admin")) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    Intent intent = new Intent(this, UserActivity.class);
                    intent.putExtra("userId", res.getInt(0));
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Invalid role!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
        }
    }
}
