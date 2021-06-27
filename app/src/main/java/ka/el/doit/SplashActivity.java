package ka.el.doit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (isLoading) {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            isLoading = false;

                            // Go next activity
                            startActivity(
                                    new Intent(
                                            SplashActivity.this,
                                            LoginActivity.class
                                    )
                            );
                            finish();
                        }
                    }
                }
        ).start();
    }
}