package com.leticiamirandam.favdish.presentation.favoritedishes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.di.FavDishApplication
import com.leticiamirandam.favdish.databinding.FragmentFavoriteDishesBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.presentation.common.adapters.FavDishAdapter

class FavoriteDishesFragment : Fragment() {

    private var mBinding : FragmentFavoriteDishesBinding? = null

    private val mFavDishViewModel : FavoriteDishesViewModel by viewModels {
        FavoriteDishesViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

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
                    favDishAdapter.dishesList(it.map { favDish ->
                        CacheToDomainMapper().mapFavDishCMToFavDish(favDish)
                    })
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