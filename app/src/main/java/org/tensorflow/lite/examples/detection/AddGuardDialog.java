package org.tensorflow.lite.examples.detection;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.examples.detection.db_firebase.EmergencyContact;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGuardDialog extends Dialog {

    EditText name;
    EditText phone;
    TextView cancel;
    TextView save;

    DatabaseReference mDatabase;
    Context context;
    String uid;

    public AddGuardDialog(Context context, String uid){
        super(context);
        this.context=context;
        this.uid=uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addguard);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.guardName);
        phone=findViewById(R.id.guardPhone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        cancel=findViewById(R.id.cancel);
        save=findViewById(R.id.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PhoneListActivity)context).addData(
                        new EmergencyContact(
                                name.getText().toString(),
                                phone.getText().toString()));

                Log.e("A",uid+" "+phone.getText().toString());
                mDatabase.child("users").child(uid).child("emergencyContact")
                        .child(phone.getText().toString())
                        .setValue(name.getText().toString());



//            //AddReceiver Activity로 값 전달
//            mDialogListener.onSaveClicked(name.getText().toString(),
//                    number.getText().toString());
                dismiss();
            }
        });

    }
}
