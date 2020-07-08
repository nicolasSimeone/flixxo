package com.flixxo.apps.flixxoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.utils.LocaleHelper
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.OBSecondStepViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OBSecondStepFragment : OBStepFragment(), OnCategorySelected {

    private lateinit var continueButton: Button
    private lateinit var customProgressView: CustomProgressView
    private val viewModel: OBSecondStepViewModel by viewModel()
    private val selectedCategories: MutableList<Int> = mutableListOf()

    companion object {
        fun newInstance() = OBSecondStepFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        customProgressView = CustomProgressView(context!!)
        val view = inflater.inflate(R.layout.onboarding_step2, container, false)

        LocaleHelper.onAttach(this.context!!)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_category_onBoard)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = CategoriesAdapter(onCategorySelected = this)

        continueButton = view.findViewById(R.id.btn_continue_step2)
        continueButton.setOnClickListener {
            viewModel.setFollowedCategories(selectedCategories)
        }

        viewModel.category.observe(this, Observer { value ->
            value?.let {
                recyclerView.adapter = CategoriesAdapter(it, this)
            }
        })

        viewModel.error.observe(this, Observer { value ->
            value?.let { message ->
                Toast.makeText(context, getStringById(message), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.followedCategories.observe(this, Observer {
            super.continueTo()
        })

        viewModel.loading.observe(this, Observer { value ->
            value?.let { show ->
                if (show) {
                    customProgressView.showLoadingDialog()
                } else {
                    customProgressView.hideLoadingDialog()

                }
            }
        })

        viewModel.loadCategories()

        return view
    }

    override fun isCategoryAdded(category: Category): Boolean {
        return selectedCategories.contains(category.id)
    }

    override fun categorySelected(category: Category) {
        if (isCategoryAdded(category)) {
            selectedCategories.remove(category.id)
        } else selectedCategories.add(category.id)

        if (selectedCategories.count() >= 5) {
            continueButton.alpha = 1.0f
            continueButton.isEnabled = true
        } else continueButton.alpha = 0.5f
    }

}
