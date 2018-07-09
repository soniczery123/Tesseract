package com.test.epassportreadertesseract;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText ID, Firstname, Surname, Sex, DOB, PassType, ExpDate, Nation, CitizenNo;
    Button buttonConfirm;
    final int RequestPermissionCode = 1;
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
        String result = intent.getStringExtra("result");
        if (result != null && !result.isEmpty()) extractString(result);
        final Activity activity = this;


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBlankEditText()){
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Please check your data");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(),
                                    "Save", Toast.LENGTH_SHORT).show();
                            People p = new People(PassType.getText().toString(),
                                    Surname.getText().toString(),
                                    Firstname.getText().toString(),
                                    ID.getText().toString(),
                                    Nation.getText().toString(),
                                    CitizenNo.getText().toString(),
                                    DOB.getText().toString(),
                                    Sex.getText().toString(),
                                    ExpDate.getText().toString());

                            Gson gson = new Gson();
                            String json = gson.toJson(p);
                      //      System.out.println("JSON :\n"+ json);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();
                        }
                    });
                    builder.show();

                }
            }
        });

    }


    private void extractString(String mrzResult) {
        String nID = "", nFirstname = "", nSurname = "", nSex = "", nDOB = "", nPassType = "", nExpDate = "", nNation = "", nCitizenNo = "";
        String line1 = mrzResult.split("\n")[0];
        String line2 = mrzResult.split("\n")[1];
        line1 = line1.replace(" ", "");
        line2 = line2.replace(" ", "");
        String arrLine1[] = line1.split("<");
        String arrLine2[] = line2.split("<");
        //  Log.i("1",line1);
        // Log.i("1", line2);
        String removeEmp1 = "";
        String removeEmp2 = "";
        for (int i = 0; i < arrLine1.length; i++) {
            if (!arrLine1[i].isEmpty()) {
                removeEmp1 += arrLine1[i] + ",";
                //  Log.i("1",removeEmp1);
            }
        }
        for (int i = 0; i < arrLine2.length; i++) {
            if (!arrLine2[i].isEmpty()) {
                removeEmp2 += arrLine2[i] + ",";
                // Log.i("1", removeEmp2);
            }
        }
        arrLine1 = removeEmp1.split(",");
        arrLine2 = removeEmp2.split(",");
        //  Log.i("1",arrLine1.length+"");
        //  Log.i("1",arrLine2.length+"");
        nPassType = arrLine1[0];
        nSurname = arrLine1[1].substring(3, arrLine1[1].length()).trim();
        for (int i = 2; i < arrLine1.length; i++) {
            nFirstname += arrLine1[i] + " ";
        }
        nFirstname = nFirstname.trim();
        int count = 0;
        for (int i = 0; i < arrLine2.length; i++) {
            if (!arrLine2[i].isEmpty()) {
                count++;
            }
        }
        int indexArr1 = 0;
        int indexArr2 = 0;
        int startID = 0;
        int stopID = 9;
        int startNation = 10;
        int startDOBY = 13;
        int startDOBM = 15;
        int startDOBD = 17;
        int startSex = 20;
        int startExpY = 21;
        int startExpM = 23;
        int startExpD = 25;
        int startCitizenID = 28;

        if (arrLine2.length == 3) {
            indexArr1 = 0;
            indexArr2 = 1;
            startID = 0;
            stopID = arrLine2[0].length();
            startNation = 1;
            startDOBY = 4;
            startDOBM = 6;
            startDOBD = 8;
            startSex = 11;
            startExpY = 12;
            startExpM = 14;
            startExpD = 16;
            startCitizenID = 18;
        }

        nID = arrLine2[indexArr1].substring(startID, stopID);
        nNation = arrLine2[indexArr2].substring(startNation, startNation + 3);

        try {
            int y = Integer.parseInt(arrLine2[indexArr2].substring(startDOBY, startDOBY + 2));
            int m = Integer.parseInt(arrLine2[indexArr2].substring(startDOBM, startDOBM + 2));
            int d = Integer.parseInt(arrLine2[indexArr2].substring(startDOBD, startDOBD + 2));
            nDOB = new SimpleDateFormat("yy/MM/dd").format(new Date(y, m - 1, d));
        } catch (Exception e) {
            Toast.makeText(this, "Please fill in your Date of Birth", Toast.LENGTH_SHORT).show();
        }

        nSex = arrLine2[indexArr2].substring(startSex, startSex + 1);

        try {
            int y = Integer.parseInt(arrLine2[indexArr2].substring(startExpY, startExpY + 2));
            int m = Integer.parseInt(arrLine2[indexArr2].substring(startExpM, startExpM + 2));
            int d = Integer.parseInt(arrLine2[indexArr2].substring(startExpD, startExpD + 2));
            nExpDate = new SimpleDateFormat("yy/MM/dd").format(new Date(y, m - 1, d));

        } catch (Exception e) {
            Toast.makeText(this, "Please fill in your Date of Expiry", Toast.LENGTH_SHORT).show();
        }

        nCitizenNo = arrLine2[indexArr2].substring(startCitizenID, arrLine2[indexArr2].length());
        setText(nPassType, nSurname, nFirstname, nID, nNation, nCitizenNo, nDOB, nSex, nExpDate);
    }

    private void setText(String nPassType,String nSurname,String nFirstname,String nID,String nNation,String nCitizenNo,
                         String nDOB,String nSex,String nExpDate) {
        ID.setText(nID);
        Firstname.setText(checkAlphabet(nFirstname));
        Surname.setText(checkAlphabet(nSurname));
        Sex.setText(checkAlphabet(nSex));
        CitizenNo.setText(nCitizenNo);
        DOB.setText(nDOB);
        PassType.setText(checkAlphabet(nPassType));
        ExpDate.setText(nExpDate);
        Nation.setText(checkAlphabet(nNation));
    }

    String checkAlphabet(String str) {
        if (str.contains("1") || str.contains("2") || str.contains("3") || str.contains("4") || str.contains("5") ||
                str.contains("6") || str.contains("7") || str.contains("8") || str.contains("9") || str.contains("0")) {
            return "";
        }
        return str;
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

    private void clearText() {
        ID.setText("");
        Firstname.setText("");
        Surname.setText("");
        Sex.setText("");
        CitizenNo.setText("");
        DOB.setText("");
        PassType.setText("");
        ExpDate.setText("");
        Nation.setText("");
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
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
}
