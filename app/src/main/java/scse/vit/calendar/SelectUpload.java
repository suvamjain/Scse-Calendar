package scse.vit.calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectUpload extends AppCompatActivity {

    Button student,faculty;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private static final String myprefs = "details.conf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_upload);
        student = findViewById(R.id.studupload);
        faculty = findViewById(R.id.facupload);
        prefs = getSharedPreferences(myprefs,MODE_PRIVATE);
        edit = prefs.edit();
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString("upload","studentfile");
                edit.apply();
                Intent intent = new Intent(SelectUpload.this,Admin.class);
                startActivity(intent);
            }
        });
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString("upload","facultyfile");
                edit.apply();
                Intent intent = new Intent(SelectUpload.this,Admin.class);
                startActivity(intent);
            }
        });
    }
}
