package com.example.thoughtbook;

//import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AuthManager authManager = new AuthManager();

        authManager.ensureSignedIn(task ->{
            if(task.isSuccessful()){
            String uid = authManager.getUid();

            //So basiacaly logcar is a android way of cout
            Log.d(TAG, "signed in with uid : " + uid);}
            else{                Log.wtf(TAG, "exception occured "+ task.getException());}

        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}