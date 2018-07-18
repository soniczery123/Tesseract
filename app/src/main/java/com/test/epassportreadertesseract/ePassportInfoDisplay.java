package com.test.epassportreadertesseract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import teste.epassporttest.Facade.Facade;
import teste.epassporttest.data.Credentials;
import teste.epassporttest.data.Passenger;

public class ePassportInfoDisplay extends Activity
{

    Facade fachada;
    String[] result;
    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_passport_info_display);
        Intent intent = getIntent();
        result = intent.getStringArrayExtra("result");
        views();

        startReading();
    }

    private void startReading()
    {
        fachada = new Facade(getData(result), new Facade.ReadingPassport()
        {
            @Override
            public void readingPassportDataSucess(Passenger passenger)
            {

                result[0] = passenger.getPassportNumber();
                result[1] = passenger.getDocumentID();
                result[2] = passenger.getName();
                result[3] = passenger.getSurname();
                result[4] = passenger.getGender();
                DateFormat df =  new SimpleDateFormat("yyMMdd");
                DateFormat df2 =  new SimpleDateFormat("yy/MM/dd");
                try {
                    Date dateDOB = df.parse(result[5]);
                    Date dateEXP = df.parse(result[7]);
                    result[5] = df2.format(dateDOB);
                    result[7] = df2.format(dateEXP);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                result[8] = passenger.getNationality();


            }

            @Override
            public void readingPassportImageSucess(Bitmap passengerPhoto)
            {
                bmp = passengerPhoto;



            }

            @Override
            public void readingPassportDataFail(String error) {}

            @Override
            public void readingPassportImageFail(String error) {}

            @Override
            public void readingPassportFinished()
            {

                findViewById(teste.epassporttest.R.id.loadingPanel).setVisibility(View.GONE);
                Intent i = new Intent(ePassportInfoDisplay.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                bmp = Bitmap.createScaledBitmap(bmp, 150, 200, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                i.putExtra("bmp",byteArray);
                i.putExtra("result",result);
                startActivity(i);
            }

            @Override
            public void readingPassportFail(String error) {}

        });

        fachada.handleIntent(this.getIntent());
    }

    //ASSOCIA COMPONENTES A VARIAVEIS
    private void views(){

    }

    private ArrayList<Credentials> getData(String [] result)
    {
        ArrayList<Credentials> allCredentials = new ArrayList<Credentials>();

        try
        {

            Credentials credentialGabriel = new Credentials(result[0],result[5], result[7]);

            allCredentials.add(credentialGabriel);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return allCredentials;
    }


}
