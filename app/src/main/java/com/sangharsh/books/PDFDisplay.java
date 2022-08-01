package com.sangharsh.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdfview.PDFView;
import com.sangharsh.books.model.PDFModel;

import java.io.File;
import java.util.ArrayList;

public class PDFDisplay extends AppCompatActivity {
    SangharshBooks sangharshBooks;
    PDFView pdfView;
    ImageView bookmarkButton,back;
    TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.Theme_Dark);
                ((SangharshBooks)getApplication()).setDarkMode(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.Theme_Light);
                ((SangharshBooks)getApplication()).setDarkMode(false);
                break;
        }
        setContentView(R.layout.activity_pdfdisplay);
        sangharshBooks = (SangharshBooks)getApplication();
        pdfView =  findViewById(R.id.pdf_viewer);
        heading = findViewById(R.id.pdf_view_heading);
        bookmarkButton = findViewById(R.id.is_bookmarked_pdf_view);
        back = findViewById(R.id.back_pdf_view);

        ArrayList<PDFModel> pdfModels = new StorageHelper(this).getArrayListOfPDFModel(StorageHelper.BOOKMARKS);

        for(int i =0;i<pdfModels.size();i++){
            if(pdfModels.get(i).getPointingDir().equals(sangharshBooks.getActivePdfModel().getPointingDir())){
                Log.d("sba pdf found at", ""+i);
                bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                break;
            }
        }
        heading.setText(sangharshBooks.getActivePdfModel().getName());
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean b=false;
                int j = 0;
                for(int i =0;i<pdfModels.size();i++){
                    if(pdfModels.get(i).getPointingDir().equals(sangharshBooks.getActivePdfModel().getPointingDir())){
                        b=true;
                        j=i;
                        break;
                    }
                }
                if(b){
                    bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    pdfModels.remove(j);
                    new StorageHelper(PDFDisplay.this).savePDFModel(pdfModels,StorageHelper.BOOKMARKS);
                }else{
                    bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    pdfModels.add(sangharshBooks.getActivePdfModel());
                    new StorageHelper(PDFDisplay.this).savePDFModel(pdfModels,StorageHelper.BOOKMARKS);
                }
            }
        });

        String dirPath = getFilesDir().getAbsolutePath()+"/"+sangharshBooks.getActivePdfModel().getPointingDir()+".pdf";
        File file = new File(dirPath);
        pdfView.fromFile(file).show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        sangharshBooks.clearRecentPDFModel();
        super.onBackPressed();
    }


}