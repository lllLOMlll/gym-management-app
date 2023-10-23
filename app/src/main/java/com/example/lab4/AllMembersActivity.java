package com.example.lab4;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class AllMembersActivity extends AppCompatActivity {
    private ArrayList<Member> membersList = new ArrayList<>();
    private ListView listViewMembers;

    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_all_members);

        listViewMembers = findViewById(R.id.listViewMembers);
        btnHome = findViewById(R.id.btnHome);

        Intent mainActivity = new Intent(this, MainActivity.class);

        // HOME
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mainActivity);
            }
        });

        // Get the membersList from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("members", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("membersList", null);
        Type type = new TypeToken<ArrayList<Member>>() {
        }.getType();

        if (json != null) {
            membersList = gson.fromJson(json, type);
        } else {
            membersList = new ArrayList<>();
        }

        // Create a MemberAdapter to display the data in the ListView
        MemberAdapter adapter = new MemberAdapter(this, membersList);
        listViewMembers.setAdapter(adapter);

        // DELETE A MEMBER
        listViewMembers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the member object at the clicked position
                final Member member = membersList.get(position);

                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AllMembersActivity.this);
                builder.setMessage("Are you sure you want to delete " + member.getFirstName() + " " + member.getLastName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the member from the membersList
                        membersList.remove(member);

                        // Save the updated membersList to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("members", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(membersList);
                        editor.putString("membersList", json);
                        editor.apply();

                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

    }
}
