package com.leticiamirandam.favdish.presentation.favoritedishes

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.FragmentFavoriteDishesBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.presentation.common.adapters.FavDishAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentFavoriteDishesBinding

    private val mFavDishViewModel: FavoriteDishesViewModel by viewModel()
    private lateinit var favDishAdapter: FavDishAdapter
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        favDishAdapter = FavDishAdapter(this@FavoriteDishesFragment)
        mBinding.rvFavoriteDishesList.adapter = favDishAdapter
        favoriteDishesViewModelObserver()
    }

    override fun onStart() {
        super.onStart()
        mFavDishViewModel.getFavoriteDishes()
    }

    private fun favoriteDishesViewModelObserver() {
        mFavDishViewModel.favoriteDishesListResponse.observe(viewLifecycleOwner) { favoriteDishesResponse ->
            favoriteDishesResponse.let {
                if (it.isNotEmpty()) {
                    mBinding.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding.tvNoFavoriteDishesAddedYet.visibility = View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    mBinding.rvFavoriteDishesList.visibility = View.GONE
                    mBinding.tvNoFavoriteDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
        mFavDishViewModel.favoriteDishesLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (dataError) {
                    Toast.makeText(
                        context,
                        "An error has ocurred while trying to get the favorite dishes list.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Favorite Dish API Error", "$dataError")
                }
            }
        }
        mFavDishViewModel.loadFavoriteDishes.observe(viewLifecycleOwner) { loadFavoriteDishes ->
            loadFavoriteDishes.let {
                Log.e("Favorite Dishes Loading", "$loadFavoriteDishes")
                if (loadFavoriteDishes) {
                    showCustomProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
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
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(
            FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(
                favDish
            )
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