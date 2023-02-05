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
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.di.FavDishApplication
import com.leticiamirandam.favdish.databinding.FragmentRandomDishBinding
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class RandomDishFragment : Fragment() {

    private var mBinding: FragmentRandomDishBinding? = null

    private val mRandomDishViewModel: RandomDishViewModel by viewModel()

    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel.getRandomRecipeFromApi()
        randomDishViewModelObserver()

        mBinding!!.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
    }

    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner) { randomDishResponse ->
            if (mBinding!!.srlRandomDish.isRefreshing)
                mBinding!!.srlRandomDish.isRefreshing = false
            setRandomDishResponseInUI(randomDishResponse)
        }
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (mBinding!!.srlRandomDish.isRefreshing)
                    mBinding!!.srlRandomDish.isRefreshing = false
                Log.e("Random Dish API Error", "$dataError")
            }
        }
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner) { loadRandomDish ->
            loadRandomDish.let {
                Log.e("Random Dish Loading", "$loadRandomDish")
                if(loadRandomDish && !mBinding!!.srlRandomDish.isRefreshing) {
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
            .into(mBinding!!.ivDishImage)
        mBinding!!.tvTitle.text = favDish.title
        mBinding!!.tvType.text = favDish.type
        mBinding!!.tvCategory.text = "Other"
        mBinding!!.tvIngredients.text = favDish.ingredients
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                favDish.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(favDish.directionToCook)
        }
        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )
        var addedToFavorites = false

        mBinding!!.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                favDish.cookingTime
            )
        mBinding!!.ivFavoriteDish.setOnClickListener {
            if (addedToFavorites) {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_already_added_to_favorites),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val randomDishDetais = FavDish(
                    id = favDish.id,
                    image = favDish.image,
                    imageSource = Constants.DISH_IMAGE_SOURCE_ONLINE,
                    title = favDish.title,
                    type = favDish.type,
                    category = "Other",
                    ingredients = favDish.ingredients,
                    cookingTime = favDish.cookingTime,
                    directionToCook = favDish.directionToCook,
                    favoriteDish = true
                )
                mRandomDishViewModel.insert(randomDishDetais)
                addedToFavorites = true
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}