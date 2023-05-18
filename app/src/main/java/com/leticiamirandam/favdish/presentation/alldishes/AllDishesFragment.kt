package com.leticiamirandam.favdish.presentation.alldishes

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
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

    private lateinit var binding: FragmentAllDishesBinding
    private lateinit var favDishAdapter: FavDishAdapter
    private lateinit var customListDialog: Dialog
    private val allDishesViewModel: AllDishesViewModel by viewModel()
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        addDishesListObserver()
        addErrorObserver()
        addDeleteDishObserver()
        addLoadingObserver()
    }

    private fun setupRecyclerView() {
        binding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        favDishAdapter = FavDishAdapter(this@AllDishesFragment)
        binding.rvDishesList.apply {
            adapter = favDishAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        allDishesViewModel.getAllDishes()
    }

    private fun addDishesListObserver() {
        allDishesViewModel.allDishesListResponse.observe(viewLifecycleOwner) { allDishes ->
            allDishes.let {
                if (it.isNotEmpty()) {
                    binding.rvDishesList.isVisible = true
                    binding.tvNoDishesAddedYet.visibility = View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    binding.rvDishesList.isVisible = false
                    binding.tvNoDishesAddedYet.isVisible = true
                }
            }
        }
    }

    private fun addErrorObserver() {
        allDishesViewModel.allDishesLoadingError.observe(viewLifecycleOwner) { dataError ->
            dataError.let {
                if (dataError) {
                    showFeedbackToast(getString(R.string.dish_list_error_message))
                    Log.e(getString(R.string.all_dishes_api_error_message), "$dataError")
                }
            }
        }
    }

    private fun addDeleteDishObserver() {
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
    }

    private fun addLoadingObserver() {
        allDishesViewModel.loadAllDishes.observe(viewLifecycleOwner) { loadFavoriteDishes ->
            loadFavoriteDishes.let {
                Log.e(getString(R.string.favorite_dishes_loading_message), "$loadFavoriteDishes")
                if (loadFavoriteDishes) {
                    showProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun showProgressDialog() {
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
            AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish), extras
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    fun showDeleteDishDialog(dish: FavDish) {
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

    private fun showFilterDishesListDialog() {
        customListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)
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
        customListDialog.show()
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
            }
            R.id.action_filter_dishes -> {
                showFilterDishesListDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection: String) {
        customListDialog.dismiss()
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