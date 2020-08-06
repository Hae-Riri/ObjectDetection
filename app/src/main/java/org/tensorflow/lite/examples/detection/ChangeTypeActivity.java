package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeTypeActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changetype);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        // 처음 type
        int useType = 1;        // DB에서 가져올 것
        if(useType == 1){
            radioGroup.check(R.id.userType);
        } else {
            radioGroup.check(R.id.guardType);
        }

        // 변경 버튼 누르는 경우
        Button changeBtn = (Button)findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 디비 처리 해줘야 함.
                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(intent);
            }
        });

        // 뒤로 가기 버튼
        ImageView backBtn = (ImageView)findViewById(R.id.backBtn3);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
}
