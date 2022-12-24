package com.opensource.ganada;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class FirstJoinFragment extends Fragment implements View.OnClickListener{
    View root;
    View is_teacher;
    View is_parent;
    View check1;
    View check2;
    String birth;
    TextView birthText;
    ImageButton openCalender;
    int role = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.join_first_fragment, container, false);
        is_teacher = root.findViewById(R.id.is_teacher); is_teacher.setOnClickListener(this);
        is_parent = root.findViewById(R.id.is_parent); is_parent.setOnClickListener(this);
        openCalender = root.findViewById(R.id.openCalender); openCalender.setOnClickListener(this);
        birthText = root.findViewById(R.id.birth_text);
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.is_teacher:
                Log.d("test","성공");
                check1 = root.findViewById(R.id.check1);
                check2 = root.findViewById(R.id.check2);
                check1.setVisibility(View.VISIBLE);
                check2.setVisibility(View.INVISIBLE);
                role = 1;
                break;
            case R.id.is_parent:
                check1 = root.findViewById(R.id.check1);
                check2 = root.findViewById(R.id.check2);
                check1.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.VISIBLE);
                role = 2;
                break;
            case R.id.openCalender:
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), mDateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.show();
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                    // Date Picker에서 선택한 날짜를 TextView에 설정
                    birth = String.format("%d-%d-%d", yy,mm+1,dd);
                    birthText.setText(String.format("%d년 %d월 %d일", yy,mm+1,dd));
                }
            };

    public String getBirthText() {
        return birth;
    }

    public int getRole() {
        return role;
    }
}