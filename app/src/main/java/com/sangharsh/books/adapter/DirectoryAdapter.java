package com.sangharsh.books.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sangharsh.books.BuyCoinsActivity;
import com.sangharsh.books.LiveData.UserData;
import com.sangharsh.books.StartTest;
import com.sangharsh.books.FileActivity;
import com.sangharsh.books.PDFDisplay;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.PDFModel;
import com.sangharsh.books.model.ShortTest;
import com.sangharsh.books.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder  > {

    Context context;
    Directory directory;
    SangharshBooks sangharshBooks;
//    ArrayList<Integer> colors;
//    ArrayList<Integer> layers;
    UIUpdateHomeFrag uiUpdaterHomeFrag;
    ArrayList<NativeAd> nativeAds;
    boolean isAdLoading=false;

    final int TYPE_FILE = 0;
    final int TYPE_PDF = 1;
    final int TYPE_TEST = 2;
    final int AD_TYPE = 3;

    public DirectoryAdapter(Context c, Directory directory, SangharshBooks sangharshBooks,UIUpdateHomeFrag uiUpdateHomeFrag){


        this.context=c;
        this.directory=directory;
        nativeAds=new ArrayList<>();
        loadNativeAds();
        ArrayList<PDFModel> pdfModels = directory.getPdfModels();
        ArrayList<ShortTest> tests = directory.getTests();
        ArrayList<FileModel> files = directory.getFiles();
        ArrayList<PDFModel> newPdfModels=new ArrayList<PDFModel>();
        for(int i =0;i<pdfModels.size();i++){
            if(i%3==0 && i!=0){
                newPdfModels.add(null);
            }
            newPdfModels.add(pdfModels.get(i));
        }
        ArrayList<ShortTest> newShortTestModel=new ArrayList<>();
        for(int i =newPdfModels.size();i<newPdfModels.size()+tests.size();i++){
            if(i%3==0 && i!=0){
                newShortTestModel.add(null);
            }
            newShortTestModel.add(tests.get(i-newPdfModels.size()));
        }
        ArrayList<FileModel> newFiles=new ArrayList<>();
        for(int i =newPdfModels.size()+tests.size();i<newPdfModels.size()+tests.size()+files.size();i++){
            if(i%3==0 && i!=0){
                newFiles.add(null);
            }
            newFiles.add(files.get(i-newPdfModels.size()-tests.size()));
        }
        this.directory.setPdfModels(newPdfModels);
        this.directory.setTests(newShortTestModel);
        this.directory.setFiles(newFiles);

        this.sangharshBooks = sangharshBooks;
        this.uiUpdaterHomeFrag = uiUpdateHomeFrag;
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
        }else if(viewType==TYPE_TEST){
            View view = LayoutInflater.from(context).inflate(R.layout.test_item,new LinearLayout(context),false);
            return new TestHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.ad_layout,new LinearLayout(context),false);
            return new AdViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder instanceof MyViewHolder){
            //it is a file

            FileModel file = directory.getFiles().get(position);
            ((MyViewHolder) holder).fileNameTextView.setText(file.getName());

            if (file.isPaid() || file.getPrice() > 0){
                Log.i("Buying: isPaid", "yes");
                ((MyViewHolder) holder).openTxt.setVisibility(View.GONE);
                ((MyViewHolder) holder).lockLayout.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).priceTxt.setText(file.getPrice()+" Coins");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchPurchaseFlow(file.getPointingDirId(), FileModel.class, file.getPrice());
                    }
                });
                UserData.getUserLiveData().observe((LifecycleOwner) context, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user.getPurchasedDirectories() != null
                        && user.getPurchasedDirectories().contains(file.getPointingDirId())){
                            ((MyViewHolder) holder).openTxt.setVisibility(View.VISIBLE);
                            ((MyViewHolder) holder).lockLayout.setVisibility(View.GONE);
                            ((MyViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, FileActivity.class);
                                    sangharshBooks.addToPath(directory.getFiles().get(position).getName());
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                });
            } else {
                ((MyViewHolder) holder).openTxt.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).lockLayout.setVisibility(View.GONE);
                ((MyViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, FileActivity.class);
                        sangharshBooks.addToPath(directory.getFiles().get(position).getName());
                        context.startActivity(intent);
                    }
                });
            }

        }
        if(holder instanceof PDFVIewHolder){
            int index = position-directory.getFiles().size();

            ((PDFVIewHolder) holder).pdfNamepdfItem.setText(directory.getPdfModels().get(index).getName());
            PDFModel file = directory.getPdfModels().get(index);

            ((PDFVIewHolder) holder).root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LongClickOptions longClickOptions = new LongClickOptions(sangharshBooks, context, directory.getPdfModels().get(index), uiUpdaterHomeFrag, DirectoryAdapter.this, position, index, directory.getPdfModels());
                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    longClickOptions.show(manager, "longClickOptionsPDF");
                    return true;
                }
            });

            if (file.isPaid() || file.getPrice() > 0){
                Log.i("Buying: isPaid", "yes");
                ((PDFVIewHolder) holder).openTxt.setVisibility(View.GONE);
                ((PDFVIewHolder) holder).lockLayout.setVisibility(View.VISIBLE);
                ((PDFVIewHolder) holder).priceTxt.setText(file.getPrice()+" Coins");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchPurchaseFlow(file.getPointingDir(), PDFModel.class, file.getPrice());
                    }
                });
                UserData.getUserLiveData().observe((LifecycleOwner) context, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user.getPurchasedPDFs() != null
                                && user.getPurchasedPDFs().contains(file.getPointingDir())){
                            ((PDFVIewHolder) holder).openTxt.setVisibility(View.VISIBLE);
                            ((PDFVIewHolder) holder).lockLayout.setVisibility(View.GONE);
                            ((PDFVIewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getAdvPdf(holder, index);
                                }
                            });
                        }
                    }
                });
            } else {
                ((PDFVIewHolder) holder).openTxt.setVisibility(View.VISIBLE);
                ((PDFVIewHolder) holder).lockLayout.setVisibility(View.GONE);
                ((PDFVIewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getAdvPdf(holder, index);
                    }
                });
            }
        }
        if(holder instanceof TestHolder){
            int index = position-directory.getFiles().size()-directory.getPdfModels().size();
            ((TestHolder)holder).fileNameTextView.setText(directory.getTests().get(index).getTitle());

            ShortTest file = directory.getTests().get(index);

            if (file.isPaid() || file.getPrice() > 0){
                Log.i("Buying: isPaid", "yes");
                ((TestHolder) holder).openTxt.setVisibility(View.GONE);
                ((TestHolder) holder).lockLayout.setVisibility(View.VISIBLE);
                ((TestHolder) holder).priceTxt.setText(file.getPrice()+" Coins");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchPurchaseFlow(file.getId(), ShortTest.class, file.getPrice());
                    }
                });
                UserData.getUserLiveData().observe((LifecycleOwner) context, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user.getPurchasedQuiz() != null
                                && user.getPurchasedQuiz().contains(file.getId())){
                            ((TestHolder) holder).openTxt.setVisibility(View.VISIBLE);
                            ((TestHolder) holder).lockLayout.setVisibility(View.GONE);
                            ((TestHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(context, StartTest.class).putExtra("testId", directory.getTests().get(index).getId()));
                                }
                            });
                        }
                    }
                });
            } else {
                ((TestHolder) holder).openTxt.setVisibility(View.VISIBLE);
                ((TestHolder) holder).lockLayout.setVisibility(View.GONE);
                ((TestHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, StartTest.class).putExtra("testId", directory.getTests().get(index).getId()));

                    }
                });
            }
        }
        if(holder instanceof AdViewHolder){
            Log.i("loadNative", "Adview");
            if(nativeAds.size()>0) {
                populateNativeAdView(nativeAds.get(0), ((AdViewHolder) holder).nativeAdView,(AdViewHolder)holder, position);
                nativeAds.remove(0);
            }else{
                prevIndexWithNoNative = position;
                ((AdViewHolder) holder).root.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            loadNativeAds();
        }
    }

    private void launchPurchaseFlow(String pointingDirId, Class klass, int price) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Fetching details");
        dialog.show();
        UserData.getUserLiveData()
                .observe((LifecycleOwner) context, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        UserData.getUserLiveData().removeObserver(this);
                        if (user.getPoints() < price){
                            insufficientBalanceAlert(price, user.getPoints());
                            dialog.dismiss();
                        } else {
                            confirmPurchase(pointingDirId, klass, price, user, dialog);
                        }
                    }
                });
    }

    private void confirmPurchase(String pointingDirId, Class klass, int price, User user, ProgressDialog dialog) {
        dialog.hide();
        new AlertDialog.Builder(context)
                .setTitle("Confirm purchase")
                .setMessage("Are you sure you want to buy the product for " + price + " coin. This transaction cannot be refunded.")
                .setPositiveButton("Buy Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        purchase(pointingDirId, klass, price, user, dialog);
                    }
                })
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialog.dismiss();
                        Toast.makeText(context, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void purchase(String pointingDirId, Class klass, int price, User user, ProgressDialog dialog) {
        dialog.show();
        Map updates = new HashMap();
        updates.put("points", FieldValue.increment(-1*price));

        if (klass == FileModel.class)
            updates.put("purchasedDirectories", FieldValue.arrayUnion(pointingDirId));
        if (klass == PDFModel.class)
            updates.put("purchasedPDFs", FieldValue.arrayUnion(pointingDirId));
        if (klass == ShortTest.class)
            updates.put("purchasedQuiz", FieldValue.arrayUnion(pointingDirId));

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dialog.hide();
                        if (task.isSuccessful()){
                            UserData.refreshUser();
                            dialog.dismiss();
                            Toast.makeText(context, "Purchase Successful! Updating values", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage(task.getException().getMessage())
                                    .setCancelable(true)
                                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    private void insufficientBalanceAlert(int price, int points) {
        new AlertDialog.Builder(context)
                .setTitle("Insufficient Balance")
                .setMessage("You don't have enough coins to buy this product. You can purchase coins by clicking the button below.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Buy Coins", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(context, BuyCoinsActivity.class);
                        context.startActivity(intent);
                    }
                })
                .show();
    }

    AdLoader adLoader;
    private void loadNativeAds(){
        Log.i(TAG, "loadNativeAds: abh nativeAdArrayListSize: "+nativeAds.size() );
        if(nativeAds.size()<2 && !isAdLoading){
            isAdLoading=true;
            adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_id_native))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeAds.add(nativeAd);
                            isAdLoading=adLoader.isLoading();
                            if (prevIndexWithNoNative != -1){
                                notifyItemChanged(prevIndexWithNoNative);
                                prevIndexWithNoNative = -1;
                            }
                            Log.i("loadNative", "loaded");
                            Log.i(TAG, "native ads loaded: abh nativeAdArrayListSize: "+nativeAds.size() );

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            Log.i("loadNative", adError.getMessage());
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAds(new AdRequest.Builder().build(), 5);
            Log.i("loadNative", "startedLoading");
        }
    }

    int prevIndexWithNoNative = -1;
    private void populateNativeAdView(NativeAd ad, NativeAdView adView, AdViewHolder holder, int pos){
        Log.i("loadNative", "populated");
        holder.root.setVisibility(View.VISIBLE);
        //title
        holder.title.setText(ad.getHeadline());
        adView.setHeadlineView(holder.title);

        //desc
        holder.desc.setText(ad.getBody());
        adView.setBodyView(holder.desc);

        //icon
        if (ad.getIcon() != null){
            if (ad.getIcon().getDrawable() != null) {
                ((ImageView) holder.icon).setImageDrawable(ad.getIcon().getDrawable());
                adView.setIconView(holder.icon);
            } else if (ad.getIcon().getUri() != null){
                Picasso.get()
                        .load(ad.getIcon().getUri())
                        .into(holder.icon);
                adView.setIconView(holder.icon);
            } else {
                holder.icon.setVisibility(View.GONE);
                holder.imageCard.setVisibility(View.GONE);
            }
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }


        //cta button
        holder.cta.setText(ad.getCallToAction());
        adView.setCallToActionView(holder.cta);

        adView.setNativeAd(ad);

    }

    private void getAdvPdf(RecyclerView.ViewHolder holder,int index){
        ((PDFVIewHolder) holder).root.setEnabled(false);
        boolean b = false;
        for(int i =0;i<new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).size();i++){
            if(new StorageHelper(context).getArrayListOfPDFModel(StorageHelper.DOWNLOADED).get(i).getPointingDir().equals(directory.getPdfModels().get(index).getPointingDir()) ){
                b = true;
                break;

            }
        }
        if(b){
            ((PDFVIewHolder) holder).root.setEnabled(true);
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
                            ((PDFVIewHolder) holder).downloadPercentTV.setText(String.valueOf(prog)+"%");

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
                            ((PDFVIewHolder) holder).root.setEnabled(true);
                            sangharshBooks.setActivePdfModel(directory.getPdfModels().get(index));
                            context.startActivity(new Intent(context, PDFDisplay.class));

                        }

                        @Override
                        public void onError(Error error) {
                            Log.d("sba", "download onError: "+error.getServerErrorMessage()+" connection exception "  +error.getConnectionException());
                            ((PDFVIewHolder) holder).seekBar.setVisibility(View.GONE);
                            ((PDFVIewHolder) holder).downloadPercentTV.setVisibility(View.GONE);

                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            ((PDFVIewHolder) holder).root.setEnabled(true);

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
            if(directory.getFiles().get(position)==null){
                return AD_TYPE;
            }
            return TYPE_FILE;
        }
        if(position - directory.getFiles().size() < directory.getPdfModels().size()){
            if(directory.getPdfModels().get(position - directory.getFiles().size())==null){
                return AD_TYPE;
            }
            return TYPE_PDF;
        }else{
            if(directory.getTests().get(position - directory.getFiles().size()-directory.getPdfModels().size())==null){
                return AD_TYPE;
            }
            return TYPE_TEST;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{//file view holder
        TextView fileNameTextView;
        CardView fileBg;
        LinearLayout linearLayout;
        ImageView fileItemBG;

        LinearLayout lockLayout;
        TextView priceTxt;
        TextView openTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.book_item_name);
            fileBg=itemView.findViewById(R.id.background_file_item);
            linearLayout = itemView.findViewById(R.id.file_item_background);
            fileItemBG = itemView.findViewById(R.id.book_item_holder_imageview);
            lockLayout = itemView.findViewById(R.id.paidLayout);
            openTxt = itemView.findViewById(R.id.openTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
        }
    }
    public class PDFVIewHolder extends RecyclerView.ViewHolder{
        TextView pdfNamepdfItem,downloadPercentTV;
        CardView root;
        SeekBar seekBar;

//        LinearLayout llBG;

        LinearLayout lockLayout;
        TextView priceTxt;
        TextView openTxt;

        public PDFVIewHolder(View itemView){
            super(itemView);
            pdfNamepdfItem = itemView.findViewById(R.id.pdf_name_pdf_item);
            root = itemView.findViewById(R.id.pdf_item_background);
            seekBar = itemView.findViewById(R.id.pdf_item_download_seekbar);
            downloadPercentTV = itemView.findViewById(R.id.download_percent_tv);

            lockLayout = itemView.findViewById(R.id.paidLayout);
            openTxt = itemView.findViewById(R.id.openTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);

        }
    }
    public class TestHolder extends RecyclerView.ViewHolder{
        TextView fileNameTextView;
        CardView fileBg;
        LinearLayout linearLayout;
        ImageView fileItemBG;

        LinearLayout lockLayout;
        TextView priceTxt;
        TextView openTxt;

        public TestHolder(View itemView){
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.book_item_name);
            fileBg=itemView.findViewById(R.id.background_file_item);
            linearLayout = itemView.findViewById(R.id.file_item_background);
            fileItemBG = itemView.findViewById(R.id.book_item_holder_imageview);

            lockLayout = itemView.findViewById(R.id.paidLayout);
            openTxt = itemView.findViewById(R.id.openTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
        }
    }
    public class AdViewHolder extends RecyclerView.ViewHolder{
        NativeAdView nativeAdView;
        CardView root, imageCard;
        TextView title, desc,cta;
        ImageView icon;




        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            nativeAdView = itemView.findViewById(R.id.native_ad_layout);
            root = itemView.findViewById(R.id.background_ad_item);
            title=itemView.findViewById(R.id.ad_title_rv);
            desc = itemView.findViewById(R.id.ad_desc_rv);
            icon = itemView.findViewById(R.id.ad_image);
            cta = itemView.findViewById(R.id.cta_ad_view);
            imageCard = itemView.findViewById(R.id.imageCard);
        }


    }

}
