package com.opensource.ganada;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    private View view;
    private BottomSheetListener listener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView bottomName;
    TextView bottomEmail;
    TextView bottomRole;
    ImageView bottomExit;
    Button reviseUser;
    Button logOut;
    Button signOut;
    TextView link1;
    TextView link2;
    TextView link3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_menu, container, false);
        mAuth = FirebaseAuth.getInstance();
        listener = (BottomSheetListener) getContext();
        bottomName = view.findViewById(R.id.bottom_name);
        bottomEmail = view.findViewById(R.id.bottom_email);
        bottomRole = view.findViewById(R.id.bottom_role);
        bottomExit = view.findViewById(R.id.bottom_exit); bottomExit.setOnClickListener(this);
        reviseUser = view.findViewById(R.id.bottom_revise_user); reviseUser.setOnClickListener(this);
        logOut = view.findViewById(R.id.bottom_logout); logOut.setOnClickListener(this);
        signOut = view.findViewById(R.id.bottom_signout); signOut.setOnClickListener(this);
        link1 = view.findViewById(R.id.bottom_link1); link1.setOnClickListener(this);
        link2 = view.findViewById(R.id.bottom_link2); link2.setOnClickListener(this);
        link3 = view.findViewById(R.id.bottom_link3); link3.setOnClickListener(this);
        get_user_info();
        return view;
    }

    public void get_user_info() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                bottomName.setText(user.getName());
                bottomEmail.setText(user.getBirth());
                if(user.getRole().equals("Teacher")) {
                    bottomRole.setText("교사");
                    bottomRole.setBackgroundColor(Color.parseColor("#FF9A51"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent urlintent;
        switch (view.getId()) {
            case R.id.bottom_exit:
                dismiss();
                break;
            case R.id.bottom_revise_user:
                Intent intent = new Intent(getActivity(), ModifyMemeberInfo.class);
                startActivity(intent);
                break;
            case R.id.bottom_logout:
                listener.onButtonClicked("logout");
                break;
            case R.id.bottom_signout:
                listener.onButtonClicked("signout");
                break;
            case R.id.bottom_link1:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rizkiarm/LipNet"));
                startActivity(urlintent);
                break;
            case R.id.bottom_link2:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kykymouse/koreanLipNet"));
                startActivity(urlintent);
                break;
            case R.id.bottom_link3:
                urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=JMfCyvMQ-JM"));
                startActivity(urlintent);
                break;
        }
    }

    // 부모 액티비티와 연결하기 위한 인터페이스
    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}
