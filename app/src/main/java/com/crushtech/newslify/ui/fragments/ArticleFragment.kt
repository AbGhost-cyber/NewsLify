package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.entertainment_news.view.*
import kotlinx.android.synthetic.main.fragment_article.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    // private lateinit var bottomSheetBehavior:BottomSheetBehavior
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).supportActionBar?.hide()
        //  (activity as NewsActivity).supportActionBar?.hide()
        val article = args.article
        //get the argument from the generated arg class

        fab_favorite.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).apply {
                setAction("View") {
                    findNavController().navigate(R.id.action_articleFragment_to_savedNewsFragment)
                }
                show()
            }
        }

        fab_share.setOnClickListener {
            Snackbar.make(view, "share button clicked", Snackbar.LENGTH_SHORT).show()
        }

        val bottomSheet: NestedScrollView = view.findViewById(R.id.my_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        article.urlToImage?.let { image ->
            Picasso.get().load(image).fit().centerCrop()
                .into(article_img)
        }
        scrollwebView.apply {

            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as NewsActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as NewsActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onStop() {
        (activity as NewsActivity).showBottomNavigation()
        (activity as NewsActivity).supportActionBar?.show()
        super.onStop()
    }

}