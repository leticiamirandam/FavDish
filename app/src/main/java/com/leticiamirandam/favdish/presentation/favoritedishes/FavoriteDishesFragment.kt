package com.leticiamirandam.favdish.presentation.favoritedishes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.leticiamirandam.favdish.databinding.FragmentFavoriteDishesBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.presentation.common.adapters.FavDishAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteDishesFragment : Fragment() {

    private var mBinding : FragmentFavoriteDishesBinding? = null

    private val mFavDishViewModel : FavoriteDishesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val favDishAdapter = FavDishAdapter(this@FavoriteDishesFragment)
        mBinding!!.rvFavoriteDishesList.adapter = favDishAdapter

        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding!!.tvNoFavoriteDishesAddedYet.visibility = View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(
            favDish
        ))
        if(requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}