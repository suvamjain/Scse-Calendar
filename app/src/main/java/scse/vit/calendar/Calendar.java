package scse.vit.calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static scse.vit.calendar.NotificationPublisher.NOTIFICATION_ID;

public class Calendar extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();;
    DatabaseReference main_database;
    SimpleDateFormat sdf,sdf1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private TextView error_message;
    Spinner syear,smonth;
    long futuremillis,timesystem,currentmillis,date,difftime;
    Date dsystem;
    ArrayAdapter<CharSequence> montharray;
    ArrayAdapter<CharSequence> yeararray;
    String year_selected,month_selected,datestring,datetime;
    java.util.Calendar cal;
    Boolean connected;

    protected SharedPreferences prefs,prefs1;
    private static final String myprefs = "details.conf";
    private static final String myprefs2 = "synctime.conf";

    private JSONObject myDataset;
    private SparseArray<String> myDateSet;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        error_message=findViewById(R.id.calendar_msg);
        syear = findViewById(R.id.yearselect);
        smonth = findViewById(R.id.monthselect);
        montharray = ArrayAdapter.createFromResource(this,R.array.months,R.layout.spinner_layout);
        yeararray = ArrayAdapter.createFromResource(this,R.array.years,R.layout.spinner_layout);
        montharray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yeararray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        syear.setAdapter(yeararray);
        smonth.setAdapter(montharray);
        connected = false;
        final ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
        cal = java.util.Calendar.getInstance();
        year_selected = Integer.toString(cal.get(java.util.Calendar.YEAR));
        SimpleDateFormat month = new SimpleDateFormat("MMMM");
        month_selected = month.format(cal.getTime());
        syear.setSelection(getindex(syear,year_selected));
        smonth.setSelection(getindex(smonth,month_selected));
        syear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_selected = (String) parent.getItemAtPosition(position);
                if (myDateSet!=null)
                setEventList();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        smonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month_selected = (String) parent.getItemAtPosition(position);
                if (myDateSet!=null)
                setEventList();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        prefs = getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        prefs1 = getSharedPreferences(myprefs2,MODE_PRIVATE);
        if(prefs.getString("usertype","").equals("faculty"))
        {
            main_database=database.getReference().child("faculty");

            FloatingActionButton fab,fab1;
            fab =  findViewById(R.id.fab);
            fab1 =  findViewById(R.id.fab1);
            fab.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.VISIBLE);
            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.edit().clear().commit();
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(Calendar.this, StartupActivity.class);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchNextActivity);
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(Calendar.this, MainActivity.class);
                    startActivity(launchNextActivity);
                }
            });
        }
        else
        {
            FloatingActionButton fab;
            fab =  findViewById(R.id.fab1);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.edit().clear().commit();
                    Intent launchNextActivity;
                    launchNextActivity = new Intent(Calendar.this, StartupActivity.class);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchNextActivity);
                }
            });
            main_database=database.getReference().child("student");
        }

        mRecyclerView =  findViewById(R.id.event_recycler);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        error_message.setText("Fetching Data...");
        error_message.setTextColor(getResources().getColor(R.color.white));

        get_data();

    }

    private int getindex(Spinner myspin, String selected_value) {
        int index=0;
        for(int i=0;i<myspin.getCount();i++){
            if (myspin.getItemAtPosition(i).toString().equalsIgnoreCase(selected_value)){
                index = i;
                break;
            }
        }
        return index;
    }


    private void get_data()
    {
        main_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myDataset=new JSONObject();
                myDateSet=new SparseArray<>();
                Toast.makeText(Calendar.this,month_selected+" -- "+year_selected,Toast.LENGTH_LONG).show();
                int k=0;
                for (DataSnapshot parent : dataSnapshot.getChildren())
                {
                    String date= parent.getKey();

                    JSONObject desc= new JSONObject();
                    int i=0;
                    for (DataSnapshot childdata : parent.getChildren())
                    {

                        try {
                            desc.put(Integer.toString(i++),childdata.getValue(String.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    JSONArray tmp=new JSONArray();
                    tmp.put(desc);

                    try {
                        myDataset.put(date,tmp);
                        myDateSet.put(k++,date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                if (myDateSet.size()!=0) {
                    //Log.d("---After loop",myDateSet.toString());
                    //Log.d("-------After looppp",myDataset.toString());

                    error_message.setVisibility(View.GONE);
                    java.util.Calendar c = java.util.Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm a");
                    String formattedDate = df.format(c.getTime());
                    if(connected==true) {
                        prefs1.edit().putString("fetchtime",formattedDate).apply();
                        Toast.makeText(Calendar.this, "Last Sync : " + formattedDate + "", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(Calendar.this, "Last Sync : " + prefs1.getString("fetchtime","") + "", Toast.LENGTH_LONG).show();
                    }

                    setEventList();
                }
                else
                {
                    error_message.setVisibility(View.VISIBLE);
                    error_message.setText("Data Unavailable");
                    error_message.setTextColor(getResources().getColor(R.color.white));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("cancel", "Failed to read value.", error.toException());
                Toast.makeText(Calendar.this,"Loading Cancelled By user",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setEventList()
    {

        SparseArray<String> Date_obj=new SparseArray<>();
        int k= 0;
        date=System.currentTimeMillis();
        sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        sdf1 = new SimpleDateFormat("yyyy-mm-dd hh:mm a");
        datestring = sdf.format(date);
        try {
            dsystem = sdf.parse(datestring);
            timesystem = dsystem.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //scheduleNotification(getNotification("New events are awaiting your presence today","Events today !"), 10000,0);
        for(int i=0;i<myDateSet.size();i++)
        {
            try {
                String dt = myDateSet.valueAt(i);
                DateFormat df = new SimpleDateFormat("y-M-d");
                Date d1 = df.parse(dt);
                futuremillis = d1.getTime()+25200000;
                difftime = futuremillis-timesystem;

                if(difftime>=0) {
                    //Log.d("-----Wait-----", "future: " + Long.toString(futuremillis) + ", present: " + Long.toString(timesystem) + ", diff: " + Long.toString(difftime));
                    JSONArray tmp=myDataset.getJSONArray(myDateSet.get(i));
                    JSONObject temp=tmp.getJSONObject(0);

                    StringBuffer content= new StringBuffer();
                    for( int ik = 0; ik < temp.length(); ik++ ) {

                        content.append(temp.get(Integer.toString(ik)).toString());
                        content.append("\n");

                    }
                    scheduleNotification(getNotification(content.toString(),"Events today !"), difftime,i);
                }


                    DateFormat text_month = new SimpleDateFormat("MMMM");
                    DateFormat text_year = new SimpleDateFormat("yyyy");
                    String temp_month=String.valueOf(text_month.format(d1)).trim();
                    String temp_year=String.valueOf(text_year.format(d1)).trim();
                /*Log.d("--MOn ",String.valueOf(text_month.format(d1)));
                Log.d("--Year ",String.valueOf(text_year.format(d1)));*/

                if ((temp_month.equalsIgnoreCase(month_selected)) && (temp_year.equalsIgnoreCase(year_selected))) {
                    Date_obj.put(k++, dt);
                    Log.d("----msggg ","selected ");
                }
            }catch (Exception e)
            {
                Log.e("----Parse Date error ","Calendar");
            }
        }
        mAdapter = new EventAdapter(Calendar.this, myDataset, Date_obj);

        mRecyclerView.setAdapter(mAdapter);
        if (Date_obj.size()==0)
        {
            error_message.setVisibility(View.VISIBLE);
            error_message.setText("No Events");
            error_message.setTextColor(getResources().getColor(R.color.white));
        }
        else {
            error_message.setVisibility(View.GONE);
        }
    }


    private void scheduleNotification(Notification notification, long delay, int num) {
        //Intent intent = new Intent(getActivity(), GalleryActivity.class);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, num);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, num, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Log.d(".......Id:::",Integer.toString(num));
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
    private Notification getNotification(String content,String title) {
        Intent intent = new Intent(this, Calendar.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent, FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentTitle(title);
        builder.setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 });
        builder.setLights(Color.GREEN, 3000, 3000);
        builder.setStyle(new Notification.BigTextStyle().bigText(content));
        builder.setContentText(content);
        builder.setSound(alarmSound);
        builder.setAutoCancel(true);
        builder.setContentIntent(pi);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        return builder.build();
    }


}
