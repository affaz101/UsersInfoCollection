package com.abu.users.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abu.users.Model.ItemModel;
import com.abu.users.R;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    ArrayList<ItemModel> arrayList;
    Context context;

    public MyRecyclerAdapter(Context con, ArrayList<ItemModel> arrl) {
        context = con;
        arrayList = arrl;
    }

    @NonNull
    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.MyViewHolder holder, int position) {
        ItemModel model =arrayList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvPhone.setText(model.getPhone());
        int g = model.getGender();
        holder.tvGender.setText(mGenderText(g));
        holder.tvAddress.setText(model.getAddress());


    }
    private String mGenderText(int i){
        String s = "";
        switch (i){
            case 1:
                s = "Male";
                break;
            case 2:
                s = "Female";
                break;
            case 3:
                s = "Other";
                break;
        }
        return s;
    };

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvGender, tvAddress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_name_id);
            tvPhone = (TextView) itemView.findViewById(R.id.item_phone_id);
            tvGender = (TextView) itemView.findViewById(R.id.item_gender_id);
            tvAddress = (TextView) itemView.findViewById(R.id.item_address_id);

        }
    }
}
