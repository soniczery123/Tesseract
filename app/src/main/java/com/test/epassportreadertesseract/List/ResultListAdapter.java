package com.test.epassportreadertesseract.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.epassportreadertesseract.Model.PassportModel;
import com.test.epassportreadertesseract.R;

import java.util.ArrayList;

public class ResultListAdapter extends ArrayAdapter<PassportModel> {
    private Context mContext;
    private int mLayoutResId;
    private ArrayList<PassportModel> resultList;

    public ResultListAdapter(@NonNull Context context, int layoutResId, @NonNull ArrayList<PassportModel> resultList) {
        super(context, layoutResId, resultList);

        this.mContext = context;
        this.mLayoutResId = layoutResId;
        this.resultList = resultList;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemLayout = inflater.inflate(mLayoutResId, null);

        PassportModel item = resultList.get(position);

        TextView fn = (TextView)itemLayout.findViewById(R.id.textViewFirstName_List);
        TextView sn = (TextView)itemLayout.findViewById(R.id.textViewSurname_List);

        fn.setText(item.getFirstname());
        sn.setText(item.getSurname());



        return itemLayout;
    }
}