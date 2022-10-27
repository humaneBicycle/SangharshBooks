package com.sangharsh.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.adapter.DirectoryAdapter;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.PDFModel;
import com.sangharsh.books.interfaces.DirectoryChangeListener;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;

import java.util.ArrayList;

public class FileActivity extends AppCompatActivity implements DirectoryChangeListener, UIUpdateHomeFrag {
    ProgressBar progressBar;
    TextView nothingAvailableTV, heading;
    SangharshBooks sangharshBooks;
    Directory directory;
    ArrayList<Directory> directories;
    RecyclerView recyclerView;
    DirectoryAdapter directoryAdapter;
    ImageView imageView;
    int hotFixDontAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sangharshBooks = (SangharshBooks)getApplication();
        if(sangharshBooks.isDarkMode()){
            setTheme(R.style.Theme_Dark);
        }else{
            setTheme(R.style.Theme_Light);
        }

        setContentView(R.layout.activity_file);
        progressBar = findViewById(R.id.progress_file_activity);
        nothingAvailableTV = findViewById(R.id.text_nothing_available_file_activity);
        heading = findViewById(R.id.file_activity_heading);
        imageView = findViewById(R.id.back_file);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        heading.setText(sangharshBooks.getLatestDir());
        sangharshBooks.setAdCount(sangharshBooks.getAdCount()+1);

        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",sangharshBooks.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        directories = (ArrayList<Directory>) task.getResult().toObjects(Directory.class);
                        directory =  task.getResult().toObjects(Directory.class).get(0);
                        recyclerView = findViewById(R.id.rv_file_activity);
                        directoryAdapter = new DirectoryAdapter(FileActivity.this,directory,(SangharshBooks) getApplication(),FileActivity.this);
                        recyclerView.setAdapter(directoryAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(FileActivity.this));
                        progressBar.setVisibility(View.GONE);
                        if(!(directory.getFiles().size()>0) && !(directory.getPdfModels().size()>0)){
                            nothingAvailableTV.setVisibility(View.VISIBLE);
                        }
                    }else{
                        //new directory created.
                        hotFixDontAsk=1;
                        directory = new Directory(1,new ArrayList<>(),new ArrayList<>(),sangharshBooks.getPath());
                        directoryAdapter = new DirectoryAdapter(FileActivity.this,directory,(SangharshBooks) getApplication(),FileActivity.this);
                        FirebaseFirestore.getInstance().collection("directory").document().set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Toast.makeText(FileActivity.this, "Created successfully!", Toast.LENGTH_SHORT).show();
                                    Log.d("sba", "onComplete: created empty document");
                                }else{
                                    Toast.makeText(FileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        progressBar.setVisibility(View.GONE);
                        nothingAvailableTV.setVisibility(View.VISIBLE);
                        Log.d("sba", "onComplete: empty result"+task.getException());
                        //task empty
                    }
                }else{
                    Toast.makeText(FileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d("sba", "onComplete: "+task.getException());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sangharshBooks.removeRecentDirectoryFromPath();
    }

    @Override
    public void update() {
        //recreate();
    }

    @Override
    public void onFileModelAdded(FileModel fileModel) {
        if(hotFixDontAsk==1){
            recreate();
            hotFixDontAsk++;
        }else {
            if (directory != null && directoryAdapter != null) {
                directory.getFiles().add(fileModel);
                directoryAdapter.notifyDataSetChanged();
            }
            if (directory.getPdfModels().size() + directory.getFiles().size() == 1) {
                nothingAvailableTV.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onPDFModelAdded(PDFModel pdfModel) {
        if(hotFixDontAsk==1){
            recreate();
        }else {
            if (directory != null && directoryAdapter != null) {
                directory.getPdfModels().add(pdfModel);
                directoryAdapter.notifyDataSetChanged();
            }

            if (directory.getPdfModels().size() + directory.getFiles().size() == 1) {
                nothingAvailableTV.setVisibility(View.INVISIBLE);
            }
        }
    }
}