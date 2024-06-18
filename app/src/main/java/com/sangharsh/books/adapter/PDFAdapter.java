package com.sangharsh.books.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangharsh.books.PDFDisplay;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.MyViewHolder> {

    Context context;
    ArrayList<PDFModel> pdfModels;
    SangharshBooks sangharshBooks;
    String mode;


    public PDFAdapter (Application application, Context context, ArrayList<PDFModel> pdfModels,String mode){
        this.context = context;
        this.pdfModels = pdfModels;
        this.sangharshBooks = (SangharshBooks) application;
        this.mode = mode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_item,new LinearLayout(context),false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.pdfName.setText(pdfModels.get(position).getName());
        holder.openTxt.setVisibility(View.VISIBLE);
        holder.lockLayout.setVisibility(View.GONE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sangharshBooks.setActivePdfModel(pdfModels.get(position));
                context.startActivity(new Intent(context, PDFDisplay.class));
            }
        });

        if(mode.equals("bookmarks")){
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    BookmarkLongClickOptions bookmarkLongClickOptions = new BookmarkLongClickOptions(context,pdfModels,pdfModels.get(position),position,PDFAdapter.this);
                    bookmarkLongClickOptions.show(((AppCompatActivity)context).getSupportFragmentManager(),"bookmarkLongClick");
                    return true;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return pdfModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pdfName;
        CardView cardView;

        LinearLayout lockLayout;
        TextView priceTxt;
        TextView openTxt;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfName = itemView.findViewById(R.id.pdf_name_pdf_item);
            cardView = itemView.findViewById(R.id.pdf_item_background);

            lockLayout = itemView.findViewById(R.id.paidLayout);
            openTxt = itemView.findViewById(R.id.openTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
        }
    }
}
