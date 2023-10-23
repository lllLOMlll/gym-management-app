package com.example.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CheckInOut extends AppCompatActivity {

    private TextView tvFirstNameCheckIn_Out;
    private TextView tvLastNameCheckIn_Out;
    private TextView tvDOBCheckIn_Out;
    private TextView tvAgeCheckIn_Out;
    private TextView tvCodeCheckIn_Out;
    private TextView tvVisitsCheckIn_Out;
    private Button btnCheckInOut_CheckInOut;

    private ImageView ivPictureCheckInOut;


    private Member member;
    private boolean checkedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        // Initialize TextViews
        tvFirstNameCheckIn_Out = findViewById(R.id.tvFirstNameCheckIn_Out);
        tvLastNameCheckIn_Out = findViewById(R.id.tvLastNameCheckIn_Out);
        tvDOBCheckIn_Out = findViewById(R.id.tvDOBCheckIn_Out);
        tvAgeCheckIn_Out = findViewById(R.id.tvAgeCheckIn_Out);
        tvCodeCheckIn_Out = findViewById(R.id.tvCodeCheckIn_Out);
        tvVisitsCheckIn_Out = findViewById(R.id.tvVisitsCheckIn_Out);

        // Initialize the button
        btnCheckInOut_CheckInOut = findViewById(R.id.btnCheckInOut_CheckInOut);

        // Initialize picture
        ivPictureCheckInOut = findViewById(R.id.ivPictureCheckInOut);


        // Get the Member object from the Intent
        Intent intent = getIntent();
        member = (Member) intent.getSerializableExtra("member");

        if (member != null) {
            // Display the data of the user
            tvFirstNameCheckIn_Out.setText(member.getFirstName());
            tvLastNameCheckIn_Out.setText(member.getLastName());
            tvDOBCheckIn_Out.setText(member.getDateOfBirth());
            tvAgeCheckIn_Out.setText(String.valueOf(member.getAge()));
            tvCodeCheckIn_Out.setText(member.getCode());
            tvVisitsCheckIn_Out.setText(String.valueOf(member.getVisits()));

            byte[] imageUrl = member.getAvatar(); // Replace with the URL of the user's avatar
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.blank_avatar) // Replace with the placeholder image
                    .into(ivPictureCheckInOut);
            // Set the click listener for the Check In/Out button
            checkedIn = member.isCheckInStatus();
            updateCheckInOutButton();
            btnCheckInOut_CheckInOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkedIn = !checkedIn;
                    if (checkedIn) {
                        // Increment the number of visits
                        member.setVisits(member.getVisits() + 1);
                        tvVisitsCheckIn_Out.setText(String.valueOf(member.getVisits()));
                    }
                    member.setCheckInStatus(checkedIn);
                    updateCheckInOutButton();
                    saveMemberData();

                    // Start the MainActivity
                    Intent intent = new Intent(CheckInOut.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    private void updateCheckInOutButton() {
        if (checkedIn) {
            btnCheckInOut_CheckInOut.setText("Check Out");
        } else {
            btnCheckInOut_CheckInOut.setText("Check In");
        }
    }

    private void saveMemberData() {
        SharedPreferences sharedPreferences = getSharedPreferences("members", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Get the list of members from shared preferences
        String json = sharedPreferences.getString("membersList", null);
        Type type = new TypeToken<ArrayList<Member>>() {
        }.getType();
        ArrayList<Member> membersList = gson.fromJson(json, type);
        if (membersList == null) {
            membersList = new ArrayList<>();
        }

        // Find the index of the current member in the list
        int index = -1;
        for (int i = 0; i < membersList.size(); i++) {
            Member m = membersList.get(i);
            if (m.getCode().equals(member.getCode())) {
                index = i;
                break;
            }
        }

        // Update the visits for the current member
        if (index >= 0) {
            Member updatedMember = membersList.get(index);
            updatedMember.setVisits(member.getVisits());
            updatedMember.setCheckInStatus(member.isCheckInStatus());
            membersList.set(index, updatedMember);
        } else {
            // Add the current member to the list if not found
            membersList.add(member);
        }

        // Save the updated list to shared preferences
        String updatedJson = gson.toJson(membersList);
        editor.putString("membersList", updatedJson);
        editor.apply();
    }
}

