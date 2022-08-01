package com.sangharsh.books.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sangharsh.books.R;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;


public class BookmarkLongClickOptions extends BottomSheetDialogFragment {

    Context context;
    ArrayList<PDFModel> pdfModels;
    int pos;
    AppCompatButton deleteButton;
    PDFModel pdfModel;
    PDFAdapter pdfAdapter;

    public BookmarkLongClickOptions(Context context, ArrayList<PDFModel> pdfModels,PDFModel pdfModel,int position,PDFAdapter pdfAdapter){
        this.context = context;
        this.pdfModels = pdfModels;
        this.pos=position;
        this.pdfModel=pdfModel;
        this.pdfAdapter=pdfAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_click_options,container);
//        deleteButton = view.findViewById(R.id.delete);
        deleteButton.setText("Remove from Bookmark");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    pdfModels.remove(pdfModel);
                    deleteButton.setText("Remove from Bookmark");
                    pdfAdapter.notifyDataSetChanged();
                    new StorageHelper(context).savePDFModel(pdfModels,StorageHelper.BOOKMARKS);
                    dismiss();



            }
        });

        return view;
    }
}
