package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "VotingApp.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE Polls (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, candidates TEXT)");
        db.execSQL("CREATE TABLE Votes (pollId INTEGER, userId INTEGER, candidate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Polls");
        db.execSQL("DROP TABLE IF EXISTS Votes");
        onCreate(db);
    }

    public boolean insertUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("Users", null, values);
        return result != -1;
    }

    public Cursor checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Users WHERE username = ? AND password = ?", new String[]{username, password});
    }

    public long createPoll(String name, String candidates) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("candidates", candidates);
        return  db.insert("Polls", null, values);
    }

    public Cursor getPoll(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Polls WHERE id = ?", new String[]{id});
    }

    public boolean castVote(int pollId, int userId, String candidate) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the user has already voted in the poll
        String query = "SELECT * FROM Votes WHERE pollId = ? AND userId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(pollId), String.valueOf(userId)});

        if (cursor.getCount() > 0) {
            // User has already voted
            cursor.close();
            return false;
        }

        cursor.close();

        // If no existing vote is found, allow the user to cast a vote
        ContentValues values = new ContentValues();
        values.put("pollId", pollId);
        values.put("userId", userId);
        values.put("candidate", candidate);
        long result = db.insert("Votes", null, values);

        return result != -1;
    }

    public Cursor getVoteCounts(String pollId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT candidate, COUNT(*) as voteCount FROM Votes WHERE pollId = ? GROUP BY candidate";
        return db.rawQuery(query, new String[]{pollId});
    }


}
