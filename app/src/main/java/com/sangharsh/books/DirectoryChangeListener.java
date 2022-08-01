package com.sangharsh.books;

import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.PDFModel;

public interface DirectoryChangeListener {

    void update();
    void onFileModelAdded(FileModel fileModel);
    void onPDFModelAdded(PDFModel pdfModel);

}
