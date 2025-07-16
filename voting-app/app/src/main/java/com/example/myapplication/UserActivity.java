package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    EditText pollId;
    Button searchPoll, castVote, logout; // Reference the new logout button
    ListView candidateList;
    DBHelper dbHelper;
    int userId;
    String selectedCandidate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        pollId = findViewById(R.id.pollId);
        searchPoll = findViewById(R.id.searchPoll);
        castVote = findViewById(R.id.castVote);
        logout = findViewById(R.id.logout); // Initialize logout button
        candidateList = findViewById(R.id.candidateList);

        dbHelper = new DBHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        searchPoll.setOnClickListener(v -> {
            String id = pollId.getText().toString();
            Cursor res = dbHelper.getPoll(id);
            if (res != null && res.getCount() > 0) {
                res.moveToFirst();
                String candidates = res.getString(2);
                String[] candidateArray = candidates.split(",");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, candidateArray);
                candidateList.setAdapter(adapter);
                candidateList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                candidateList.setOnItemClickListener((parent, view, position, id1) -> selectedCandidate = candidateArray[position]);
            } else {
                Toast.makeText(this, "Poll not found!", Toast.LENGTH_SHORT).show();
            }
        });

        castVote.setOnClickListener(v -> {
            if (selectedCandidate != null) {
                String id = pollId.getText().toString();
                if (dbHelper.castVote(Integer.parseInt(id), userId, selectedCandidate)) {
                    Toast.makeText(this, "Vote cast successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You have already casted vote!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a candidate!", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(v -> {
            // Log out the user and navigate to the main or login activity
            Intent intent = new Intent(UserActivity.this, MainActivity.class); // Redirect to main/login activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
            startActivity(intent);
            finish(); // End current activity
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}
