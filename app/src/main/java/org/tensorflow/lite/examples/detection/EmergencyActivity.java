package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.db_firebase.User;
import org.tensorflow.lite.examples.detection.sos.GPSManager;
import org.tensorflow.lite.examples.detection.sos.PushPayload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {
    ProgressBar barTimer;

    Button cancelBtn;

    public static Context mContext;
    String uid;
    User user;
    FirebaseUser currentUser;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;

    GPSManager gm;
    ArrayList<String> tokens = new ArrayList<>();

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        startTimer(30);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //사고아닐 때 count reset하기
                countDownTimer.cancel();
                finish();
            }
        });


        mContext = this;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        setUser(currentUser);
        PrefrenceManager.setBoolean(this,"emergencyIntent",false);
    }

    private void setUser(FirebaseUser currentUser) {
        if (currentUser == null) {
            //로그인상태 유지가 안된 것이므로 다시 로그인화면으로 이동
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {//성공했을 때
            uid = currentUser.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//비동기처리
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void startTimer(final int sec) {
        barTimer = (ProgressBar) findViewById(R.id.barTimer);
        countDownTimer = new CountDownTimer(sec * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                barTimer.setProgress((int)seconds);

            }
            @Override
            public void onFinish() {
                // 사고 발생 처리
                getEmergencyContactToken();
                Toast.makeText(getApplicationContext(),"보호자에게 푸시알림을 전송합니다.", Toast.LENGTH_LONG).show();
            }
        }.start();

    }

    private void getEmergencyContactToken() {

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //User 정보 한 명씩 가져와서 target에 저장(uid,이메일,emergencyContact전부다)
                    //target은 모든 사용자의 정보가 들어감
                    User target = ds.getValue(User.class);
                    //user정보 중 emergencyContact 속 정보를 하나씩 entry에 가져옴(전화번호,이름)
                    //entry는 특정 사용자의 연락처 속 사람들의 정보가 있음 target의 번호와 entry일치 확인
                    for(Map.Entry<String, String>entry : user.getEmergencyContact().entrySet()){
                        if(target.getPhoneNumber().equals(entry.getKey())){
                            tokens.add(target.getToken());
                        }else{//일치하는 사용자가 없으면 그냥 문자만 보내기
//                            sms.sendSOS();
                        }
                    }
                }
                for(String t: tokens){
                    Log.e("token before push : ",t);
                }
                sendPush(tokens);
                for(String t:tokens){
                    Log.e("token after send : ",t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendPush(ArrayList<String> tokens) {
        gm = new GPSManager(this);
        String location = gm.getAddress();
        Log.e("location:",location);
        for(String t:tokens){
            //index.js에 따라
            String key = mDatabase.child("push").child("token").push().getKey();
            PushPayload data = new PushPayload();//push정보 저장
            data.setKey(key);
            data.setTitle(user.getName()+"님 사고 발생 알림");
            data.setMessage( "[Rescue ONE]\n"+user.getName()+
                    "님이 사고발생을 알립니다.\n위치:"+location);
            data.setToken(t);
            data.setAddress(location);
            mDatabase.child("push").child("token").child(key).setValue(data);
            mDatabase.child("push").child("token").child(key).child("location").setValue(location);
        }
    }
}
