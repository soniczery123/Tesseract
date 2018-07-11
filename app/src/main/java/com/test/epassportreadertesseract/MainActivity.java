package com.test.epassportreadertesseract;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.test.epassportreadertesseract.Model.InsertResponseModel;
import com.test.epassportreadertesseract.SaveToDB.ApiInterface;
import com.test.epassportreadertesseract.SaveToDB.ConnectServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText ID, Firstname, Surname, Sex, DOB, PassType, ExpDate, Nation, CitizenNo;
    TextView ID_Con, Firstname_Con, Surname_Con, Sex_Con, DOB_Con, PassType_Con, ExpDate_Con, Nation_Con, CitizenNo_Con;
    Button buttonConfirm;
    final int RequestPermissionCode = 1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck1 == PackageManager.PERMISSION_DENIED || permissionCheck2 == PackageManager.PERMISSION_DENIED
                || permissionCheck3 == PackageManager.PERMISSION_DENIED)
            RequestRuntimePermission();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#ff3eaff9"), PorterDuff.Mode.SRC_IN);
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);

        ID = (EditText) findViewById(R.id.textViewID);
        CitizenNo = (EditText) findViewById(R.id.textViewCitizen);
        Firstname = (EditText) findViewById(R.id.textViewFirstName);
        Surname = (EditText) findViewById(R.id.textViewSurname);
        Sex = (EditText) findViewById(R.id.textViewSex);
        DOB = (EditText) findViewById(R.id.textViewDOB);
        PassType = (EditText) findViewById(R.id.textViewPassportType);
        ExpDate = (EditText) findViewById(R.id.textViewExpDate);
        Nation = (EditText) findViewById(R.id.textViewNation);
        Intent intent = getIntent();
        String[] result = intent.getStringArrayExtra("result");
        if (result != null && result.length!=0) setText(result);
        final Activity activity = this;


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBlankEditText()) {
                    progressBar.setVisibility(View.VISIBLE);
                    showDialog();
                }
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_confirm_data, null);
        builder.setView(view);

        ID_Con = (TextView) view.findViewById(R.id.textViewID_Con);
        CitizenNo_Con = (TextView) view.findViewById(R.id.textViewCitizen_Con);
        Firstname_Con = (TextView) view.findViewById(R.id.textViewFirstName_Con);
        Surname_Con = (TextView) view.findViewById(R.id.textViewSurname_Con);
        Sex_Con = (TextView) view.findViewById(R.id.textViewSex_Con);
        DOB_Con = (TextView) view.findViewById(R.id.textViewDOB_Con);
        PassType_Con = (TextView) view.findViewById(R.id.textViewPassportType_Con);
        ExpDate_Con = (TextView) view.findViewById(R.id.textViewExpDate_Con);
        Nation_Con = (TextView) view.findViewById(R.id.textViewNation_Con);

        ID_Con.setText(ID.getText().toString().toUpperCase());
        Firstname_Con.setText(Firstname.getText().toString().toUpperCase());
        Surname_Con.setText((Surname.getText().toString()).toUpperCase());
        Sex_Con.setText(Sex.getText().toString().toUpperCase());
        CitizenNo_Con.setText(CitizenNo.getText().toString().toUpperCase());
        DOB_Con.setText(DOB.getText().toString().toUpperCase());
        PassType_Con.setText(PassType.getText().toString().toUpperCase());
        ExpDate_Con.setText(ExpDate.getText().toString().toUpperCase());
        Nation_Con.setText(Nation.getText().toString().toUpperCase());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                insertData(PassType_Con.getText().toString(),
                        Surname_Con.getText().toString(),
                        Firstname_Con.getText().toString(),
                        ID_Con.getText().toString(),
                        Nation_Con.getText().toString(),
                        CitizenNo_Con.getText().toString(),
                        DOB_Con.getText().toString(),
                        Sex_Con.getText().toString(),
                        ExpDate_Con.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        progressBar.setVisibility(View.GONE);
        builder.show();
    }



    private void setText(String[] result) {
        ID.setText(result[0]);
        CitizenNo.setText(result[1]);
        Firstname.setText(result[2]);
        Surname.setText(result[3]);
        Sex.setText(result[4]);
        DOB.setText(result[5]);
        PassType.setText(result[6]);
        ExpDate.setText(result[7]);
        Nation.setText(result[8]);
    }


    private boolean checkBlankEditText() {
        boolean check = true;
        if (ID.getText().toString().equals("")) {
            ID.setError("Please enter your Passport No.");
            check = false;
        }
        if (Firstname.getText().toString().equals("")) {
            Firstname.setError("Please enter your Firstname");
            check = false;
        }
        if (Surname.getText().toString().equals("")) {
            Surname.setError("Please enter your Surname");
            check = false;
        }
        if (Sex.getText().toString().equals("")) {
            Sex.setError("Please enter your Sex");
            check = false;
        }
        if (CitizenNo.getText().toString().equals("")) {
            CitizenNo.setError("Please enter your Citizen No.");
            check = false;
        }
        if (DOB.getText().toString().equals("")) {
            DOB.setError("Please enter your Date of Birth");
            check = false;
        }
        if (PassType.getText().toString().equals("")) {
            PassType.setError("Please enter your Passport Type");
            check = false;
        }
        if (ExpDate.getText().toString().equals("")) {
            ExpDate.setError("Please enter your Date of Expiration");
            check = false;
        }
        if (Nation.getText().toString().equals("")) {
            Nation.setError("Please enter your Nationality");
            check = false;
        }
        return check;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        if (item.getItemId() == R.id.btn_camera) {
            //  clearText();
            intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.btn_nfc) {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC Not Availiable!", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(MainActivity.this, NfcAcitivity.class);
                startActivity(intent);
            }
        }

        return true;
    }

    private void RequestRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            Toast.makeText(this, "CAMERA permission allows us to access CAMERA app", Toast.LENGTH_SHORT).show();
        else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Canceled", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private void insertData(String PassType, String Surname, String Firstname, String ID, String Nation, String CitizenNo, String DOB, String Sex, String ExpDate) {
        ApiInterface apiService = ConnectServer.getClient().create(ApiInterface.class);
        Call<InsertResponseModel> call = apiService.insertData(PassType, Surname, Firstname, ID, Nation, CitizenNo, DOB, Sex, ExpDate);
        call.enqueue(new Callback<InsertResponseModel>() {
                         @Override
                         public void onResponse(Call<InsertResponseModel> call, Response<InsertResponseModel> response) {
                             InsertResponseModel InsertResponse = response.body();
                             progressBar.setVisibility(View.GONE);
                             Toast.makeText(MainActivity.this, InsertResponse.getMessage(), Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void onFailure(Call<InsertResponseModel> call, Throwable t) {
                             progressBar.setVisibility(View.GONE);
                             Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                             Log.e("ERROR ", t.getMessage());
                         }
                     }

        );
    }
}
