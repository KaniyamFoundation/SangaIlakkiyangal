package com.jskaleel.sangaelakkiyangal.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.jskaleel.sangaelakkiyangal.R
import com.shockwave.pdfium.PdfDocument
import java.io.File


class PdfViewerActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
    lateinit var pdfView: PDFView

    override fun onPageChanged(page: Int, pageCount: Int) {
    }

    override fun loadComplete(nbPages: Int) {
        printBookmarksTree(pdfView.tableOfContents, "-");
    }

    override fun onPageError(page: Int, t: Throwable?) {
        Toast.makeText(this, t?.message, Toast.LENGTH_SHORT).show()
    }

    private fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pdf_viewer)
        pdfView = findViewById(R.id.pdfView)

        val uri : Uri? = intent.data
        if (uri != null) {
            displayFromUri(uri)
        }
    }

    private fun displayFromUri(uri: Uri) {
        val file = File(uri.path)
        pdfView.fromFile(file)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load()
    }

    companion object {
        fun getPdfViewerIntent(context: Context, uri: Uri): Intent {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.data = uri
            return intent
        }
    }

}