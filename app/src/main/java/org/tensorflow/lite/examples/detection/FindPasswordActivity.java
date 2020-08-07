package org.tensorflow.lite.examples.detection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FindPasswordActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        //뒤로가기
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        email=(EditText)findViewById(R.id.email);

        Button findBtn = (Button)findViewById(R.id.findPW);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().matches(getString(R.string.email_match_checker))){
                    Toast.makeText(FindPasswordActivity.this,"이메일 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                }else{
                    findPassword();
                }
            }
        });

    }

    private void findPassword() {
        //마지막 인자인 indeterminate가 true이면 안 꺼짐
        dialog =ProgressDialog.show(FindPasswordActivity.this,"","이메일 전송 중입니다.",true);
        mAuth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FindPasswordActivity.this,"비밀번호 재설정 이메일을 보냈습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                        dialog.dismiss();

                        currentUser = mAuth.getCurrentUser();

                        if(currentUser!=null){
                            Intent intent=new Intent(getApplication(),LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            finish();
                        }
                    }
                });
    }
}
