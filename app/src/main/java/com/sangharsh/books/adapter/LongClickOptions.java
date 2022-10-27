package com.sangharsh.books.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class LongClickOptions extends BottomSheetDialogFragment {
    SangharshBooks sangharshBooks;
    Context context;
    AppCompatButton deleteButton, bookmarkButton;
    FileModel fileModel;
    PDFModel pdfModel;
    boolean b;
    ArrayList<PDFModel> bookmarkedPdfs;
    int j;
    UIUpdateHomeFrag uiUpdateHomeFrag;
    int pos, index;
    ArrayList<PDFModel> pdfModels;
    ArrayList<FileModel> fileModels;
    DirectoryAdapter adapter;


    public LongClickOptions(SangharshBooks sangharshBooks, Context context,FileModel fileModel,UIUpdateHomeFrag uiUpdateHomeFrag,DirectoryAdapter adapter,int pos,ArrayList<FileModel> fileModels){
        this.sangharshBooks = sangharshBooks;
        this.context = context;
        this.fileModel = fileModel;
        this.uiUpdateHomeFrag = uiUpdateHomeFrag;
        this.pos=pos;
        this.index = index;
        this.fileModels = fileModels;
        this.adapter = adapter;
    }
    public LongClickOptions(SangharshBooks sangharshBooks, Context context, PDFModel pdfModel,UIUpdateHomeFrag uiUpdateHomeFrag,DirectoryAdapter adapter,int pos,int index,ArrayList<PDFModel> pdfModels){
        this.sangharshBooks = sangharshBooks;
        this.context = context;
        this.pdfModel = pdfModel;
        this.uiUpdateHomeFrag = uiUpdateHomeFrag;
        this.pos=pos;
        this.index = index;
        this.pdfModels = pdfModels;
        this.adapter=adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.long_click_options,container);

//        deleteButton = v.findViewById(R.id.delete);



        if(getTag().equals("longClickOptionsPDF")){

            bookmarkButton = v.findViewById(R.id.bookmark_long_click_options);
            bookmarkButton.setVisibility(View.VISIBLE);
            bookmarkedPdfs =  new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.BOOKMARKS);
            b=false;
            for(int i =0;i<bookmarkedPdfs.size();i++){
                if(bookmarkedPdfs.get(i).getPointingDir().equals(pdfModel.getPointingDir())){
                    b=true;
                    j=i;
                    break;
                }
            }
            if(b){
                //this pdf is a bookmark
                bookmarkButton.setText("Remove from Bookmark");
            }

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(b){
                        //pdf is a bookmark
                        bookmarkedPdfs.remove(j);
                        new StorageHelper(context).savePDFModel(bookmarkedPdfs,StorageHelper.BOOKMARKS);
                        bookmarkButton.setText(R.string.bookmark);
                    }else{
                        //not add
                        bookmarkedPdfs.add(pdfModel);
                        new StorageHelper(context).savePDFModel(bookmarkedPdfs,StorageHelper.BOOKMARKS);
                        bookmarkButton.setText("Remove from Bookmark");
                    }
                }
            });

//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("sba long click", "onCreateView: "+ getTag());
//
//                        deletePDF();
//                        deleteButton.setEnabled(false);
//
//
//                }
//            });
        }



        return v;
    }

    private void deleteFile(String pathOfFile) {
        Log.d("sba del operation", "finc entry deleteFilePath: "+pathOfFile);
        if(sangharshBooks.getPath().equals(getParentDirectoryPath(pathOfFile))){
            FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",getParentDirectoryPath(pathOfFile)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            Directory parentDirectory = task.getResult().toObjects(Directory.class).get(0);
                            for(int i =0;i<parentDirectory.getFiles().size();i++){
                                if(parentDirectory.getFiles().get(i).getPointingDirId().equals(fileModel.getPointingDirId())){
                                    parentDirectory.getFiles().remove(i);
                                    String idToDelete=task.getResult().getDocuments().get(0).getId();
                                    FirebaseFirestore.getInstance().collection("directory").document().set(parentDirectory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FirebaseFirestore.getInstance().collection("directory").document(idToDelete).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            fileModels.remove(pos);
                                                            adapter.notifyDataSetChanged();
                                                            dismiss();
                                                            Log.d("sbe delete operation", "onComplete: file deleted from arraylist of parent dir");
                                                        }else{
                                                            return;
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    }
                }
            });
        }
        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",pathOfFile).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        Directory directory = task.getResult().toObjects(Directory.class).get(0);
                        for (int i = 0; i < directory.getFiles().size(); i++) {
                            Log.d("sba delete operation", "deleting at path " + directory.getFiles().get(i).getPointingDirId());
                            deleteFile(directory.getFiles().get(i).getPointingDirId());
                        }
                        String docid = task.getResult().getDocuments().get(0).getId();
                        Log.d("sba delete operation", "deleting directory with id " + docid);
                        FirebaseFirestore.getInstance().collection("directory").document(docid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Log.d("sba del operation", "onComplete: with path "+pathOfFile);
                                }
                            }
                        });
                    }else{
                        Log.d("sba del operation", "this file " +pathOfFile+" was not createed yet");
                    }
                }else{
                    Toast.makeText(context, "Deletion Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deletePDF() {
        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",sangharshBooks.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Directory directory = task.getResult().toObjects(Directory.class).get(0);
                    for(int i =0;i<directory.getPdfModels().size();i++){
                        if(directory.getPdfModels().get(i).getPointingDir().equals(pdfModel.getPointingDir())){
                            ArrayList<PDFModel> bookmarks = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.BOOKMARKS);
                            for (int k=0;k<bookmarks.size();k++){
                                if(directory.getPdfModels().get(i).getPointingDir().equals(bookmarks.get(k).getPointingDir())){
                                    bookmarks.remove(k);
                                    new StorageHelper(context).savePDFModel(bookmarks,StorageHelper.BOOKMARKS);
                                }
                            }
                            ArrayList<PDFModel> downloads = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
                            for (int k=0;k<downloads.size();k++){
                                if(directory.getPdfModels().get(i).getPointingDir().equals(downloads.get(k).getPointingDir())){
                                    downloads.remove(k);
                                    new StorageHelper(context).savePDFModel(downloads,StorageHelper.DOWNLOADED);
                                }
                            }

                            directory.getPdfModels().remove(i);
                            FirebaseFirestore.getInstance().collection("directory").document(task.getResult().getDocuments().get(0).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseFirestore.getInstance().collection("directory").document().set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                                    uiUpdateHomeFrag.update();
                                                    pdfModels.remove(index);
                                                    adapter.notifyItemRemoved(index);
                                                    adapter.notifyDataSetChanged();
                                                    dismiss();

                                                }else{
                                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        break;
                        }
                    }


                }else{
                    Toast.makeText(context , "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String getParentDirectoryPath(String k){
        String s=k;
        for(int i = s.length()-1;i>=0;i--){
            if('\\'==s.charAt(i)){
                s = s.substring(0,i);

                break;
            }
        }
        return s;

    }


}
