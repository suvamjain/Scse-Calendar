package scse.vit.calendar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;


public class Admin extends AppCompatActivity {

    Button SelectButton, UploadButton;
    TextView status;
    String PdfNameEditText ;

    protected SharedPreferences prefs;
    private static final String myprefs = "details.conf";

    Uri uri;

    public static String PDF_UPLOAD_HTTP_URL;

    public int PDF_REQ_CODE = 1;

    String PdfNameHolder, PdfPathHolder, PdfID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        prefs = getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        if(prefs.getString("upload","").equals("studentfile"))
        {
            PDF_UPLOAD_HTTP_URL = "https://valiantcity.000webhostapp.com/calendar/upload.php";
        }
        else
        {
            PDF_UPLOAD_HTTP_URL = "https://valiantcity.000webhostapp.com/calendar/faculty/upload.php";
        }

        AllowRunTimePermission();
        status= findViewById(R.id.msg);
        SelectButton =  findViewById(R.id.button);
        UploadButton =  findViewById(R.id.button2);
        PdfNameEditText ="scse";

        SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* PDF selection code start from here .
                String[] mimeTypes =
                        {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip"};*/
                Intent intent = new Intent();

                intent.setType("application/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);

            }
        });

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PdfUploadFunction();



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            SelectButton.setText("File is Selected");
        }
    }

    public void PdfUploadFunction() {

        PdfNameHolder = PdfNameEditText.trim();

        PdfPathHolder = FilePath.getPath(this, uri);

        if (PdfPathHolder == null) {

            Toast.makeText(this, "Please move your file to internal storage & try again.", Toast.LENGTH_LONG).show();

        } else {

            try {

                PdfID = UUID.randomUUID().toString();
                String upload=
                        new MultipartUploadRequest(this, PdfID, PDF_UPLOAD_HTTP_URL)
                                .addFileToUpload(PdfPathHolder, "pdf")
                                .addParameter("name", PdfNameHolder)
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(5)
                                .setDelegate(new UploadStatusDelegate() {
                                    @Override
                                    public void onProgress(UploadInfo uploadInfo) {
                                        status.setText("Uploading...");
                                        status.setTextColor(getResources().getColor(R.color.black));
                                    }

                                    @Override
                                    public void onError(UploadInfo uploadInfo, Exception exception) {
                                        status.setText("Failed, Please try again");
                                        status.setTextColor(getResources().getColor(R.color.colorAccent));
                                    }

                                    @Override
                                    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                        status.setText("Completed");
                                        status.setTextColor(getResources().getColor(R.color.green));

                                        Intent myIntent = new Intent(Admin.this, Json.class);
                                        startActivity(myIntent);

                                    }

                                    @Override
                                    public void onCancelled(UploadInfo uploadInfo) {
                                        status.setText("Cancelled...");
                                        status.setTextColor(getResources().getColor(R.color.colorAccent));
                                    }
                                })
                                .startUpload();

            } catch (Exception exception) {

                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void AllowRunTimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(Admin.this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {

            Toast.makeText(Admin.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(Admin.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(Admin.this,"Permission Granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(Admin.this,"Permission Canceled", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }



}