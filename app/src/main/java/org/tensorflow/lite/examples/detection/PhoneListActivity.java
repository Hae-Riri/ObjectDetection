package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.db_firebase.EmergencyContact;
import org.tensorflow.lite.examples.detection.db_firebase.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PhoneListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    String uid;
    User user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<EmergencyContact> datas = new ArrayList<>();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonelist);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerViewList);
        // 리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 추가 버튼
        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 연락처 추가 화면으로 이동
                if(adapter.getItemCount() < 8) {
                    AddGuardDialog addGuardDialog = new AddGuardDialog(PhoneListActivity.this, uid);
                    addGuardDialog.show();
                }else{
                    Toast.makeText(getApplicationContext(),"연락처는 8개까지 저장 가능합니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 뒤로 가기 버튼
        ImageView backBtn = (ImageView)findViewById(R.id.backBtn1);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new ClickLister() {
            @Override
            public void onClick(View view, int position) {
                EmergencyContact contact = datas.get(position);
                Toast.makeText(PhoneListActivity.this,contact.getName()+
                        ' '+contact.getNumber()+' ', Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }
    public interface ClickLister{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private PhoneListActivity.ClickLister clickLister;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                     final PhoneListActivity.ClickLister clickLister){
            this.clickLister = clickLister;
            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clickLister!=null){
                        clickLister.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickLister!=null&&gestureDetector.onTouchEvent(e)){
                clickLister.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(currentUser);
        setUser(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        
    }
    private void setUser(FirebaseUser currentUser){
        if(currentUser == null ){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }else{
            uid = currentUser.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    setRV();
                    //현 user데이터 갱신과 동시에 rv를 set하기
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void setRV() {
        datas.clear();
        if(user.getEmergencyContact()!=null && user.getEmergencyContact().size()!=0){
            //하나씩 묶음 읽어오기
            for(Map.Entry<String, String>entry:user.getEmergencyContact().entrySet()){
                //읽어서 ArrayList datas에 하나씩 저장하기
                datas.add(new EmergencyContact(entry.getValue(),entry.getKey()));
                //이름이 key, 번호가 value
            }
        }
        //객체 지정
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);//rv크기 고정
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //rv에 올릴 adapater 객체 지정
        adapter = new AddPhoneAdapter(this,datas);
        recyclerView.setAdapter(adapter);
    }


    public void deleteServerDB(int position){
        String receiverName = datas.get(position).getName();
        String receiverPhone = datas.get(position).getNumber();

        String deleteNum = datas.get(position).getNumber();
        mDatabase.child("users").child(uid).child("emergencyContact")
                .child(deleteNum).removeValue();
        //adapter.notifyDataSetChanged();
        setUser(currentUser);


    }

    public void addData(EmergencyContact data) {
        datas.add(data);
        setUser(currentUser);
        //adapter.notifyDataSetChanged();
        Toast.makeText(PhoneListActivity.this,"상대방에게 설치알림 문자가 발송되었습니다.", Toast.LENGTH_LONG).show();
    }
}

