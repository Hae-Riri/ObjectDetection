package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.databinding.ItemPhonesBinding;
import org.tensorflow.lite.examples.detection.db_firebase.EmergencyContact;

import java.util.ArrayList;

public class AddPhoneAdapter extends RecyclerView.Adapter<AddPhoneAdapter.MyViewHolder> {
    Context context;
    ArrayList<EmergencyContact> datas;
    ItemPhonesBinding mBinding;

    public class MyViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener{
        ItemPhonesBinding mBinding;
        public View view;

        public MyViewHolder(ItemPhonesBinding mBinding){
            super(mBinding.getRoot());//mBinding의 getRoot로 view가져오기
            this.mBinding = mBinding;
        }
        public void bind (EmergencyContact emergencyContact){
            mBinding.name.setText(emergencyContact.getName());
            mBinding.phone.setText(emergencyContact.getNumber());
            mBinding.getRoot().setOnCreateContextMenuListener(this);
        }
//        public void OnClick(View view){
//            if(view.equals(mBinding.delete)) {
//                ((PhoneListActivity) context).deleteServerDB(getAdapterPosition());
//            }
@Override
public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
    //MenuItem Edit = menu.add(Menu.NONE,1001,1,"편집");
    MenuItem Delete = menu.add(Menu.NONE,1002,2,"삭제");
    //Edit.setOnMenuItemClickListener(onEditMenu);
    Delete.setOnMenuItemClickListener(onEditMenu);
}
        //4.컨텍스트 메뉴에서 항목 클릭 시의 동작(삭제,편집)
        private final MenuItem.OnMenuItemClickListener onEditMenu =
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case 1002://삭제

                                //firebase에서의 삭제
                                ((PhoneListActivity)context).deleteServerDB(getAdapterPosition());

                                //View에서의 삭제
//                                datas.remove(7 - (getAdapterPosition()+1));
//                                notifyItemRemoved(getAdapterPosition());
//                                notifyItemRangeChanged(getAdapterPosition(),datas.size());
                                break;
                        }
                        return true;
                    }
                };


    }

    public AddPhoneAdapter(Context context, ArrayList<EmergencyContact> datas){
        this.context=context;
        this.datas=datas;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ItemPhonesBinding mBinding = ItemPhonesBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        //생성한 뷰 홀더 객체를 리턴
        return new MyViewHolder(mBinding);
    }

    //위에서 저장한 뷰 홀더 객체를 화면에 표시
    //포지션에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시하기

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
