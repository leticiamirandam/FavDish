package com.leticiamirandam.favdish.presentation.addupdate

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.AddDishUseCase
import com.leticiamirandam.favdish.domain.usecase.UpdateDishUseCase
import kotlinx.coroutines.launch

internal class AddUpdateViewModel(
    private val addDishUseCase: AddDishUseCase,
    private val updateDishUseCase: UpdateDishUseCase,
) : ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        addDishUseCase(dish)
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        updateDishUseCase(dish)
    }

    fun validateDish(resources: Resources, dish: FavDish): String? {
        var errorMesssage: String? = null
        with(dish) {
            if (title.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_enter_dish_title)
            if(type.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_select_dish_type)
            if(category.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_select_dish_category)
            if(ingredients.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_enter_dish_ingredients)
            if(cookingTime.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_select_dish_cooking_time)
            if(directionToCook.isEmpty())
                errorMesssage = resources.getString(R.string.err_msg_enter_dish_cooking_instructions)

        }
        return errorMesssage
    }
}