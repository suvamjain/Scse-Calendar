package scse.vit.calendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText user,pass;
    Button loginbut;
    String passw="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        loginbut = findViewById(R.id.loginbut);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference main_database;
        main_database=database.getReference().child("admin");

        main_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passw = dataSnapshot.getValue(String.class);
                Log.d("----PASS-----",passw);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passw!="") {
                    if (user.getText().toString().equals("admin") && pass.getText().toString().equals(passw)) {
                        Intent launchNextActivity;
                        launchNextActivity = new Intent(MainActivity.this, SelectUpload.class);
                        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(launchNextActivity);
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry wrong credentials", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Can't conect to server at the moment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
