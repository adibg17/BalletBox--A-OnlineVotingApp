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

import java.util.ArrayList;
import java.util.HashMap;


public class AdminActivity extends AppCompatActivity {
    EditText pollName, candidates, searchPollId;
    Button createPoll, goToMain, searchPoll;
    ListView candidateList;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        pollName = findViewById(R.id.pollName);
        candidates = findViewById(R.id.candidates);
        searchPollId = findViewById(R.id.pollIdInput);
        createPoll = findViewById(R.id.createPoll);
        goToMain = findViewById(R.id.goToMain);
        searchPoll = findViewById(R.id.viewVotes);
        candidateList = findViewById(R.id.voteList);

        dbHelper = new DBHelper(this);

        createPoll.setOnClickListener(v -> {
            String name = pollName.getText().toString();
            String candidateListText = candidates.getText().toString();
            long id = dbHelper.createPoll(name, candidateListText);
            if (id != -1) {
                Toast.makeText(this, "Poll created successfully! Poll ID: " + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error creating poll!", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(this, "Admin Activity: Poll creation process executed", Toast.LENGTH_SHORT).show();
        });

        goToMain.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);

            // Indicate navigation action
            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
        });

        searchPoll.setOnClickListener(v -> {
            String pollId = searchPollId.getText().toString();

            if (pollId.isEmpty()) {
                Toast.makeText(this, "Please enter a Poll ID", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor pollCursor = dbHelper.getPoll(pollId);

            if (pollCursor != null && pollCursor.getCount() > 0) {
                pollCursor.moveToFirst();

                // Retrieve poll details
                String name = pollCursor.getString(pollCursor.getColumnIndexOrThrow("name"));
                String candidatesText = pollCursor.getString(pollCursor.getColumnIndexOrThrow("candidates"));

                // Split candidates into a list
                String[] candidatesArray = candidatesText.split(",");

                // Get the vote counts for this poll
                Cursor voteCursor = dbHelper.getVoteCounts(pollId);

                // Store vote counts in a HashMap
                HashMap<String, Integer> voteCountMap = new HashMap<>();
                if (voteCursor != null && voteCursor.getCount() > 0) {
                    while (voteCursor.moveToNext()) {
                        String candidate = voteCursor.getString(voteCursor.getColumnIndexOrThrow("candidate"));
                        int voteCount = voteCursor.getInt(voteCursor.getColumnIndexOrThrow("voteCount"));
                        voteCountMap.put(candidate, voteCount);
                    }
                    voteCursor.close();
                }

                // Build result data ensuring all candidates are included
                ArrayList<String> resultData = new ArrayList<>();
                for (String candidate : candidatesArray) {
                    int voteCount = voteCountMap.getOrDefault(candidate.trim(), 0);
                    resultData.add(candidate.trim() + " - Votes: " + voteCount);
                }

                // Populate ListView with the vote counts
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultData);
                candidateList.setAdapter(adapter);

                // Display poll name in a toast
                Toast.makeText(this, "Poll Name: " + name, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Poll not found!", Toast.LENGTH_SHORT).show();
            }

            if (pollCursor != null) {
                pollCursor.close();
            }
        });

    }
}
