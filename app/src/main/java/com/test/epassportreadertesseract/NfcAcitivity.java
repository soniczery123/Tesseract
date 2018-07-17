package com.test.epassportreadertesseract;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class NfcAcitivity extends AppCompatActivity {
    private NfcAdapter NFCadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_acitivity);
        System.out.println("MAIN");
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
            // tratar erro
        } else if (!NFCadapter.isEnabled()) {
            // tratar erro
        } else {
            //prepare the intent to the reader activity
            Intent i = new Intent(NfcAcitivity.this, ePassportInfoDisplay.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pending = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            String[][] techs = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};

            //enable the foregroundDispatch,
            //this will give this P
            // assportIfoDisplay priority over another
            //to manage this intent

            NFCadapter.enableForegroundDispatch(NfcAcitivity.this, pending, null, techs);System.out.println("ENABLE");
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
