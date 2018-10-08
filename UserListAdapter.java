package com.chatdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserObjectViewHolder> {

    private Context context;
    private ArrayList<UserVo> userList;
    private OnItemClickInterface onItemClickInterface;

    public UserListAdapter(Context context, ArrayList<UserVo> userList, OnItemClickInterface onItemClickInterface) {
        this.context = context;
        this.userList = userList;
        this.onItemClickInterface = onItemClickInterface;
    }

    @NonNull
    @Override
    public UserObjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_list_row, parent, false);

        return new UserObjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserObjectViewHolder holder, final int position) {
        final UserVo user = userList.get(position);
        holder.username.setText(user.getUserName());
        holder.username.setTextColor(Color.BLACK);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Chat",Toast.LENGTH_SHORT).show();
                onItemClickInterface.onItemClick(position,user);
                UserDetails.chatWith = user.userId;
               // context.startActivity(new Intent(context, chat.class));

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserObjectViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public RelativeLayout relativeLayout;

        public UserObjectViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            relativeLayout = itemView.findViewById(R.id.relative_user_list);
        }
    }
}
