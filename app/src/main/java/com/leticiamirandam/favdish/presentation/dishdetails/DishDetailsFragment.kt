package com.leticiamirandam.favdish.presentation.dishdetails

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.FragmentDishDetailsBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.utils.Constants
import com.leticiamirandam.favdish.utils.NotificationUtil
import com.leticiamirandam.favdish.utils.PrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    private var favDishDetails: FavDish? = null
    private lateinit var binding: FragmentDishDetailsBinding
    private val dishDetailsViewModel: DishDetailsViewModel by viewModel()

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    internal var secondsRemaining: Long = 0
    private var timerState = MainActivity.TimerState.Stopped

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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

    override fun onResume() {
        super.onResume()
        NotificationUtil.hideTimerNotification(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        if (timerState == MainActivity.TimerState.Running) {
            val wakeUpTime = MainActivity.setAlarm(requireContext(), MainActivity.nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(requireContext(), wakeUpTime)
        } else if (timerState == MainActivity.TimerState.Paused) {
            NotificationUtil.showTimerPaused(requireContext())
        }
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, requireContext())
        PrefUtil.setSecondsRemaining(secondsRemaining, requireContext())
        PrefUtil.setTimerState(timerState, requireContext())
    }

    private fun startTimer() {
        timerState = MainActivity.TimerState.Running
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
            }
        }.start()
    }

    private fun onTimerFinished(){
        timerState = MainActivity.TimerState.Stopped
        setNewTimerLength()
        PrefUtil.setSecondsRemaining(timerLengthSeconds, requireContext())
        secondsRemaining = timerLengthSeconds
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(requireContext())
        timerLengthSeconds = (lengthInMinutes * 60L)
    }

    private fun shareDish() {
        val type = "text/plain"
        val subject = "Checkout this dish recipe"
        val shareWith = "Share with"
        favDishDetails?.let {
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
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        favDishDetails = args.dishDetails
        setupDishInfo(args)
        setupFavoriteOnClickListener(args)
        setupTimerOnClickListener(args)
    }

    private fun setupDishInfo(args: DishDetailsFragmentArgs) {
        ViewCompat.setTransitionName(binding.tvTitle, "title_${args.dishDetails.id}")
        ViewCompat.setTransitionName(binding.ivDishImage, "image_${args.dishDetails.id}")
        try {
            Glide.with(requireActivity())
                .load(args.dishDetails.image)
                .centerCrop()
                .into(binding.ivDishImage)
        } catch (e: IOException) {
            Log.i("Error: ", e.stackTraceToString())
        }
        binding.tvTitle.text = args.dishDetails.title
        binding.tvType.text = args.dishDetails.type.replaceFirstChar { it.titlecase(Locale.ROOT) }
        binding.tvCategory.text = args.dishDetails.category
        binding.tvIngredients.text = args.dishDetails.ingredients
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvCookingDirection.text = Html.fromHtml(
                args.dishDetails.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding.tvCookingDirection.text = Html.fromHtml(args.dishDetails.directionToCook)
        }
        binding.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            args.dishDetails.cookingTime
        )
        if (args.dishDetails.favoriteDish) {
            binding.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_selected
                )
            )
        } else {
            binding.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_unselected
                )
            )
        }
    }

    private fun setupFavoriteOnClickListener(args: DishDetailsFragmentArgs) {
        binding.ivFavoriteDish.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            dishDetailsViewModel.update(args.dishDetails)
            if (args.dishDetails.favoriteDish) {
                binding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                showFeedbackToast(getString(R.string.msg_added_to_favorites))
            } else {
                binding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
                showFeedbackToast(getString(R.string.msg_removed_from_favorites))
            }
        }
    }

    private fun setupTimerOnClickListener(dishDetailsArgs: DishDetailsFragmentArgs) {
        binding.addTimerButton.setOnClickListener {
            val dishDetailsCookingTime = dishDetailsArgs.dishDetails.cookingTime
            PrefUtil.setTimerLength(requireContext(), dishDetailsCookingTime.toInt())
            startTimer()
            timerState =  MainActivity.TimerState.Running
            showFeedbackToast("Timer iniciado!")
        }
    }

    private fun showFeedbackToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }
}