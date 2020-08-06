package org.tensorflow.lite.examples.detection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    EditText Id;
    EditText Pw;
    LinearLayout root;

 private static final String TAG ="LoginActivity";
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String uid;
    ProgressDialog dialog;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Id=(EditText)findViewById(R.id.id);
        Pw=(EditText)findViewById(R.id.password);
        root=(LinearLayout)findViewById(R.id.root);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 비밀번호 찾기
        TextView findPW = (TextView) findViewById(R.id.findPW);
        findPW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        // 회원가입
        TextView signUp = (TextView) findViewById(R.id.signUpBtn);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        // login 버튼
        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // firebase에서 있는지 확인, userType 확인
                // if userType = 사용자
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
                // else
//                Intent intent = new Intent(getApplicationContext(), MainGuardActivity.class);
//                startActivity(intent);
                login();
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Id.getWindowToken(),0);
                imm.hideSoftInputFromWindow(Pw.getWindowToken(),0);
            }
        });
    }

    private void login() {
        dialog = ProgressDialog.show(this,"","로그인 중입니다.",true);
        mAuth= FirebaseAuth.getInstance();

        email = Id.getText().toString();
        password =Pw.getText().toString();

        //기존사용자의 로그인
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "잘못된 정보입니다.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();//다이얼로그 끄기

                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {//로그인 성공 시 UID 확보, Token get하기
            uid = user.getUid();//FirebaseUser의 메소드
            getToken();
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            dialog.dismiss();//다이얼로그 끄기
                            Log.w(TAG,"getInstance failed",task.getException());
                            return;
                        }
                        //Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e(TAG,token);
                        saveToken(token);
                    }
                });
    }

    //db에 기기토큰 저장
    private void saveToken(String token) {
        mDatabase.child("users").child(uid).child("token").setValue(token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                });
    }
}
