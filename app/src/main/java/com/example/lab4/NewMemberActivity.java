package com.example.lab4;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class NewMemberActivity extends AppCompatActivity {
    // Declaring my variables
    // Edit text
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etDateOfBirth;
    private EditText etAge;
    private EditText etAddress;
    private EditText etCity;
    private EditText etProvince;
    private EditText etBarcode;

    // Buttons
    private Button btnTakeCode;
    private Button btnRegister;
    private Button btnTakePicture;

    // Picture
    private ImageView ivPicture;
    private byte[] avatar;

    private ArrayList<Member> membersList;
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_new_member);

        // Initializing variables
        // Edit Text
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAge = findViewById(R.id.etAge);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etProvince = findViewById(R.id.etProvince);
        etBarcode = findViewById(R.id.etBarcode);
        // Add TextWatcher to format date of birth
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        // Date of Birth formatting
        etDateOfBirth.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String yyyymmdd = "YYYYMMDD";
            private Calendar cal = Calendar.getInstance();
            /* The TextWatcher interface has three methods: beforeTextChanged(), onTextChanged(), and afterTextChanged().
            In this code block, the TextWatcher is created as an anonymous inner class, and only
            the afterTextChanged() method is implemented.*/
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* This line of code is a condition that checks whether the current text in the EditText field
                (s.toString()) is different from the last saved value (current).*/
                // If the editable is not equal to current
                if (!s.toString().equals(current)) {
                    // The pattern [^\d.] matches any character that is not a digit (\d) or a period (.).
                    // String clean = new value of current
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    // String cleanC =  last saved value of current
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    // Increment var sel for every second character in clean starting from the second character.
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + yyyymmdd.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int year = Integer.parseInt(clean.substring(0, 4));
                        int month = Integer.parseInt(clean.substring(4, 6));
                        int day = Integer.parseInt(clean.substring(6, 8));

                        if (month > 12) month = 12;
                        cal.set(Calendar.MONTH, month - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the validation of month and day
                        // then set month, since month is valid it will set day properly
                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", year, month, day);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 4),
                            clean.substring(4, 6),
                            clean.substring(6, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    etDateOfBirth.setText(current);
                    etDateOfBirth.setSelection(sel < current.length() ? sel : current.length());


                }
            }
        });

        // Buttons
        btnTakeCode = findViewById(R.id.btnTakeCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        // Picture
        ivPicture = findViewById(R.id.ivPicture);



        // REGISTER
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String dateOfBirth = etDateOfBirth.getText().toString();

                String ageString = etAge.getText().toString();
                if (ageString.isEmpty()) {
                    // Show an error message and return
                    Toast.makeText(NewMemberActivity.this, "Please fill in the age field.", Toast.LENGTH_LONG).show();
                    return;
                }
                int age = Integer.parseInt(ageString);
                String address = etAddress.getText().toString();
                String city = etCity.getText().toString();
                String province = etProvince.getText().toString();
                String barcode = etBarcode.getText().toString();

                // Check if any fields are empty
                if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || etAge.getText().toString().isEmpty() || address.isEmpty() || city.isEmpty() || province.isEmpty() || barcode.isEmpty() || avatar == null) {
                    Toast.makeText(NewMemberActivity.this, "Please fill in all fields and select an avatar.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Saving the data of the new user
                Member newMember = new Member(firstName, lastName, dateOfBirth, age, address, city, province, barcode, avatar, 0, false);

                // Get the existing members from SharedPreferences
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

                // Check if the barcode already exists in the list of members
                boolean barcodeExists = false;
                for (Member member : membersList) {
                    if (member.getCode().equals(barcode)) {
                        barcodeExists = true;
                        break;
                    }
                }

                if (barcodeExists) {
                    // Show an error message and do not add the new member
                    Toast.makeText(NewMemberActivity.this, "Barcode already exists, please enter a unique barcode.", Toast.LENGTH_LONG).show();
                } else {
                    // Add the new member and save the updated list back to SharedPreferences
                    membersList.add(newMember);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    json = gson.toJson(membersList);
                    editor.putString("membersList", json);
                    editor.apply();

                    // Reset all the fields
                    etFirstName.setText("");
                    etLastName.setText("");
                    etDateOfBirth.setText("");
                    etAge.setText("");
                    etAddress.setText("");
                    etCity.setText("");
                    etProvince.setText("");
                    etBarcode.setText("");
                    // Reset the avatar
                    ivPicture.setImageResource(0);
                    avatar = null;

                    // Pass the Member object to the AllMembersActivity
                    Intent intent = new Intent(view.getContext(), AllMembersActivity.class);
                    intent.putExtra("newMember", newMember);
                    startActivity(intent);
                }
            }
        });


        // BARCODE
        btnTakeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calling the method to scan the barcode
                scanBarcode();
            }
        });

        // PICTURE
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
    }

    // METHODS
    private void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.initiateScan();
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, "Permission denied. Please enable Camera and Storage permissions to proceed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String barcodeNumber = result.getContents();
                Toast.makeText(this, "Scanned: " + barcodeNumber, Toast.LENGTH_LONG).show();
                etBarcode.setText(barcodeNumber);
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPicture.setImageBitmap(imageBitmap);
            avatar = bitmapToByteArray(imageBitmap);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}