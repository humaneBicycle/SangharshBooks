package com.sangharsh.books.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangharsh.books.DirectoryChangeListener;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class AddDirectoryBottomSheetAdapter extends BottomSheetDialogFragment {
    private static final int PICK_PDF_REQUEST = 1111;
    Context context;
    AppCompatSpinner typeSpinner;
    EditText pdfName, fileName;
    AppCompatButton btSelect;
    AppCompatButton addButton;
    SangharshBooks sangharshBooks;
    TextView pathBottomSheet;
    DirectoryChangeListener callback;
    Directory directory;
    Activity activity;
    Uri mPdfFile;
    private StorageReference storageReference;
    ProgressDialog progressDialog;

    public AddDirectoryBottomSheetAdapter(Context context, SangharshBooks sangharshBooks, Activity activity, DirectoryChangeListener callback){
        this.context = context;
        this.sangharshBooks = sangharshBooks;
        this.activity=activity;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_bottom_sheet,container);

        //modeSpinner = v.findViewById(R.id.mode_selector_bottom_style_spinner);
        typeSpinner = v.findViewById(R.id.type_selector_bottom_style_spinner);
        pdfName = v.findViewById(R.id.pdf_name_edittext);
//        pdfURL = v.findViewById(R.id.pdf_url_edittext);
        fileName = v.findViewById(R.id.file_name_edittext);
        addButton = v.findViewById(R.id.bottom_sheet_add_button);
        pathBottomSheet = v.findViewById(R.id.path_bottom_sheet);
        btSelect = v.findViewById(R.id.add_pdf_local_storage);
//        seekBar = v.findViewById(R.id.upload_progress);
        progressDialog = new ProgressDialog(activity);

//        String[] items = new String[]{"Parent Directory","Any Other Directory"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,items);
//        modeSpinner.setAdapter(adapter);

        String[] type = new String[]{"File","PDF"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,type);
        typeSpinner.setAdapter(adapter1);

        pathBottomSheet.setText(sangharshBooks.path);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){//file
                    pdfName.setVisibility(View.GONE);
                    //pdfURL.setVisibility(View.GONE);
                    fileName.setVisibility(View.VISIBLE);
                    btSelect.setVisibility(View.GONE);
                }else if(i==1){//pdf
                    pdfName.setVisibility(View.VISIBLE);
                    //pdfURL.setVisibility(View.VISIBLE);
                    fileName.setVisibility(View.GONE);
                    btSelect.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(typeSpinner.getSelectedItem().equals("File")){
                    //file hai bawa
                    if(!fileName.getText().toString().equals("") && fileName.getText()!=null){
                        //directory = sangharshBooks.getLastActiveDirectory();

//                        if(directory==null){
//                            directory=new Directory(0,new ArrayList<>(),new ArrayList<>(),sangharshBooks.path);
//                        }
                        //l.setDepth(1);
                        //directory.getFiles().add(fileModel);
                        //Log.d("sba", "path while adding "+sangharshBooks.getPath());
                        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",sangharshBooks.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(!task.getResult().isEmpty()) {
                                        directory = task.getResult().toObjects(Directory.class).get(0);
                                        FileModel fileModel = new FileModel(fileName.getText().toString(),sangharshBooks.getPath()+"\\"+fileName.getText().toString());

                                        String id = task.getResult().getDocuments().get(0).getId();
                                        boolean b=false;
                                        for(int i =0;i<directory.getFiles().size();i++){
                                            if(fileName.getText().toString().equals(directory.getFiles().get(i).getName())){
                                                b=true;
                                                Log.d("sba bottomsheet", "filename "+fileName.getText().toString()+" directory "+ directory.getFiles().get(i).getName());
                                            }
                                        }
                                        directory.getFiles().add(fileModel);
                                        //Log.d("sba bottomsheet", "onComplete: checked for falseness"+ b);
                                        if(b){
                                            Toast.makeText(context, "This name already exists!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            FirebaseFirestore.getInstance().collection("directory").document(id).set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Successfully added file!", Toast.LENGTH_SHORT).show();
                                                        callback.onFileModelAdded(fileModel);
                                                        dismiss();
                                                    } else {
                                                        Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                                        Log.d("sba", "onComplete: " + task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    }else{
                                        ArrayList<FileModel> fileModels = new ArrayList<>();
                                        FileModel fileModel = new FileModel(fileName.getText().toString(),sangharshBooks.getPath()+"\\"+fileName.getText().toString());
                                        fileModels.add(fileModel);
                                        directory = new Directory(0,fileModels,new ArrayList<>(),sangharshBooks.getPath());
                                        //directory.getFiles().add(fileModel);
                                        FirebaseFirestore.getInstance().collection("directory").document().set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Successfully added file!", Toast.LENGTH_SHORT).show();
                                                    //callback.Callback();
                                                    callback.onFileModelAdded(fileModel);
                                                    //Log.d("sba", "onComplete: noooooo");
                                                    dismiss();
                                                } else {
                                                    Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                                    Log.d("sba", "onComplete: " + task.getException());
                                                }
                                            }
                                        });

                                    }


                                }
                            }
                        });


                    }


                }else if (typeSpinner.getSelectedItem().equals("PDF")){
                    //pdf hai bawa
                    if(!pdfName.getText().toString().isEmpty() && mPdfFile!=null){
                        Log.d("sba pdf from local storage", "onClick: "+mPdfFile.getPath());
                        //seekBar.setVisibility(View.VISIBLE);
                        progressDialog.setMessage("Uploading");
                        progressDialog.show();
                        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",sangharshBooks.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    directory = task.getResult().toObjects(Directory.class).get(0);
                                    String id = task.getResult().getDocuments().get(0).getId();
                                    boolean b = false;
                                    storageReference= FirebaseStorage.getInstance().getReference();
                                    StorageReference sr= storageReference.child(sangharshBooks.getPath()+"\\"+pdfName.getText().toString()+".pdf");
                                    for(int i = 0; i<directory.getPdfModels().size();i++){
                                        if(directory.getPdfModels().get(i).getPointingDir().equals(sangharshBooks.getPath()+"\\"+pdfName.getText().toString())){
                                            b=true;
                                            break;
                                        }
                                    }
                                    if(b){
                                        Toast.makeText(context, "PDF with this name exists!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        UploadTask uploadTask = sr.putFile(mPdfFile);
                                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if(!task.isSuccessful()){
                                                    throw task.getException();
                                                }
                                                return sr.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                //seekBar.setVisibility(View.GONE);
                                                progressDialog.dismiss();
                                                if(task.isSuccessful()){
                                                    String url = task.getResult().toString();
                                                    Log.d("sba download url", "onComplete: "+url);
//                                                    String url = task.getResult().getUploadSessionUri().toString();
                                                    PDFModel pdfModel = new PDFModel(false,url,"",pdfName.getText().toString(),sangharshBooks.getPath()+"\\"+pdfName.getText().toString(),false);
                                                    directory.getPdfModels().add(pdfModel);
                                                    FirebaseFirestore.getInstance().collection("directory").document(id).set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(context, "PDF added successfully!", Toast.LENGTH_SHORT).show();
                                                                callback.onPDFModelAdded(pdfModel);
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                    Log.d("sba pdf from local storage", "onClick: error in setting directory"+task.getException());
                                                }
                                            }
                                        });
//                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                                            @Override
//                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                                                int prog = (int) (snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
//                                                seekBar.setProgress(prog*100);
//                                            }
//                                        })

                                    }


                                }else{
                                    Log.d("sba pdf from local storage", "onClick: fail error"+task.getException());
                                }
                            }
                        });
//                    }else if(!pdfName.getText().toString().isEmpty()){
//                        Log.d("sba pdf from url", "onClick: ");
//                        FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path",sangharshBooks.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if(task.isSuccessful()){
//                                    if(!task.getResult().isEmpty()){
//                                        //task.getResult().getDocuments().get(0).getId();
//                                        directory = task.getResult().toObjects(Directory.class).get(0);
//                                        String id = task.getResult().getDocuments().get(0).getId();
//                                        boolean b = false;
//                                        for(int i = 0; i<directory.getPdfModels().size();i++){
//                                            if(directory.getPdfModels().get(i).getName().equals(pdfName.getText().toString())){
//                                                b=true;
//                                                break;
//                                            }
//                                        }
//
//                                        if(b){
//                                            Toast.makeText(context, "PDF Already exists!", Toast.LENGTH_SHORT).show();
//                                        }else{
////                                            PDFModel pdfModel = new PDFModel(false,pdfURL.getText().toString(),"",pdfName.getText().toString(),sangharshBooks.getPath()+"\\"+pdfName.getText().toString(),false);
////                                            directory.getPdfModels().add(pdfModel);
////                                            FirebaseFirestore.getInstance().collection("directory").document(id).set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                @Override
////                                                public void onComplete(@NonNull Task<Void> task) {
////                                                    if(task.isSuccessful()){
////                                                        Toast.makeText(context, "PDF added successfully!", Toast.LENGTH_SHORT).show();
////                                                        callback.update();
////                                                    }else{
////                                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
////                                                    }
////                                                }
////                                            });
//                                        }
//                                    }else {
//                                        Log.d("aba addbottomsheet", "onComplete: pdf task is empty");
//                                        FirebaseFirestore.getInstance().collection("directory").document().set(directory).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(context, "Successfully added file!", Toast.LENGTH_SHORT).show();
//                                                    //callback.update();
//                                                    //Log.d("sba", "onComplete: noooooo");
//                                                    dismiss();
//                                                } else {
//                                                    Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
//                                                    Log.d("sba", "onComplete: " + task.getException());
//                                                }
//                                            }
//                                        });
//                                    }
//                                }else{
//                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
                    }
                    else{
                        Toast.makeText(context, "Please complete the form!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        resultLauncher = registerForActivityResult(
//                new ActivityResultContracts
//                        .StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(
//                            ActivityResult result)
//                    {
//                        // Initialize result data
//                        Intent data = result.getData();
//                        // check condition
//                        if (data != null) {
//                            // When data is not equal to empty
//                            // Get PDf uri
//                            Uri sUri = data.getData();
//                            // set Uri on text view
//                            // Get PDF path
//                            String sPath = sUri.getPath();
//                            // Set path on text view
//                            //File file = new File(sPath);
//                            mPdfFile = new File(sPath);
//
//                        }
//                    }
//                });


        btSelect.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v)
                    {
                        selectPDF();
                        // check condition
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                            // When permission is not granted
                            // Result permission
                            Log.d("sbarequesting perm", "onClick: ");
                            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
                        }
                        else {
                            Log.d("sba requesting perm", "onClick: ");
                            // When permission is granted
                            // Create method
                            selectPDF();
                        }
                    }
                });


        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            selectPDF();
        }
        else {
            // When permission is denied
            // Display toast
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a pdf"),PICK_PDF_REQUEST);

        //resultLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_PDF_REQUEST && data!=null && data.getData()!=null){
            mPdfFile = data.getData();
            Log.d("sba activity res", "onActivityResult: "+data.toString()+" uri "+ mPdfFile);

        }
    }
}
