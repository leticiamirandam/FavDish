package com.leticiamirandam.favdish.presentation.common.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.ItemDishLayoutBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.addupdate.AddUpdateDishActivity
import com.leticiamirandam.favdish.presentation.alldishes.AllDishesFragment
import com.leticiamirandam.favdish.presentation.favoritedishes.FavoriteDishesFragment
import com.leticiamirandam.favdish.utils.Constants

class FavDishAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<FavDishAdapter.FavDishViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class FavDishViewHolder(val view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore

        fun bind (favDish: FavDish) {
            ViewCompat.setTransitionName(view.tvDishTitle, "title_${favDish.id}")
            ViewCompat.setTransitionName(view.ivDishImage, "image_${favDish.id}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavDishViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.requireContext()), parent, false
        )
        return FavDishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavDishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.bind(dish)
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                holder.tvTitle to "title_${dish.id}",
                holder.ivDishImage to "image_${dish.id}")
            if (fragment is AllDishesFragment) {
                fragment.dishDetails(dish, extras)
            }
            if (fragment is FavoriteDishesFragment) {
                fragment.dishDetails(dish, extras)
            }
        }

        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit_dish) {
                    val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                } else if (it.itemId == R.id.action_delete_dish) {
                    if (fragment is AllDishesFragment) {
                        fragment.showDeleteDishDialog(dish)
                    }
                }
                true
            }
            popup.show()
        }
        if (fragment is AllDishesFragment) {
            holder.ibMore.visibility = View.VISIBLE
        }
        if (fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
    }

    private fun openDishDetails() {

    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }
}