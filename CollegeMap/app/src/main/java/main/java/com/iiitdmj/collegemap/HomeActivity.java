package main.java.com.iiitdmj.collegemap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button SP;
    private Button DC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SP = (Button) findViewById(R.id.SP);

        DC = (Button) findViewById(R.id.DC);

    }


    @Override
    protected void onStart() {

        SP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(i);

            }
        });

        super.onStart();

        DC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this, Direction.class);
                startActivity(i);

            }
        });

        super.onStart();
    }
}

