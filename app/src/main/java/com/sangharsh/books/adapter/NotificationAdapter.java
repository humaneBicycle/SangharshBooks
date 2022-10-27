package com.sangharsh.books.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangharsh.books.R;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;
import com.sangharsh.books.model.Notification;

import java.util.ArrayList;
import java.util.Random;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    Context context;
    ArrayList<Notification> notifications;
    UIUpdateHomeFrag callback;
    ArrayList<Integer> colors;
    public NotificationAdapter(Context context, ArrayList<Notification> notifications, UIUpdateHomeFrag callback){
        this.context=context;
        this.notifications=notifications;
        this.callback = callback;

        inflateColors();
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item,new LinearLayout(context),false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.titleTV.setText(notifications.get(position).getTitle());
        holder.bodyTV.setText(notifications.get(position).getBody());

        int randForColor = new Random().nextInt(colors.size());
        Drawable drawable=context.getResources().getDrawable(R.drawable.tiny_stroke);
        drawable.setTint(context.getResources().getColor(colors.get(randForColor)));
        holder.itemBody.setBackgroundDrawable(drawable);
        colors.remove(randForColor);
        if(colors.size()==0){
            inflateColors();
        }

    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        TextView bodyTV, titleTV;
        CardView itemBody;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            bodyTV = itemView.findViewById(R.id.notification_item_body);
            titleTV = itemView.findViewById(R.id.notification_item_title);
            itemBody = itemView.findViewById(R.id.notification_item_background);
        }
    }
    private void inflateColors(){
        if(colors==null) {
            colors = new ArrayList<>();
        }
        colors.add(R.color.my_green);
        colors.add(R.color.my_blue);
        colors.add(R.color.my_red);
        colors.add(R.color.my_yellow);
        colors.add(R.color.my_skyblue);
        colors.add(R.color.my_purple);
    }
}
