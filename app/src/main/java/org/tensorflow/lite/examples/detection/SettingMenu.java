package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingMenu extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // buttons
        // backbuttons
        ImageView backBtn = (ImageView)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingMenu.this, DetectorActivity.class);
                startActivity(intent);
            }
        });

        // 정보 변경
        TextView userInfoBtn = (TextView) findViewById(R.id.userInfo);
        userInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingMenu.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        // 사용자 유형 변경
        TextView changeTypeBtn = (TextView) findViewById(R.id.changeType);
        changeTypeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingMenu.this, ChangeTypeActivity.class);
                startActivity(intent);
            }
        });

        // 연락처 목록
        TextView phoneListBtn = (TextView)findViewById(R.id.phoneList);
        phoneListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingMenu.this, PhoneListActivity.class);
                startActivity(intent);
            }
        });


        // logout
        TextView logoutBtn = (TextView)findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"로그아웃됩니다. 다시 로그인하세요.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingMenu.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
