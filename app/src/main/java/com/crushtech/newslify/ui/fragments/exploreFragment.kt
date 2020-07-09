package com.crushtech.newslify.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.ExploreItemsAdapter
import com.crushtech.newslify.adapter.ExploreGroupAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.explore_layout.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

data class Explore(val name: String, val imageSource: Int, val itemBackground: Int)
data class ExploreSource(val sourceName: String, val img: Int, val motto: String)

private var explore: ArrayList<Explore>? = null
private var exploreSource: ArrayList<ExploreSource>? = null

private var exploreGroupAdapter: ExploreGroupAdapter? = null
private lateinit var viewModel: NewsViewModel

class exploreFragment : Fragment(R.layout.explore_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        initExploreItems()
        setUpRecyclerViewForSource()
        exploreGroupAdapter!!.exploreItems.differ.submitList(explore)
    }

    private fun initExploreItems() {
        explore = ArrayList()
        explore!!.add(
            Explore(
                "Technology",
                R.drawable.technology_icon,
                R.drawable.explore_item1_bg
            )
        )
        explore!!.add(Explore("Sports", R.drawable.sports_icon, R.drawable.explore_item2_bg))
        explore!!.add(Explore("Business", R.drawable.business_icon, R.drawable.explore_item3_bg))
        explore!!.add(
            Explore(
                "Entertainment",
                R.drawable.entertainment_icon,
                R.drawable.explore_item4_bg
            )
        )
        explore!!.add(Explore("Health", R.drawable.health_icon, R.drawable.explore_item5_bg))
        explore!!.add(Explore("Science", R.drawable.science_icon, R.drawable.explore_item6_bg))
    }

    private fun setUpRecyclerViewForSource() {
        initExploreSourceItems()
        setUpData()
        exploreGroupAdapter = ExploreGroupAdapter(requireContext())
        exploreGroupAdapter!!.differ.submitList(exploreSource)
        explore_rv1.apply {
            adapter = exploreGroupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initExploreSourceItems() {
        exploreSource = ArrayList()
        exploreSource!!.add(ExploreSource("BBC", R.drawable.bbc, "The world's newsroom"))
        exploreSource!!.add(ExploreSource("CNN", R.drawable.cnn, "The world's news network"))
    }

    private fun setUpData() {
//        viewModel.specificNews.observe(viewLifecycleOwner, Observer { response ->
//            when (response) {
//                is Resource.Success -> {
//                    //groupAdapter!!.sportNews.showShimmer = false
//                    response.data?.let { newsResponse ->
//                        try {
//                            exploreGroupAdapter!!.newsSource.differ.submitList(newsResponse.articles.toList())
//                            //groupAdapter!!.sportNews.notifyDataSetChanged()
//
//                        } catch (e: Exception) {
//                        }
//                    }
//                }
//                is Resource.Error -> {
////                    lottie_no_internet.visibility = View.VISIBLE
////                    rvBreakingNews.visibility = View.INVISIBLE
////                    no_internet_text.visibility = View.VISIBLE
//                    response.message?.let {
//                        SimpleCustomSnackbar.make(
//                            requireView(), it,
//                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
//                            ContextCompat.getColor(requireContext(), R.color.mycolor)
//                        )?.show()
//
//                    }
//                }
//                is Resource.Loading -> {
//                    GlobalScope.launch(Dispatchers.Main) {
//                        delay(3000)
//                    }
//                }
//            }
//        })
//
//    }
    }
}