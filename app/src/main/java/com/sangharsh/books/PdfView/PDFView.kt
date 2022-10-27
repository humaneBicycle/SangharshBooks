package com.sangharsh.books.PdfView

import android.content.Context
import android.util.AttributeSet
import com.sangharsh.books.PdfView.subsamplincscaleimageview.ImageSource
import com.sangharsh.books.PdfView.subsamplincscaleimageview.SubsamplingScaleImageView
import com.sangharsh.books.interfaces.Callback
import java.io.File

class PDFView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SubsamplingScaleImageView(context, attrs) {

    private var mfile: File? = null
    private var mScale: Float = 8f

    init {
        setMinimumTileDpi(120)
        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START)
    }

    fun fromAsset(assetFileName: String): PDFView {
        mfile = FileUtils.fileFromAsset(context, assetFileName)
        return this
    }

    fun fromFile(file: File, mListener: Callback): PDFView {
        mfile = file
        listener = mListener;
        return this
    }

    lateinit var listener: Callback;

    fun fromFile(filePath: String): PDFView {
        mfile = File(filePath)
        return this
    }

    fun scale(scale: Float): PDFView {
        mScale = scale
        return this
    }

    fun show() {
        val source = ImageSource.uri(mfile!!.path)
        setRegionDecoderFactory { PDFRegionDecoder(view = this, file = mfile!!, scale = mScale,listener) }
        setImage(source)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.recycle()
    }
}