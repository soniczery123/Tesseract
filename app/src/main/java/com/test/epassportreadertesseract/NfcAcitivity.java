package com.test.epassportreadertesseract;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class NfcAcitivity extends AppCompatActivity {
    private NfcAdapter NFCadapter;
    String[] result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_acitivity);
        Intent intent = getIntent();
        result = intent.getStringArrayExtra("result");

        Log.e("RESULT:",result[5]+" "+result[7]);
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatch();
        System.out.println("RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
        System.out.println("ONPAUSE");
    }

    public void enableForegroundDispatch() {

        Context context = this.getApplicationContext();
        NFCadapter = NfcAdapter.getDefaultAdapter(this); // get default nfc adapter

        if (NFCadapter == null) {
            finish();
        } else if (!NFCadapter.isEnabled()) {
            finish();
        } else {
            //prepare the intent to the reader activity
            Intent i = new Intent(this, ePassportInfoDisplay.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("result", result);
            PendingIntent pending = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            String[][] techs = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};

            //enable the foregroundDispatch,
            //this will give this P
            // assportIfoDisplay priority over another
            //to manage this intent

            NFCadapter.enableForegroundDispatch(this, pending, null, techs);
            System.out.println("ENABLE");
        }
    }

    public void disableForegroundDispatch() {
        try {
            NFCadapter.disableForegroundDispatch(this);
            System.out.println("DIS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
