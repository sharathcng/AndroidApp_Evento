package com.example.evento.common.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evento.R;

import java.util.Calendar;

public class SignUpScreen2 extends AppCompatActivity {
    ImageView backBtn;
    Button next, login;
    TextView titleText;

    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen2);
        //hooks
        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);

        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);


    }

    public void call3rdSigupScreen(View view) {

        if (!validateGender() | !validateAge()) {
            return;
        }

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
        String _gender = selectedGender.getText().toString();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String _date = day+"/"+month+"/"+year;


        String fullNames = getIntent().getStringExtra("fullName");

        String emails =getIntent().getStringExtra("email");
        String userNames = getIntent().getStringExtra("userName");
        String passwords = getIntent().getStringExtra("password");

        Intent intent = new Intent(getApplicationContext(),SignUpScreen3.class);
        intent.putExtra("fullName",fullNames);
        intent.putExtra("userName",userNames);
        intent.putExtra("email",emails);
        intent.putExtra("password",passwords);
        intent.putExtra("gender",_gender);
        intent.putExtra("date",_date);


        //add transition
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View,String>(backBtn,"transition_back_arrow_btn");
        pairs[1] = new Pair<View,String>(next,"transition_next_btn");
        pairs[2] = new Pair<View,String>(login,"transition_login_btn");
        pairs[3] = new Pair<View,String>(titleText,"transition_title_text");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpScreen2.this);
            startActivity(intent,options.toBundle());
        }
        else {
            startActivity(intent);
        }

    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 5) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

}