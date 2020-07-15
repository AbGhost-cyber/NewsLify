package com.crushtech.newslify.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.fragment_article.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ArticleFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var isClicked = false
    private var btnCount = 0

    // private var streakCount = 1
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as NewsActivity).window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val view = layoutInflater.inflate(R.layout.fragment_article, container, false)
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).supportActionBar?.hide()
        (activity as NewsActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        retainInstance = true
        //get the argument from the generated arg class
        val article = args.article
        val viewPos: View = view.findViewById(R.id.myCord)
        val logoAnim: Animation = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )


        view.findViewById<FloatingActionButton>(R.id.fab_favorite).setOnClickListener {
            //article.articleIsSaved = true
            val customSnackListener: View.OnClickListener = View.OnClickListener {
                findNavController().navigate(R.id.action_articleFragment_to_savedNewsFragment)
            }
            isClicked = true
            if (isClicked && btnCount == 0) {
                val date = Calendar.getInstance().time
                val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
                val strDate = dateFormat.format(date)
                article.timeInsertedToRoomDatabase = strDate
                viewModel.saveArticle(article)

                view.findViewById<FloatingActionButton>(R.id.fab_favorite).apply {
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                    startAnimation(logoAnim)
                }

                SimpleCustomSnackbar.make(
                    viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", ContextCompat.getColor(requireContext(), R.color.mygrey)
                )?.show()
            } else {
                SimpleCustomSnackbar.make(
                    viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", ContextCompat.getColor(requireContext(), R.color.mygrey)
                )?.show()
            }
            btnCount++

        }


        //streak logic
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
            var streakCount = prefs.getInt(STREAK, 0)
            streakCount++
            if (isClicked && !(article.category!!.contains("yesterday", true))
                && !(article.category!!.contains("days", true))
            ) {
                requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
                    .edit().putInt(STREAK, streakCount).apply()


                if (streakCount == 5) {
                    SimpleCustomSnackbar.make(
                        viewPos, "Daily news article goal reached :) ", Snackbar.LENGTH_LONG,
                        null, R.drawable.streak_icon,
                        "", ContextCompat.getColor(requireContext(), R.color.mygrey)
                    )?.show()

                } else if (isClicked && streakCount > 5 && streakCount % 5 == 0) {
                    SimpleCustomSnackbar.make(
                        viewPos,
                        "x${streakCount / 5} of daily goals reached: you're on fire ",
                        Snackbar.LENGTH_LONG,
                        null,
                        R.drawable.rocket,
                        "",
                        ContextCompat.getColor(requireContext(), R.color.mygrey)
                    )?.show()
                }
            }
        })


//        GlobalScope.launch(Dispatchers.Main) {
//            delay(200L)
//            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
//                if (isClicked && articles.contains(article).) {
//                    SimpleCustomSnackbar.make(
//                        viewPos, "Daily news article goal reached :) ", Snackbar.LENGTH_LONG,
//                        null, R.drawable.streak_icon,
//                        "", ContextCompat.getColor(requireContext(), R.color.mygrey)
//                    )?.show()
//
//                } else if (isClicked && articles.size > 5 && articles.size % 5 == 0) {
//                    SimpleCustomSnackbar.make(
//                        viewPos,
//                        "x${articles.size / 5} of daily goals reached: you're on fire ",
//                        Snackbar.LENGTH_LONG,
//                        null,
//                        R.drawable.rocket,
//                        "",
//                        ContextCompat.getColor(requireContext(), R.color.mygrey)
//                    )?.show()
//                }
//            })
//
//
//        }


        view.findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            val articleUrl = "From NewsLify:  ${article.url}"
            val shareSub = "APP NAME/TITLE"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/link"
                putExtra(Intent.EXTRA_SUBJECT, shareSub)
                putExtra(Intent.EXTRA_TEXT, articleUrl)
            }
            startActivity(Intent.createChooser(intent, "Share Using"))
        }

        val bottomSheet: NestedScrollView = view.findViewById(R.id.my_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<WebView>(R.id.scrollwebView).apply {
            this.settings.cacheMode = WebSettings.LOAD_DEFAULT
            this.settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        lottie_webview_loading.visibility = View.INVISIBLE
                        webview_loading_text1.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                    }
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {

                    try {
                        lottie_webview_loading.visibility = View.GONE
                        webview_loading_text1.visibility = View.GONE
                        StyleableToast.makeText(
                            requireContext(),
                            "An error occurred",
                            R.style.customToast
                        ).show()
                    } catch (e: Exception) {
                    }
                }
            }
            article.url?.let {
                loadUrl(it)
            }
        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (activity as NewsActivity).hideBottomNavigation()
        } catch (e: Exception) {
        }

    }

    private fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        (activity as NewsActivity).hideBottomNavigation()
        super.onResume()
    }

    override fun onStop() {
        try {
            (activity as NewsActivity).supportActionBar?.show()
            (activity as NewsActivity).window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            (activity as NewsActivity).requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (e: Exception) {
        }
        super.onStop()
    }

    fun okList(article: Article): MutableList<Article> {
        val mutable = mutableListOf<Article>()
        if (!(article.category!!.contains("day"))) {
            mutable.add(article)
        }
        if (mutable.size == 5) {
            Toast.makeText(context, "streak", Toast.LENGTH_LONG).show()
        }
        return mutable
    }
}