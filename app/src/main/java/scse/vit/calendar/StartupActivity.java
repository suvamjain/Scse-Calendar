package scse.vit.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;

import pl.droidsonroids.gif.GifImageView;

public class StartupActivity extends AppCompatActivity {
    Button student,faculty,updatebutton;
    LinearLayout l1;
    TextView msg;
    SharedPreferences prefs;
    GifImageView gifImageView;
    SharedPreferences.Editor edit;
    String currentVersion,onlineversion;
    final Handler handler = new Handler();
    private static final String myprefs = "details.conf";
    FirebaseDatabase database;
    DatabaseReference main_database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        prefs = getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        edit = prefs.edit();
        database = FirebaseDatabase.getInstance();
        main_database = database.getReference().child("version");

        student=findViewById(R.id.studentbutton);
        gifImageView = findViewById(R.id.load);
        faculty=findViewById(R.id.facultybutton);
        updatebutton = findViewById(R.id.updatebut);
        l1 = findViewById(R.id.buttonslayer);
        msg = findViewById(R.id.message);
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=scse.vit.calendar"));
                startActivity(intent);
            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString("usertype","student");
                edit.putString("username","");
                edit.putString("password","");
                edit.apply();
                Intent launchNextActivity;
                launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(launchNextActivity);
            }
        });
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString("usertype","");
                edit.putString("username","");
                edit.putString("password","");
                edit.apply();
                Intent launchNextActivity;
                launchNextActivity = new Intent(StartupActivity.this, FloginActivity.class);
                startActivity(launchNextActivity);
            }
        });
        try {
            currentVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                main_database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        onlineversion = dataSnapshot.getValue(String.class);
                        gifImageView.setVisibility(View.GONE);
                        if (onlineversion != null && !onlineversion.isEmpty()) {

                            if (currentVersion.compareToIgnoreCase(onlineversion)<0) {
                                msg.setText("Please update app to continue...");
                                updatebutton.setVisibility(View.VISIBLE);
                            }
                            else{
                                if(prefs.getString("usertype","").equals("student")){
                                    Intent launchNextActivity;
                                    launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(launchNextActivity);
                                }
                                else if(prefs.getString("usertype","").equals("faculty")&&prefs.getString("password","").equals("")){
                                    Intent launchNextActivity;
                                    launchNextActivity = new Intent(StartupActivity.this, FloginActivity.class);
                                    startActivity(launchNextActivity);
                                }
                                else if(prefs.getString("password","").equals("Vitscse01")){
                                    Intent launchNextActivity;
                                    launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(launchNextActivity);
                                }
                                else if(prefs.getString("usertype","").equals("")){
                                    gifImageView.setVisibility(View.GONE);
                                    msg.setText("Please select to continue...");
                                    l1.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        else if(onlineversion==null){
                            if(prefs.getString("usertype","").equals("student")){
                                Intent launchNextActivity;
                                launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(launchNextActivity);
                            }
                            else if(prefs.getString("usertype","").equals("faculty")&&prefs.getString("password","").equals("")){
                                Intent launchNextActivity;
                                launchNextActivity = new Intent(StartupActivity.this, FloginActivity.class);
                                startActivity(launchNextActivity);
                            }
                            else if(prefs.getString("password","").equals("Vitscse01")){
                                Intent launchNextActivity;
                                launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(launchNextActivity);
                            }
                            else if(prefs.getString("usertype","").equals("")){
                                gifImageView.setVisibility(View.GONE);
                                msg.setText("Please select to continue...");
                                l1.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }, 3000);

        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prefs.getString("usertype","").equals("student")){
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchNextActivity);
                }
                else if(prefs.getString("usertype","").equals("faculty")&&prefs.getString("password","").equals("")){
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(StartupActivity.this, FloginActivity.class);
                    startActivity(launchNextActivity);
                }
                else if(prefs.getString("password","").equals("Vitscse01")){
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(StartupActivity.this, Calendar.class);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchNextActivity);
                }
                else if(prefs.getString("usertype","").equals("")){
                    gifImageView.setVisibility(View.GONE);
                    msg.setText("Please select to continue...");
                    l1.setVisibility(View.VISIBLE);
                }
            }
        }, 2000);*/
    }



}
