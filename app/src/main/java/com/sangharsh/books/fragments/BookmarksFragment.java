package com.sangharsh.books.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sangharsh.books.R;
import com.sangharsh.books.StorageHelper;
import com.sangharsh.books.adapter.PDFAdapter;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class BookmarksFragment extends Fragment {

    RecyclerView recyclerView;
    PDFAdapter pdfAdapter;
    ArrayList<PDFModel> pdfModels;


    public BookmarksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        recyclerView = view.findViewById(R.id.rv_bookmarks);
        //linearLayout = view.findViewById(R.id.linerlayout_download_frag);

        pdfModels = new StorageHelper(getActivity()).getArrayListOfPDFModel(StorageHelper.BOOKMARKS);
        pdfAdapter = new PDFAdapter(getActivity().getApplication(),getActivity(),pdfModels,"bookmarks");
        recyclerView.setAdapter(pdfAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    @Override
    public void onResume() {
        pdfModels.clear();
        pdfModels.addAll(new StorageHelper(getContext()).getArrayListOfPDFModel(StorageHelper.BOOKMARKS));
        pdfAdapter.notifyDataSetChanged();
        Log.d("sba", "onResume: of bookfrag called");
        super.onResume();
    }
}