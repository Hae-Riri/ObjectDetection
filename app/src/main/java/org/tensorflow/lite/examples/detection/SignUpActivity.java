package org.tensorflow.lite.examples.detection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.db_firebase.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText phone;
    private LinearLayout root;

    private static final String TAG = "RegisterUserActivity";
    private FirebaseAuth mAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth= FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.userEmail);
        name = (EditText) findViewById(R.id.userName);
        phone = (EditText) findViewById(R.id.userPhone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());  //자동 하이픈(-) 생성
        root=(LinearLayout)findViewById(R.id.root);
        // 회원가입 버튼
        Button signUpBtn = (Button)findViewById(R.id.doneSignUp);
        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                join();
//                if(!email.getText().toString().equals("") && !name.getText().toString().equals("") && !phone.getText().toString().equals("")){
//                    // Firebase에서 회원가입 완료 후 아래 코드 넣을 것
////                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                    startActivity(intent);
//                } else {
////                    Toast.makeText(SignUpActivity.this, "공란이 존재합니다.", Toast.LENGTH_LONG).show();
//                }
            }
        });

//        바깥화면 터치 시 키보드 없애기
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(),0);
                imm.hideSoftInputFromWindow(name.getWindowToken(),0);
                imm.hideSoftInputFromWindow(phone.getWindowToken(),0);
            }
        });

    }

    private void join() {
        //정보들어갈때까지 다이얼로그 안꺼지게
        dialog = ProgressDialog.show(this,"",
                "회원정보를 등록 중입니다. 비밀번호 재설정 메일을 전송합니다.",true);
        String stringEmail = email.getText().toString();
        String password = UUID.randomUUID().toString().substring(0,8);//랜덤비번배부

        //사용자생성
        mAuth.createUserWithEmailAndPassword(stringEmail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            updateUI(null);
                            Toast.makeText(SignUpActivity.this, "잘못된 정보입니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){//user정보가 제대로 들어오면 data에 업로드
            updateUserData(user);
        }
    }

    private void updateUserData(FirebaseUser firebaseUser) {
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setEmail(email.getText().toString());
        user.setName(name.getText().toString());
        user.setPhoneNumber(phone.getText().toString());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users")
                .child(firebaseUser.getUid());

        //database에 setValue하면서 비번재설정메일 전송
        myRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();//다이얼로그종료
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                });
    }
}
