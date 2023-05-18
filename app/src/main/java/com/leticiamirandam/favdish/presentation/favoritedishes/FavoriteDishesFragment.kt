package com.leticiamirandam.favdish.presentation.favoritedishes

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.FragmentFavoriteDishesBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.presentation.common.adapters.FavDishAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteDishesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteDishesBinding

    private val favDishViewModel: FavoriteDishesViewModel by viewModel()
    private lateinit var favDishAdapter: FavDishAdapter
    private var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        favoriteDishesViewModelObserver()
    }

    override fun onStart() {
        super.onStart()
        favDishViewModel.getFavoriteDishes()
    }

    private fun setupRecyclerView() {
        binding.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        favDishAdapter = FavDishAdapter(this@FavoriteDishesFragment)
        binding.rvFavoriteDishesList.apply {
            adapter = favDishAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun favoriteDishesViewModelObserver() {
        favDishViewModel.favoriteDishesListResponse.observe(viewLifecycleOwner) { favoriteDishesResponse ->
            favoriteDishesResponse.let {
                if (it.isNotEmpty()) {
                    binding.rvFavoriteDishesList.isVisible = true
                    binding.tvNoFavoriteDishesAddedYet.isGone = true
                    favDishAdapter.dishesList(it)
                } else {
                    binding.rvFavoriteDishesList.isGone = true
                    binding.tvNoFavoriteDishesAddedYet.isVisible = true
                }
            }
        }
        favDishViewModel.favoriteDishesLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (dataError) {
                    Toast.makeText(
                        context,
                        R.string.favorite_dish_list_error_message,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(getString(R.string.favorite_dish_api_error_message), "$dataError")
                }
            }
        }
        favDishViewModel.loadFavoriteDishes.observe(viewLifecycleOwner) { loadFavoriteDishes ->
            loadFavoriteDishes.let {
                Log.e(getString(R.string.favorite_dishes_loading_message), "$loadFavoriteDishes")
                if (loadFavoriteDishes) {
                    showCustomProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun showCustomProgressDialog() {
        progressDialog = Dialog(requireActivity())
        progressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    fun dishDetails(favDish: FavDish, extras: FragmentNavigator.Extras) {
        findNavController().navigate(
            FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(
                favDish
            ),
            extras
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }
}