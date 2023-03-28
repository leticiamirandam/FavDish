package com.leticiamirandam.favdish.presentation.randomdish

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.FragmentRandomDishBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import org.koin.androidx.viewmodel.ext.android.viewModel

class RandomDishFragment : Fragment() {

    private lateinit var mBinding: FragmentRandomDishBinding

    private val mRandomDishViewModel: RandomDishViewModel by viewModel()
    private var addedToFavorites = false
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addRandomDishViewModelObserver()
        setupOnRefreshListener()
    }

    private fun setupOnRefreshListener() {
        mBinding.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    private fun addRandomDishViewModelObserver() {
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner) { randomDishResponse ->
            if (mBinding.srlRandomDish.isRefreshing)
                mBinding.srlRandomDish.isRefreshing = false
            setRandomDishResponseInUI(randomDishResponse)
        }
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (mBinding.srlRandomDish.isRefreshing)
                    mBinding.srlRandomDish.isRefreshing = false
                Log.e(getString(R.string.random_dish_api_error_message), "$dataError")
            }
        }
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner) { loadRandomDish ->
            loadRandomDish.let {
                if (loadRandomDish && !mBinding.srlRandomDish.isRefreshing) {
                    showCustomProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun setRandomDishResponseInUI(favDish: FavDish) {
        Glide.with(requireActivity())
            .load(favDish.image)
            .centerCrop()
            .into(mBinding.ivDishImage)
        mBinding.tvTitle.text = favDish.title
        mBinding.tvType.text = favDish.type
        mBinding.tvCategory.text = getString(R.string.category_other)
        mBinding.tvIngredients.text = favDish.ingredients
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.tvCookingDirection.text = Html.fromHtml(
                favDish.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding.tvCookingDirection.text = Html.fromHtml(favDish.directionToCook)
        }
        mBinding.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )
        mBinding.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                favDish.cookingTime
            )
        mBinding.ivFavoriteDish.setOnClickListener {
            if (addedToFavorites) {
                showFeedbackToast(getString(R.string.msg_already_added_to_favorites))
            } else {
                mRandomDishViewModel.saveFavoriteDish(favDish)
                showFavoriteDishSavedFeedback()
            }
        }
    }

    private fun showFavoriteDishSavedFeedback() {
        addedToFavorites = true
        mBinding.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_selected
            )
        )
        showFeedbackToast(getString(R.string.msg_added_to_favorites))
    }

    private fun showFeedbackToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}