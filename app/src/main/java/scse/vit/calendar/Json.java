package scse.vit.calendar;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Json extends AppCompatActivity {

    TextView data;

    protected SharedPreferences prefs;
    private static final String myprefs = "details.conf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);



        String urlString= "https://valiantcity.000webhostapp.com/calendar/getData.php";

        prefs = getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        if(prefs.getString("usertype","").equals("student"))
        {
            urlString = "https://valiantcity.000webhostapp.com/calendar/getData.php";
        }
        else
        {
            urlString = "https://valiantcity.000webhostapp.com/calendar/faculty/getData.php";
        }

        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            Json.this.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            Json.this.startActivity(intent);
        }
    }

    private void doHttpUrlConnectionAction(String desiredUrl) throws Exception
    {


        @SuppressLint("StaticFieldLeak") AsyncTask<String,Void,String> fetch= new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                data.setText("fetching");
            }

            @Override
            protected String doInBackground(String... strings) {
                URL url = null;
                BufferedReader reader = null;
                StringBuilder stringBuilder;

                try
                {

                    url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(15*1000);
                    connection.connect();

                    // read the output from the server
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuilder = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        stringBuilder.append(line).append("\n");
                    }
                    return stringBuilder.toString();
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
                finally
                {

                    if (reader != null)
                    {
                        try
                        {
                            reader.close();
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }

                }
                return "cool";
            }

            @Override
            protected void onPostExecute(String s) {

                    data.setText("hi");
            }
        };

        fetch.execute(desiredUrl);

    }
}

