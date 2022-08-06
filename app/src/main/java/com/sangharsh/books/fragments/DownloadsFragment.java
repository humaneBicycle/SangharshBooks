package com.sangharsh.books.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sangharsh.books.R;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.adapter.PDFAdapter;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class DownloadsFragment extends Fragment {

    RecyclerView recyclerView;
    //LinearLayout linearLayout;
    ArrayList<PDFModel> pdfModels;
    PDFAdapter pdfAdapter;


    public DownloadsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerView = view.findViewById(R.id.rv_downloads);
        //linearLayout = view.findViewById(R.id.linerlayout_download_frag);
        pdfModels = new StorageHelper(getActivity()).getArrayListOfPDFModel(StorageHelper.DOWNLOADED);
        pdfAdapter = new PDFAdapter(getActivity().getApplication(),getActivity(),pdfModels,"downloads");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(pdfAdapter);

        return view;
    }

    @Override
    public void onResume() {
        pdfModels.clear();
        pdfModels.addAll(new StorageHelper(getContext()).getArrayListOfPDFModel(StorageHelper.DOWNLOADED));
        pdfAdapter.notifyDataSetChanged();
        super.onResume();

    }
}