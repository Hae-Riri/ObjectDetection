package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.examples.detection.db_firebase.User;

public class SettingMenu extends AppCompatActivity {

    TextView textName, textEmail, textPhone,logout,deleteEmail,gotoList;
    ImageView backBtn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    String phone;
    String name;
    String email;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        backBtn=(ImageView)findViewById(R.id.backBtn);

        textName = (TextView)findViewById(R.id.textName);
        textEmail =(TextView)findViewById(R.id.textEmail);
        textPhone = (TextView)findViewById(R.id.textPhone);
        logout = (TextView)findViewById(R.id.logout);
        deleteEmail = (TextView)findViewById(R.id.deleteEmail);
        gotoList=(TextView)findViewById(R.id.gotoList);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingMenu.this,CameraActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        });

        mDatabase.child("users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
                Log.d("SettingMenu","name is: "+name);
                //가져온 값 넣어주기
                textName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.getValue(String.class);
                Log.d("SettingMenu","name is: "+email);
                textEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //db에서 사용자정보 읽어오기
        mDatabase.child("users").child(uid).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.getValue(String.class);
                Log.d("SettingMenu","name is: "+phone);
                textPhone.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(SettingMenu.this,"로그아웃됩니다.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingMenu.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        });

        deleteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mDatabase.child("users").child(uid).removeValue();
                            Intent intent=new Intent(SettingMenu.this,LoginActivity.class);
                            startActivity(intent);
                        }

                    }
                });

            }
        });

        gotoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingMenu.this,PhoneListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
