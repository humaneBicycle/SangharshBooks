package com.sangharsh.books.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.sangharsh.books.BuildConfig;
import com.sangharsh.books.FileActivity;
import com.sangharsh.books.PDFDisplay;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.UIUpdateHomeFrag;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.PDFModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class DirectoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder  > {

    Context context;
    Directory directory;
    SangharshBooks sangharshBooks;
    ArrayList<Directory> directories;
    ArrayList<Integer> colors;
    ArrayList<Integer> layers;
    UIUpdateHomeFrag uiUpdaterHomeFrag;

    final int TYPE_FILE = 0;
    final int TYPE_PDF = 1;

    public DirectoryAdapter(Context c, Directory directory, SangharshBooks sangharshBooks,UIUpdateHomeFrag uiUpdateHomeFrag){
        this.context=c;
        this.directory=directory;
        this.sangharshBooks = sangharshBooks;
        this.uiUpdaterHomeFrag = uiUpdateHomeFrag;
        //this.activity = activity;

        inflateColors();
        inflateLayers();
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

//        colors.add(R.color.my_purple);
    }

    private void inflateLayers(){
        if(layers==null) {
            layers = new ArrayList<>();
        }
        layers.add(R.drawable.ic_layer_1);
        layers.add(R.drawable.ic_layer_2);
        layers.add(R.drawable.ic_layer_3);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_FILE){
            View view = LayoutInflater.from(context).inflate(R.layout.file_item,new LinearLayout(context),false);
            return new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.pdf_item,new LinearLayout(context),false);
            return new PDFVIewHolder(view);

        }

//        View view = LayoutInflater.from(context).inflate(R.layout.file_item,null);
//        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //0 to f files, then p pdfs. total = p+f
        //if the type is file, open intent and pass the document id it stores. if not then open pdf
        if(holder instanceof MyViewHolder){
            //it is a file
            ((MyViewHolder) holder).fileNameTextView.setText(directory.getFiles().get(position).getName());
            int randForColor = new Random().nextInt(colors.size());
            ((MyViewHolder) holder).linearLayout.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(colors.get(randForColor))));
            colors.remove(randForColor);
            if(colors.size()==0){
                inflateColors();
            }
            int randForLayer = new Random().nextInt(layers.size());
            ((MyViewHolder) holder).fileItemBG.setBackground(context.getResources().getDrawable(layers.get(randForLayer)));
            layers.remove(randForLayer);
            if(layers.size()==0){
                inflateLayers();
            }

            ((MyViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FileActivity.class);
                    sangharshBooks.addToPath(directory.getFiles().get(position).getName());
                    //sangharshBooks.addStack(directory.getFiles().get(position).getPointingDirId());
                    context.startActivity(intent);
                }
            });


//            ((MyViewHolder) holder).linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    LongClickOptions longClickOptions = new LongClickOptions(sangharshBooks,context,directory.getFiles().get(position),uiUpdaterHomeFrag,DirectoryAdapter.this,position,directory.getFiles());
//                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
//                    longClickOptions.show(manager,"longClickOptionsFile");
//                    return true;
//                }
//            });

        }
        if(holder instanceof PDFVIewHolder){
            int index = position-directory.getFiles().size();


            ((PDFVIewHolder) holder).relativeLayoutBG.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LongClickOptions longClickOptions = new LongClickOptions(sangharshBooks,context,directory.getPdfModels().get(index),uiUpdaterHomeFrag,DirectoryAdapter.this,position,index, directory.getPdfModels());
                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                    longClickOptions.show(manager,"longClickOptionsPDF");
                    return true;
                }
            });

            int randForColor = new Random().nextInt(colors.size());
            ((PDFVIewHolder) holder).llBG.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(colors.get(randForColor))));
            Drawable drawable=context.getResources().getDrawable(R.drawable.tiny_stroke);
            drawable.setTint(context.getResources().getColor(colors.get(randForColor)));
            ((PDFVIewHolder) holder).relativeLayoutBG.setBackgroundDrawable(drawable);
            colors.remove(randForColor);
            if(colors.size()==0){
                inflateColors();
            }
            ((PDFVIewHolder) holder).relativeLayoutBG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAdvPdf(holder,index);
                }
            });
            //ArrayList<PDFModel> pdfModels1 =  new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.BOOKMARKS);
//            for (int i = 0;i<pdfModels1.size();i++){
//                if(directory.getPdfModels().get(index).getPointingDir().equals(pdfModels1.get(i).getPointingDir())){
//                    ((PDFVIewHolder) holder).bookMarkImg.setBackground(context.getDrawable(R.drawable.ic_home_icon));
//                    break;
//                }
//            }
//
//            ((PDFVIewHolder) holder).bookMarkImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(directory.getPdfModels().get(index).isBookMarked()){
//                        ((PDFVIewHolder) holder).bookMarkImg.setBackground(context.getDrawable(R.drawable.ic_profile));
//                        for (int i = 0;i<pdfModels1.size();i++){
//                            if(directory.getPdfModels().get(index).getPointingDir().equals(pdfModels1.get(i).getPointingDir())){
//                                pdfModels1.remove(i);
//                                break;
//                            }
//                        }
//                        new StorageHelper(context).savePDFModel(pdfModels1,StorageHelper.BOOKMARKS);
//
//                    }else{
//                        ((PDFVIewHolder) holder).bookMarkImg.setBackground(context.getDrawable(R.drawable.ic_home_icon));
//                        PDFModel pdfModel1 = directory.getPdfModels().get(index);
//                        pdfModel1.setBookMarked(true);
//                        directory.getPdfModels().get(index).setBookMarked(true);
//                        pdfModels1.add(pdfModel1);
//                        new StorageHelper(context).savePDFModel(pdfModels1,StorageHelper.BOOKMARKS);
//                    }
//
//                }
//            });
            ((PDFVIewHolder) holder).pdfNamepdfItem.setText(directory.getPdfModels().get(index).getName());
            ((PDFVIewHolder) holder).seekBar.setEnabled(false);

//            ((PDFVIewHolder) holder).downloadBTN.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    getAdvPdf(holder,index);
//                }
//            });

//            ((PDFVIewHolder) holder).basicPDFViewer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(false);
//                    ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(false);
//                    Log.d("sba directoryadapter", "onClick: advanced pdf viewer");
//                    boolean b = false;
//                    for(int i =0;i<new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).size();i++){
//                        //directory.getPdfModels().get(position).getPointingDir();
//                        //String s = (new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir());
//                        if(new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir().equals(directory.getPdfModels().get(index).getPointingDir()) ){
//                            //if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf").exists()) {
//                                b = true;
//                                break;
//                            //}
//                        }
//                    }
//                    if(b){
//                        //downloaded file
//                        String dirPath = context.getFilesDir().getAbsolutePath()+"/"+directory.getPdfModels().get(index).getPointingDir()+".pdf";
//                        //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf";
//                        File file = new File(dirPath);
//                        //Log.d("sba adapter", dirPath);
////                        if(!file.exists()){
////                            file.mkdir();
////                        }
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
//                        intent.setDataAndType(uri, "application/pdf");
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//                        context.startActivity(intent);
//                    }else{
//                        //download the file. add it to storage
//                        Log.d("sba pdf onclick", "onClick: not downloaded! initiating download");
//                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
//                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
//                        PRDownloader.initialize(context);
//                        String url = directory.getPdfModels().get(index).getUrl();
//                        //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//                        String dirPath = context.getFilesDir().getAbsolutePath();
////                        if(!new File(dirPath+"/abhay.pdf").exists()){
////                            Log.d("sba add bottomsheet", "directory does not exist");
////                            //new File(dirPath).mkdir();
////                        }
//                        //String fileName = directory.getPdfModels().get(index).getPointingDir();
//                        //Log.d("sba downloading details", "url: "+url +" dirPath " + dirPath+" filename "+fileName );
//                        int downloadId = PRDownloader.download(url, dirPath, directory.getPdfModels().get(index).getPointingDir()+".pdf")
//                                .build()
//                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                                    @Override
//                                    public void onStartOrResume() {
////                                        Toast.makeText(context, "Starting download!", Toast.LENGTH_SHORT).show();
////                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
////                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
//
//                                    }
//                                })
//                                .setOnPauseListener(new OnPauseListener() {
//                                    @Override
//                                    public void onPause() {
//
//                                    }
//                                })
//                                .setOnCancelListener(new OnCancelListener() {
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//                                })
//                                .setOnProgressListener(new OnProgressListener() {
//                                    @Override
//                                    public void onProgress(Progress progress) {
//                                        //Log.d("sba bottom sheet", "download progress"+progress.currentBytes);
//
//                                        int prog = ((int)(progress.currentBytes*100/progress.totalBytes));
//                                        Log.d("sba", "onProgress: "+String.valueOf(prog));
//                                        ((PDFVIewHolder) holder).seekBar.setProgress(prog);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setText(String.valueOf(prog)+"%");
//
//                                    }
//                                })
//                                .start(new OnDownloadListener() {
//                                    @Override
//                                    public void onDownloadComplete() {
//
//                                        ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
//                                        pdfs.add(directory.getPdfModels().get(index));
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
//                                        new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
//                                        Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
////                                        if(pdfs.size()==0){
////                                            pdfs.add(directory.)
////                                        }else {
////                                            pdfs.get(index).setOfflinePath(dirPath);
////                                        }
//                                    }
//
//                                    @Override
//                                    public void onError(Error error) {
//                                        Log.d("sba", "download onError: "+error.getServerErrorMessage()+" connection exception "  +error.getConnectionException());
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
//                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//                                    }
//
//                                });
//
//                    }
//                }
//            });

//            ((PDFVIewHolder) holder).advancedPDFViewer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(false);
//                    ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(false);
//                    boolean b = false;
//                    for(int i =0;i<new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).size();i++){
//                        //directory.getPdfModels().get(position).getPointingDir();
//                        //String s = (new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir());
//                        if(new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir().equals(directory.getPdfModels().get(index).getPointingDir()) ){
//                            //if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf").exists()) {
//                            b = true;
//                            break;
//                            //}
//                        }
//                    }
//                    if(b){
//                        //downloaded file
//                        String dirPath = context.getFilesDir().getAbsolutePath()+"/"+directory.getPdfModels().get(index).getPointingDir()+".pdf";
//                        //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf";
//                        File file = new File(dirPath);
//                        //Log.d("sba adapter", dirPath);
////                        if(!file.exists()){
////                            file.mkdir();
////                        }
//                        sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
//                        context.startActivity(new Intent(context, PDFDisplay.class));
//                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//                    }else{
//                        //download the file. add it to storage
//                        Log.d("sba pdf onclick", "onClick: not downloaded! initiating download");
//                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
//                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
//                        PRDownloader.initialize(context);
//                        String url = directory.getPdfModels().get(index).getUrl();
//                        //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//                        String dirPath = context.getFilesDir().getAbsolutePath();
////                        if(!new File(dirPath+"/abhay.pdf").exists()){
////                            Log.d("sba add bottomsheet", "directory does not exist");
////                            //new File(dirPath).mkdir();
////                        }
//                        //String fileName = directory.getPdfModels().get(index).getPointingDir();
//                        //Log.d("sba downloading details", "url: "+url +" dirPath " + dirPath+" filename "+fileName );
//                        int downloadId = PRDownloader.download(url, dirPath, directory.getPdfModels().get(index).getPointingDir()+".pdf")
//                                .build()
//                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                                    @Override
//                                    public void onStartOrResume() {
//                                        //Toast.makeText(context, "Starting download!", Toast.LENGTH_SHORT).show();
////                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
////                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
//
//                                    }
//                                })
//                                .setOnPauseListener(new OnPauseListener() {
//                                    @Override
//                                    public void onPause() {
//
//                                    }
//                                })
//                                .setOnCancelListener(new OnCancelListener() {
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//                                })
//                                .setOnProgressListener(new OnProgressListener() {
//                                    @Override
//                                    public void onProgress(Progress progress) {
//                                        //Log.d("sba bottom sheet", "download progress"+progress.currentBytes);
//
//                                        int prog = ((int)(progress.currentBytes*100/progress.totalBytes));
//                                        Log.d("sba", "onProgress: "+String.valueOf(prog));
//                                        ((PDFVIewHolder) holder).seekBar.setProgress(prog);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setText(String.valueOf(prog)+"%");
//
//                                    }
//                                })
//                                .start(new OnDownloadListener() {
//                                    @Override
//                                    public void onDownloadComplete() {
//
//                                        ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
//                                        pdfs.add(directory.getPdfModels().get(index));
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
//                                        new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
//                                        Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//
//
////                                        Log.d("sba", "basic pdf view onClick at: "+directory.getPdfModels().get(index).getName());
////                                        sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
////                                        context.startActivity(new Intent(context, PDFDisplay.class));
////                                        ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
////                                        pdfs.add(directory.getPdfModels().get(index));
////                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
////                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
////                                        new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
////                                        Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
////                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
////                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
////                                        if(pdfs.size()==0){
////                                            pdfs.add(directory.)
////                                        }else {
////                                            pdfs.get(index).setOfflinePath(dirPath);
////                                        }
//                                    }
//
//                                    @Override
//                                    public void onError(Error error) {
//                                        Log.d("sba", "download onError: "+error.getServerErrorMessage()+" connection exception "  +error.getConnectionException());
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
//                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//                                    }
//
//                                });
//
//                    }
//
//
//
//                }
//            });



            //TODO set on click listeners. if file not available offline, download it.

        }
    }
    private void getAdvPdf(RecyclerView.ViewHolder holder,int index){
        ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(false);
        boolean b = false;
        for(int i =0;i<new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).size();i++){
            //directory.getPdfModels().get(position).getPointingDir();
            //String s = (new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir());
            if(new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir().equals(directory.getPdfModels().get(index).getPointingDir()) ){
                //if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf").exists()) {
                b = true;
                break;
                //}
            }
        }
        if(b){
            //downloaded file
            String dirPath = context.getFilesDir().getAbsolutePath()+"/"+directory.getPdfModels().get(index).getPointingDir()+".pdf";
            //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf";
            File file = new File(dirPath);
            //Log.d("sba adapter", dirPath);
//                        if(!file.exists()){
//                            file.mkdir();
//                        }
            ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(true);
            sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
            context.startActivity(new Intent(context, PDFDisplay.class));

        }else{
            //download the file. add it to storage
            Log.d("sba pdf onclick", "onClick: not downloaded! initiating download");
            ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
            ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
            PRDownloader.initialize(context);
            String url = directory.getPdfModels().get(index).getUrl();
            //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            String dirPath = context.getFilesDir().getAbsolutePath();
//                        if(!new File(dirPath+"/abhay.pdf").exists()){
//                            Log.d("sba add bottomsheet", "directory does not exist");
//                            //new File(dirPath).mkdir();
//                        }
            //String fileName = directory.getPdfModels().get(index).getPointingDir();
            //Log.d("sba downloading details", "url: "+url +" dirPath " + dirPath+" filename "+fileName );
            int downloadId = PRDownloader.download(url, dirPath, directory.getPdfModels().get(index).getPointingDir()+".pdf")
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            //Toast.makeText(context, "Starting download!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            //Log.d("sba bottom sheet", "download progress"+progress.currentBytes);

                            int prog = ((int)(progress.currentBytes*100/progress.totalBytes));
                            Log.d("sba", "onProgress: "+String.valueOf(prog));
                            ((PDFVIewHolder) holder).seekBar.setProgress(prog);
                            //((PDFVIewHolder) holder).downloadPercentTV.setText(String.valueOf(prog)+"%");

                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {

                            ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
                            pdfs.add(directory.getPdfModels().get(index));
                            ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
                            ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
                            new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
                            Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
                            ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(true);
                            sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
                            context.startActivity(new Intent(context, PDFDisplay.class));


//                                        Log.d("sba", "basic pdf view onClick at: "+directory.getPdfModels().get(index).getName());
//                                        sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
//                                        context.startActivity(new Intent(context, PDFDisplay.class));
//                                        ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
//                                        pdfs.add(directory.getPdfModels().get(index));
//                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
//                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);
//                                        new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
//                                        Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
//                                        ((PDFVIewHolder) holder).basicPDFViewer.setEnabled(true);
//                                        ((PDFVIewHolder) holder).advancedPDFViewer.setEnabled(true);
//                                        if(pdfs.size()==0){
//                                            pdfs.add(directory.)
//                                        }else {
//                                            pdfs.get(index).setOfflinePath(dirPath);
//                                        }
                        }

                        @Override
                        public void onError(Error error) {
                            Log.d("sba", "download onError: "+error.getServerErrorMessage()+" connection exception "  +error.getConnectionException());
                            ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
                            ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);

                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(true);

                        }

                    });
        }
    }


    @Override
    public int getItemCount() {
        if(directory.getFiles()==null && directory.getPdfModels()==null){
            return 0;
        }

        if(directory.getFiles()==null){
            return directory.getPdfModels().size();
        }
        if(directory.getPdfModels()==null){
            return directory.getFiles().size();
        }
       return directory.getFiles().size()+directory.getPdfModels().size();
        //return directories.size();
    }

    @Override
    public int getItemViewType(int position){
        //directories.size();
        if(position < directory.getFiles().size()){
            return TYPE_FILE;
        }

        if(position - directory.getFiles().size() < directory.getPdfModels().size()){
            return TYPE_PDF;
        }

        return directories.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{//file view holder
        TextView fileNameTextView;
        CardView fileBg;
        LinearLayout linearLayout;
        ImageView fileItemBG;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.book_item_name);
            fileBg=itemView.findViewById(R.id.background_file_item);
            linearLayout = itemView.findViewById(R.id.file_item_background);
            fileItemBG = itemView.findViewById(R.id.book_item_holder_imageview);
        }
    }
    public class PDFVIewHolder extends RecyclerView.ViewHolder{
        TextView pdfNamepdfItem;
        TextView advancedPDFViewer, basicPDFViewer;
        CardView relativeLayoutBG;
        SeekBar seekBar;
        ProgressBar downloadPercentTV;
        ImageView bookMarkImg;
        LinearLayout llBG;
        AppCompatButton downloadBTN;

        public PDFVIewHolder(View itemView){
            super(itemView);
            pdfNamepdfItem = itemView.findViewById(R.id.pdf_name_pdf_item);
//            advancedPDFViewer = itemView.findViewById(R.id.pdf_view_advance);
//            basicPDFViewer = itemView.findViewById(R.id.pdf_view_basic);
            relativeLayoutBG = itemView.findViewById(R.id.pdf_item_background);
            seekBar = itemView.findViewById(R.id.pdf_item_download_seekbar);
            downloadPercentTV = itemView.findViewById(R.id.download_percent);
            //bookMarkImg = itemView.findViewById(R.id.is_bookmarked_pdf_item);
            llBG = itemView.findViewById(R.id.ll);
//            downloadBTN= itemView.findViewById(R.id.download_pdf_item);

        }
    }

}
