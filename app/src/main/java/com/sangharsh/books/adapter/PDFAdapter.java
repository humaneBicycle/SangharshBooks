package com.sangharsh.books.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.sangharsh.books.BuildConfig;
import com.sangharsh.books.PDFDisplay;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.model.PDFModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.MyViewHolder> {

    Context context;
    ArrayList<PDFModel> pdfModels;
    SangharshBooks sangharshBooks;
    String mode;
    ArrayList<Integer> colors;


    public PDFAdapter (Application application, Context context, ArrayList<PDFModel> pdfModels,String mode){
        this.context = context;
        this.pdfModels = pdfModels;
        this.sangharshBooks = (SangharshBooks) application;
        this.mode = mode;
        inflateColors();
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

        int randForColor = new Random().nextInt(colors.size());
        holder.llBG.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(colors.get(randForColor))));
        Drawable drawable=context.getResources().getDrawable(R.drawable.tiny_stroke);
        drawable.setTint(context.getResources().getColor(colors.get(randForColor)));
        holder.cardView.setBackgroundDrawable(drawable);
        colors.remove(randForColor);
        if(colors.size()==0){
            inflateColors();
        }

        holder.basicViewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dirPath = context.getFilesDir().getAbsolutePath()+"/"+pdfModels.get(position).getPointingDir()+".pdf";
                //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+directory.getPdfModels().get(index).getName()+".pdf";
                File file = new File(dirPath);
                //Log.d("sba adapter", dirPath);
//                        if(!file.exists()){
//                            file.mkdir();
//                        }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sangharshBooks.setActivePdfModel(pdfModels.get(position));
                context.startActivity(new Intent(context, PDFDisplay.class));
            }
        });
        holder.advancedViewTV.setOnClickListener(new View.OnClickListener() {
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
//                    pdfModels.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position,pdfModels.size());


                    return true;
                }
            });
        }

//        holder.appCompatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PRDownloader.initialize(context);
//                String url = pdfModels.get(position).getUrl();
//                //String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//                String dirPath = context.getFilesDir().getAbsolutePath();
////                        if(!new File(dirPath+"/abhay.pdf").exists()){
////                            Log.d("sba add bottomsheet", "directory does not exist");
////                            //new File(dirPath).mkdir();
////                        }
//                //String fileName = directory.getPdfModels().get(index).getPointingDir();
//                //Log.d("sba downloading details", "url: "+url +" dirPath " + dirPath+" filename "+fileName );
//                int downloadId = PRDownloader.download(url, dirPath, pdfModels.get(position).getPointingDir()+".pdf")
//                        .build()
//                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                            @Override
//                            public void onStartOrResume() {
//                                //Toast.makeText(context, "Starting download!", Toast.LENGTH_SHORT).show();
////                                        ((PDFVIewHolder) holder).seekBar.setVisibility(View.VISIBLE);
////                                        ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.VISIBLE);
//
//                            }
//                        })
//                        .setOnPauseListener(new OnPauseListener() {
//                            @Override
//                            public void onPause() {
//
//                            }
//                        })
//                        .setOnCancelListener(new OnCancelListener() {
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        })
//                        .setOnProgressListener(new OnProgressListener() {
//                            @Override
//                            public void onProgress(Progress progress) {
//                                //Log.d("sba bottom sheet", "download progress"+progress.currentBytes);
//
//                                int prog = ((int)(progress.currentBytes*100/progress.totalBytes));
//                                Log.d("sba", "onProgress: "+String.valueOf(prog));
//                                holder.seekBar.setProgress(prog);
//                                holder.downloadPercentTV.setText(String.valueOf(prog)+"%");
//
//                            }
//                        })
//                        .start(new OnDownloadListener() {
//                            @Override
//                            public void onDownloadComplete() {
//
//                                ArrayList<PDFModel> pdfs = new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
//                                pdfs.add(pdfModels.get(position));
//                                holder.seekBar.setVisibility(View.GONE);
//                                holder.downloadPercentTV.setVisibility(View.GONE);
//                                new StorageHelper(context).savePDFModel(pdfs,StorageHelper.DOWNLOADED);
//                                Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
//                                holder.basicPDFViewer.setEnabled(true);
//                                holder.advancedPDFViewer.setEnabled(true);
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
//                            }
//
//                            @Override
//                            public void onError(Error error) {
//                                Log.d("sba", "download onError: "+error.getServerErrorMessage()+" connection exception "  +error.getConnectionException());
//                                holder.seekBar.setVisibility(View.GONE);
//                                holder.downloadPercentTV.setVisibility(View.GONE);
//                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                holder.basicPDFViewer.setEnabled(true);
//                                holder.advancedPDFViewer.setEnabled(true);
//                            }
//
//                        });
//
//            }
//
//        });
        
    }

    @Override
    public int getItemCount() {
        return pdfModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pdfName;
        TextView advancedViewTV;
        TextView basicViewTV;
        CardView cardView;
        LinearLayout backgroundLL;
        ImageView igmPdf;
        AppCompatButton appCompatButton;
        SeekBar seekBar;
        TextView downloadPercentTV;

        LinearLayout llBG;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfName = itemView.findViewById(R.id.pdf_name_pdf_item);
            advancedViewTV = itemView.findViewById(R.id.pdf_view_advance);
            basicViewTV = itemView.findViewById(R.id.pdf_view_basic);
            cardView = itemView.findViewById(R.id.pdf_item_background);
            backgroundLL = itemView.findViewById(R.id.ll);
            igmPdf=itemView.findViewById(R.id.img_pdf);
//            appCompatButton = itemView.findViewById(R.id.download_pdf_item);
            seekBar = itemView.findViewById(R.id.pdf_item_download_seekbar);
            downloadPercentTV = itemView.findViewById(R.id.download_percent);
            llBG = itemView.findViewById(R.id.ll);
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
