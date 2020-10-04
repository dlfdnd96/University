package kr.ac.gachon.bbangbbangclock.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kr.ac.gachon.bbangbbangclock.activity.MainActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}