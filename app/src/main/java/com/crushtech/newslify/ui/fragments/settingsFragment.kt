package com.crushtech.newslify.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.PremiumThemesAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.PRIVACY_POLICY
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mikelau.countrypickerx.CountryPickerCallbacks
import com.mikelau.countrypickerx.CountryPickerDialog
import kotlinx.android.synthetic.main.settings_layout.*
import java.util.*

data class ThemeItems(val themeName: String, val lottieRaw: Int, val themeBackground: Int)
class settingsFragment : Fragment(R.layout.settings_layout) {
    private var countryPicker: CountryPickerDialog? = null
    private var countryIsoCode: String? = null
    private var myCountry: String? = null
    private var settingItemsAnim: Animation? = null
    private var isPremiumUser = false
    private lateinit var viewModel: NewsViewModel
    private var premiumDialog: BottomSheetDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel

        val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
        val streakCount = prefs.getInt(STREAK, 0)
        dailynewsGoals.text = "Goals reached: $streakCount news articles read today"

        settingItemsAnim = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )
        dailynewsGoalsParent.setOnClickListener {
            it.startAnimation(settingItemsAnim)
        }
        custom_N.setOnClickListener {
            it.startAnimation(settingItemsAnim)
        }
        checkCoins.setOnClickListener {
            it.startAnimation(settingItemsAnim)
        }
        support_us.setOnClickListener {
            it.startAnimation(settingItemsAnim)
        }


        val conPrefs = requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val country = conPrefs.getString("Country", "")
        change_country.text = "Change Country: ${country?.capitalize()}"
        setupDailyGoals()
        setupCountry()
        setupPrivacyPolicy()
        setUpShareFunction()
        setUpRateApp()
        setupPurchaseThemes()

        sendFeedback.setOnClickListener {
            sendFeedback.startAnimation(settingItemsAnim)
            findNavController().navigate(R.id.action_settingsFragment_to_sendFeebackFragment)
        }

    }

    private fun setupDailyGoals() {
        setnewsGoals.setOnClickListener {
            setnewsGoals.startAnimation(settingItemsAnim)
            val dialog = Dialog(requireContext(), R.style.PauseDialog)
            dialog.setContentView(R.layout.news_goals_dialog)
            val dismissDialog = dialog.findViewById<Button>(R.id.btn_cancel)
            val setDailyCount = dialog.findViewById<Button>(R.id.btn_set)
            val goalCount = dialog.findViewById<EditText>(R.id.goal_count)
            val animation: Animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.fade_in
            )
            val prefs =
                requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
            val getPrefsCount = prefs.getInt("Goal Count", 5)
            goalCount.setText(getPrefsCount.toString())

            dismissDialog.setOnClickListener {
                dismissDialog.animation = animation
                dialog.dismiss()
            }
            setDailyCount.setOnClickListener {
                setDailyCount.animation = animation
                goalCount.let {
                    var goalCountText = it.text.toString()
                    if (TextUtils.isEmpty(goalCountText) || goalCountText == "0") {
                        goalCountText = "5"
                    }
                    requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
                        .edit().putInt("Goal Count", goalCountText.toInt()).apply()
                }
                view?.let { it1 ->
                    SimpleCustomSnackbar.make(
                        it1, "Your Goal is set: you're good to go",
                        Snackbar.LENGTH_LONG, null, R.drawable.rocket, "",
                        null
                    )?.show()
                }
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                hideKeyboard()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.setCancelable(false)
            // }
        }
    }

    private fun setupCountry() {
        change_countryParent.setOnClickListener {
            change_countryParent.startAnimation(settingItemsAnim)
            countryPicker = CountryPickerDialog(
                requireContext(),
                CountryPickerCallbacks { country, _ ->
                    countryIsoCode = country.isoCode
                    myCountry = country.getCountryName(context)
                    change_country.text = "Change Country: ${myCountry?.capitalize()}"
                    myCountry?.let { saveDataToPref(it) }
                    view?.let { it1 ->
                        SimpleCustomSnackbar.make(
                            it1, "Country changed: please RESTART the app",
                            Snackbar.LENGTH_SHORT, null,
                            R.drawable.country_changed_icon, "",
                            null
                        )?.show()
                    }
                }, false, 0
            )
            countryPicker!!.show()
        }

    }

    private fun saveDataToPref(country: String) {
        requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE).apply {
            edit().apply {
                putString("Country", country)
                putString("countryIsoCode", countryIsoCode!!.toLowerCase(Locale.ROOT))
                apply()
            }
        }
    }

    private fun setupPrivacyPolicy() {
        privacy_policy.setOnClickListener {
            privacy_policy.startAnimation(settingItemsAnim)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(PRIVACY_POLICY)
            startActivity(intent)
        }

    }

    private fun setUpRateApp() {
        rate_app.setOnClickListener {
            rate_app.startAnimation(settingItemsAnim)
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + requireContext().packageName)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                    )
                )
            }

        }
    }

    private fun setUpShareFunction() {
        shareApp.setOnClickListener {
            shareApp.startAnimation(settingItemsAnim)
            val shareIntent = Intent(Intent.ACTION_SEND)
            val appPackageName =
                requireContext().applicationContext.packageName
            val strAppLink: String
            strAppLink = try {
                "https://play.google.com/store/apps/details?id$appPackageName"
            } catch (anfe: ActivityNotFoundException) {
                "https://play.google.com/store/apps/details?id$appPackageName"
            }
            shareIntent.type = "text/link"
            val shareBody =
                "Hey, Check out NewsLify, i use it to read trending news of all kinds . Get it for free at \n$strAppLink"
            val shareSub = "APP NAME/TITLE"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(shareIntent, "Share Using"))
        }

    }


    private fun setupPurchaseThemes() {
        purchaseThemes.setOnClickListener {
//            if (!isPremiumUser) {
//                this.activity?.let { it1 -> showPopupDialog(requireContext(), it1) }
//            }else {
            premiumDialog =
                BottomSheetDialog(
                    requireContext(),
                    R.style.Theme_MaterialComponents_BottomSheetDialog
                )
            premiumDialog!!.setContentView(R.layout.customthemes_popup_dialog)
            val dismissDialog =
                premiumDialog!!.findViewById<ExtendedFloatingActionButton>(R.id.close_theme_popup)
            val slideInAnim: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.button_anim
            )
            if (dismissDialog != null) {
                dismissDialog.animation = slideInAnim
            }
            val recyclerView = premiumDialog!!.findViewById<RecyclerView>(R.id.customThemesRv)
            val themesItems: ArrayList<ThemeItems> = ArrayList()
            val themesAdapter = PremiumThemesAdapter()
            themesItems.add(
                ThemeItems(
                    "Premium Theme 1",
                    R.raw.themes,
                    R.drawable.explore_item1_bg
                )
            )
            themesItems.add(
                ThemeItems(
                    "Premium Theme 2",
                    R.raw.themes,
                    R.drawable.themes_item2_bg
                )
            )
            themesItems.add(
                ThemeItems(
                    "Premium Theme 3",
                    R.raw.themes,
                    R.drawable.explore_item2_bg
                )
            )
            themesItems.add(
                ThemeItems(
                    "Premium Theme 4",
                    R.raw.themes,
                    R.drawable.theme_item4_bg
                )
            )
            themesItems.add(
                ThemeItems(
                    "Premium Theme 5",
                    R.raw.themes,
                    R.drawable.explore_item3_bg
                )
            )
            themesItems.add(
                ThemeItems(
                    "Default App Theme",
                    R.raw.themes,
                    R.drawable.default_theme_item_bg
                )
            )

            themesAdapter.differ.submitList(themesItems)
            recyclerView?.apply {
                adapter = themesAdapter
                layoutManager =
                    GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
            }
            // to update the current switch item
            if (recyclerView != null) {
                if (!recyclerView.isComputingLayout && recyclerView.scrollState == SCROLL_STATE_IDLE) {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }

            dismissDialog?.setOnClickListener {
                premiumDialog!!.dismiss()
            }
            premiumDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            premiumDialog!!.show()
        }
    }


    override fun onStop() {
        countryPicker?.dismiss()
        premiumDialog?.dismiss()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onStop()
    }

    override fun onResume() {
        (activity as NewsActivity).showBottomNavigation()
        super.onResume()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onPause() {
        try {
            premiumDialog?.dismiss()
        } catch (e: Exception) {
        }
        super.onPause()
    }

    override fun onDestroy() {
        try {
            premiumDialog?.dismiss()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

}