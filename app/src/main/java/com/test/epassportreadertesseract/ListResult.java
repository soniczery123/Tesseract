package com.test.epassportreadertesseract;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.test.epassportreadertesseract.List.ResultListAdapter;
import com.test.epassportreadertesseract.Model.PassportModel;
import com.test.epassportreadertesseract.SaveToDB.ApiInterface;
import com.test.epassportreadertesseract.SaveToDB.ConnectServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListResult extends AppCompatActivity {
    private ResultListAdapter mAdapter;
    private ArrayList<PassportModel> resultItemList = new ArrayList<>();
    TextView ID_Con, Firstname_Con, Surname_Con, Sex_Con, DOB_Con, PassType_Con, ExpDate_Con, Nation_Con, CitizenNo_Con, titleDialog;
    EditText editText_Search;
    ListView lv;
    Button button_Search;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        lv = (ListView) findViewById(R.id.resultList);
        editText_Search = (EditText) findViewById(R.id.editText_Search);
        button_Search = (Button) findViewById(R.id.button_Search);
        getResult();
        mAdapter = new ResultListAdapter(
                this,
                R.layout.list_detail,
                resultItemList
        );
        lv.setAdapter(mAdapter);

        button_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResult();
                mAdapter.notifyDataSetChanged();
                editText_Search.clearFocus();

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDataClick(i);
            }
        });
    }


    public void showDataClick(int index) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListResult.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_confirm_data, null);
        builder.setView(view);
        titleDialog = (TextView) view.findViewById(R.id.titleDialog);
        titleDialog.setText(R.string.title_dialog_show_data);
        ID_Con = (TextView) view.findViewById(R.id.textViewID_Con);
        CitizenNo_Con = (TextView) view.findViewById(R.id.textViewCitizen_Con);
        Firstname_Con = (TextView) view.findViewById(R.id.textViewFirstName_Con);
        Surname_Con = (TextView) view.findViewById(R.id.textViewSurname_Con);
        Sex_Con = (TextView) view.findViewById(R.id.textViewSex_Con);
        DOB_Con = (TextView) view.findViewById(R.id.textViewDOB_Con);
        PassType_Con = (TextView) view.findViewById(R.id.textViewPassportType_Con);
        ExpDate_Con = (TextView) view.findViewById(R.id.textViewExpDate_Con);
        Nation_Con = (TextView) view.findViewById(R.id.textViewNation_Con);

        ID_Con.setText(resultItemList.get(index).getPassportNo());
        Firstname_Con.setText(resultItemList.get(index).getFirstname());
        Surname_Con.setText((resultItemList.get(index).getSurname()));
        Sex_Con.setText(resultItemList.get(index).getSex());
        CitizenNo_Con.setText(resultItemList.get(index).getCitizenID());
        DOB_Con.setText(resultItemList.get(index).getDOB());
        PassType_Con.setText(resultItemList.get(index).getPassportType());
        ExpDate_Con.setText(resultItemList.get(index).getExpDate());
        Nation_Con.setText(resultItemList.get(index).getNationality());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void getResult() {
        ApiInterface apiInterface = ConnectServer.getClient().create(ApiInterface.class);
        Call<List<PassportModel>> call = apiInterface.getData(editText_Search.getText().toString());
        call.enqueue(new Callback<List<PassportModel>>() {
            @Override
            public void onResponse(Call<List<PassportModel>> call, Response<List<PassportModel>> response) {
                resultItemList.clear();
                for (PassportModel passportModel : response.body()) {
                    resultItemList.add(passportModel);
                }
            }

            @Override
            public void onFailure(Call<List<PassportModel>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
}
