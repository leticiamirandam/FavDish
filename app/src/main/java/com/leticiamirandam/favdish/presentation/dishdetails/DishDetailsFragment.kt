package com.leticiamirandam.favdish.presentation.dishdetails

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.FragmentDishDetailsBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    private var mFavDishDetails: FavDish? = null
    private lateinit var mBinding: FragmentDishDetailsBinding
    private val dishDetailsViewModel: DishDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_dish -> {
                shareDish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareDish() {
        val type = "text/plain"
        val subject = "Checkout this dish recipe"
        val shareWith = "Share with"
        mFavDishDetails?.let {
            val image = if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                it.image
            } else {
                ""
            }
            val cookingInstructions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    it.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                ).toString()
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(it.directionToCook).toString()
            }
            val extraText =
                "$image \n" +
                        "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                        "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                        "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = type
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, extraText)
            startActivity(Intent.createChooser(intent, shareWith))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        mFavDishDetails = args.dishDetails
        setupDishInfo(args)
        setupFavoriteOnClickListener(args)
    }

    private fun setupDishInfo(args: DishDetailsFragmentArgs) {
        try {
            Glide.with(requireActivity())
                .load(args.dishDetails.image)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("TAG", "ERROR loading image", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource.let {
                            Palette.from(resource!!.toBitmap()).generate { palette ->
                                val intColor = palette?.lightVibrantSwatch?.rgb ?: 0
                                mBinding.rlDishDetailMain.setBackgroundColor(intColor)
                            }
                        }
                        return false
                    }
                })
                .into(mBinding.ivDishImage)
        } catch (e: IOException) {
            Log.i("Error: ", e.stackTraceToString())
        }
        mBinding.tvTitle.text = args.dishDetails.title
        mBinding.tvType.text = args.dishDetails.type.replaceFirstChar { it.titlecase(Locale.ROOT) }
        mBinding.tvCategory.text = args.dishDetails.category
        mBinding.tvIngredients.text = args.dishDetails.ingredients
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.tvCookingDirection.text = Html.fromHtml(
                args.dishDetails.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding.tvCookingDirection.text = Html.fromHtml(args.dishDetails.directionToCook)
        }
        mBinding.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            args.dishDetails.cookingTime
        )
        if (args.dishDetails.favoriteDish) {
            mBinding.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_selected
                )
            )
        } else {
            mBinding.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_unselected
                )
            )
        }
    }

    private fun setupFavoriteOnClickListener(args: DishDetailsFragmentArgs) {
        mBinding.ivFavoriteDish.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            dishDetailsViewModel.update(args.dishDetails)
            if (args.dishDetails.favoriteDish) {
                mBinding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                showFeedbackToast(getString(R.string.msg_added_to_favorites))
            } else {
                mBinding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
                showFeedbackToast(getString(R.string.msg_removed_from_favorites))
            }
        }
    }

    private fun showFeedbackToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }
}