package com.example.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Declaring my variables
    // Buttons
    private Button btnNewMember;
    private Button btnCheckInOut;
    private Button btnAllMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        // Initializing variables
        // Buttons
        btnNewMember = findViewById(R.id.btnNewMember);
        btnCheckInOut = findViewById(R.id.btnCheckInOut);
        btnAllMembers = findViewById(R.id.btnAllMembers);


        // Creating an intent to launch activities
        Intent newMemberActivity = new Intent(this, NewMemberActivity.class);
        Intent allMembersActivity = new Intent(this, AllMembersActivity.class);
        Intent checkInOutActivity = new Intent(this, CheckInOut.class);

        // NEW MEMBER
        btnNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(newMemberActivity);
            }
        });

        // ALL MEMBERS
        btnAllMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(allMembersActivity);
            }
        });

        // CHECK IN/OUT
        btnCheckInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calling the method to scan the barcode
                scanBarcode();
            }
        });
    }

    // METHODS
    private void scanBarcode() {
        /*The IntentIntegrator class is part of the ZXing ("Zebra Crossing") library, which is
        a popular open-source library for working with QR codes and barcodes in Android apps.
         */
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        // InitiateScan() is a method of the ZXing library
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // The barcode is converted as a String
                String barcodeNumber = result.getContents();
                Toast.makeText(this, "Scanned: " + barcodeNumber, Toast.LENGTH_LONG).show();

                // Gson is a Java library that can be used to convert Java Objects into their JSON representation.
                // JSON = JavaScript Object Notation
                // SharedPreferences is an Android class that provides a way to store small amounts of data in key-value pairs.
                SharedPreferences sharedPreferences = getSharedPreferences("members", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("membersList", null);
                /*This line creates a TypeToken object that represents the type of the data being retrieved
                from the SharedPreferences. In this case, it represents an ArrayList of Member objects.
                 */
                Type type = new TypeToken<ArrayList<Member>>() {
                }.getType();
                ArrayList<Member> membersList = gson.fromJson(json, type);

                if (membersList != null) {
                    /* Initialize a variable member to null. I will use this variable to stock
                    the member data corresponding to the barcode
                     */
                    Member member = null;
                    for (Member m : membersList) {
                        if (m.getCode().equals(barcodeNumber)) {
                            member = m;
                            break;
                        }
                    }
                    // If the barcode corresponds to a user (member)
                    if (member != null) {
                        Intent checkInOutActivity = new Intent(this, CheckInOut.class);
                        /* "putExtra" is a method of the Intent class. It is used to add extra data to an intent,
                        such as passing data from one activity to another, or starting a service with additional data.
                        In that case, if the barcode exist, the app opens the "checkInOutActivity" with the data
                        of the member
                         */
                        checkInOutActivity.putExtra("member", member);
                        startActivity(checkInOutActivity);
                    // Member not found
                    } else {
                        Toast.makeText(this, "Member not found", Toast.LENGTH_SHORT).show();
                    }
                // Memberlist not found
                } else {
                    Toast.makeText(this,  "No memberlist found", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}