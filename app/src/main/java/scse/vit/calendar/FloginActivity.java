package scse.vit.calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FloginActivity extends AppCompatActivity {
    EditText key;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    Button loginbut;
    private static final String myprefs = "details.conf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flogin);
        loginbut = findViewById(R.id.loginbut);
        key = findViewById(R.id.passfaculty);
        prefs = getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(key.getText().toString().equals("Vitscse01")){
                    edit = prefs.edit();
                    edit.putString("usertype","faculty");
                    edit.putString("username","scse");
                    edit.putString("password","Vitscse01");
                    edit.apply();
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(FloginActivity.this, Calendar.class);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchNextActivity);
                }
                else{
                    Toast.makeText(FloginActivity.this,"The key is incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
