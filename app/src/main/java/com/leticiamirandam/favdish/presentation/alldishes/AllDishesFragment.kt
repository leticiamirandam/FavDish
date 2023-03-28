package com.leticiamirandam.favdish.presentation.alldishes

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.DialogCustomListBinding
import com.leticiamirandam.favdish.databinding.FragmentAllDishesBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.MainActivity
import com.leticiamirandam.favdish.presentation.addupdate.AddUpdateDishActivity
import com.leticiamirandam.favdish.presentation.common.adapters.CustomListItemAdapter
import com.leticiamirandam.favdish.presentation.common.adapters.FavDishAdapter
import com.leticiamirandam.favdish.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding
    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog: Dialog
    private val allDishesViewModel: AllDishesViewModel by viewModel()
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        addAllDishesViewModelObserver()
    }

    private fun setupRecyclerView() {
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        mBinding.rvDishesList.adapter = mFavDishAdapter
    }

    override fun onStart() {
        super.onStart()
        allDishesViewModel.getAllDishes()
    }

    private fun addAllDishesViewModelObserver() {
        allDishesViewModel.allDishesListResponse.observe(viewLifecycleOwner) { allDishes ->
            allDishes.let {
                if (it.isNotEmpty()) {
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                } else {
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
        allDishesViewModel.deleteDishResponse.observe(viewLifecycleOwner) { deletedDish ->
            deletedDish.let {
                if (deletedDish) {
                    showFeedbackToast(getString(R.string.dish_deleted_successfully_message))
                    allDishesViewModel.getAllDishes()
                } else {
                    Log.e(getString(R.string.all_dishes_api_error_message), "$deletedDish")
                }
            }
        }
        allDishesViewModel.allDishesLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (dataError) {
                    showFeedbackToast(getString(R.string.dish_list_error_message))
                    Log.e(getString(R.string.all_dishes_api_error_message), "$dataError")
                }
            }
        }
        allDishesViewModel.loadAllDishes.observe(viewLifecycleOwner) { loadFavoriteDishes ->
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
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(
            AllDishesFragmentDirections.actionAllDishesToDishDetails(
                favDish
            )
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            allDishesViewModel.deleteDish(dish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = resources.getStringArray(R.array.dish_types).toMutableList()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()
        if (filterItemSelection == Constants.ALL_ITEMS) {
            allDishesViewModel.getAllDishes()
        } else {
            allDishesViewModel.getFilteredList(filterItemSelection)
        }
    }

    private fun showFeedbackToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}