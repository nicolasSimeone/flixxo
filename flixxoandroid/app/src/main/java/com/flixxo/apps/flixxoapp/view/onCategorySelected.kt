package com.flixxo.apps.flixxoapp.view

import com.flixxo.apps.flixxoapp.model.Category

interface OnCategorySelected {
    fun categorySelected(category: Category)
    fun isCategoryAdded(category: Category): Boolean
}