package com.sangharsh.books.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import androidx.cardview.widget.CardView;
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
import com.sangharsh.books.StartTest;
import com.sangharsh.books.FileActivity;
import com.sangharsh.books.PDFDisplay;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;
import java.util.Random;

public class DirectoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder  > {

    Context context;
    Directory directory;
    SangharshBooks sangharshBooks;
    ArrayList<Integer> colors;
    ArrayList<Integer> layers;
    UIUpdateHomeFrag uiUpdaterHomeFrag;

    final int TYPE_FILE = 0;
    final int TYPE_PDF = 1;
    final int TYPE_TEST = 2;

    public DirectoryAdapter(Context c, Directory directory, SangharshBooks sangharshBooks,UIUpdateHomeFrag uiUpdateHomeFrag){
        this.context=c;
        this.directory=directory;
        this.sangharshBooks = sangharshBooks;
        this.uiUpdaterHomeFrag = uiUpdateHomeFrag;

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
        }else if(viewType==TYPE_PDF){
            View view = LayoutInflater.from(context).inflate(R.layout.pdf_item,new LinearLayout(context),false);
            return new PDFVIewHolder(view);

        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.file_item,new LinearLayout(context),false);
            return new TestHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                    context.startActivity(intent);
                }
            });

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
            ((PDFVIewHolder) holder).pdfNamepdfItem.setText(directory.getPdfModels().get(index).getName());
            ((PDFVIewHolder) holder).seekBar.setEnabled(false);

        }
        if(holder instanceof TestHolder){
            int index = position-directory.getFiles().size()-directory.getPdfModels().size();
            ((TestHolder)holder).fileNameTextView.setText(directory.getTests().get(index).getTitle());
            ((TestHolder)holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, StartTest.class).putExtra("testId",directory.getTests().get(index).getId()));

                }
            });
        }
    }
    private void getAdvPdf(RecyclerView.ViewHolder holder,int index){
        ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(false);
        boolean b = false;
        for(int i =0;i<new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).size();i++){
            if(new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir().equals(directory.getPdfModels().get(index).getPointingDir()) ){
                b = true;
                break;

            }
        }
        if(b){
            ((PDFVIewHolder) holder).relativeLayoutBG.setEnabled(true);
            sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
            context.startActivity(new Intent(context, PDFDisplay.class));
        }else{
            Log.i("sba pdf onclick", "onClick: not downloaded! initiating download");
            ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
            ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
            PRDownloader.initialize(context);
            String url = directory.getPdfModels().get(index).getUrl();
            String dirPath = context.getFilesDir().getAbsolutePath();
            int downloadId = PRDownloader.download(url, dirPath, directory.getPdfModels().get(index).getPointingDir()+".pdf")
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

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

                            int prog = ((int)(progress.currentBytes*100/progress.totalBytes));
                            Log.i("sba", "onProgress: "+String.valueOf(prog));
                            ((PDFVIewHolder) holder).seekBar.setProgress(prog);

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
        if(directory.getFiles()==null && directory.getPdfModels()==null && directory.getTests()==null){
            return 0;
        }

        if(directory.getFiles()==null){
            return directory.getPdfModels().size()+directory.getTests().size();
        }
        if(directory.getPdfModels()==null){
            return directory.getFiles().size()+directory.getTests().size();
        }
        if(directory.getTests()==null){
            return directory.getFiles().size()+directory.getPdfModels().size();
        }
       return directory.getFiles().size()+directory.getPdfModels().size()+directory.getTests().size();
    }

    @Override
    public int getItemViewType(int position){
            Log.i("abh", "getItemViewType: pos: "+position);
        if(position < directory.getFiles().size()){
            return TYPE_FILE;
        }
        if(position - directory.getFiles().size() < directory.getPdfModels().size()){
            return TYPE_PDF;
        }else{
            return TYPE_TEST;
        }
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
        CardView relativeLayoutBG;
        SeekBar seekBar;
        ProgressBar downloadPercentTV;
        LinearLayout llBG;

        public PDFVIewHolder(View itemView){
            super(itemView);
            pdfNamepdfItem = itemView.findViewById(R.id.pdf_name_pdf_item);
            relativeLayoutBG = itemView.findViewById(R.id.pdf_item_background);
            seekBar = itemView.findViewById(R.id.pdf_item_download_seekbar);
            downloadPercentTV = itemView.findViewById(R.id.download_percent);
            llBG = itemView.findViewById(R.id.ll);

        }
    }
    public class TestHolder extends RecyclerView.ViewHolder{
        TextView fileNameTextView;
        CardView fileBg;
        LinearLayout linearLayout;
        ImageView fileItemBG;

        public TestHolder(View itemView){
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.book_item_name);
            fileBg=itemView.findViewById(R.id.background_file_item);
            linearLayout = itemView.findViewById(R.id.file_item_background);
            fileItemBG = itemView.findViewById(R.id.book_item_holder_imageview);
        }
    }

}
