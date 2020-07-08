package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.utils.inflate
import kotlinx.android.synthetic.main.item_obstep2.view.*

class CategoriesAdapter(
    private var category: List<Category> = ArrayList(),
    private var onCategorySelected: OnCategorySelected
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder {
        val inflatedView = parent.inflate(R.layout.item_obstep2)
        return CategoriesHolder(inflatedView, onCategorySelected)
    }

    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {
        val category = category[position]
        holder.bind(category)
        if (onCategorySelected.isCategoryAdded(category)) {
            holder.select()
        } else {
            holder.unselect()
        }
    }

    override fun getItemCount(): Int = category.count()

    class CategoriesHolder(private val view: View, private var onCategorySelected: OnCategorySelected) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var category: Category

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (onCategorySelected.isCategoryAdded(category)) {
                unselect()
            } else {
                select()
            }
            onCategorySelected.categorySelected(category)
        }

        fun select() {
            val item = view.findViewById<ConstraintLayout>(R.id.category_onboard)
            val check = view.findViewById<CardView>(R.id.circle_check)
            item.background = view.context.resources.getDrawable(R.drawable.category_selected)
            check.visibility = View.VISIBLE
        }

        fun unselect() {
            val item = view.findViewById<ConstraintLayout>(R.id.category_onboard)
            val check = view.findViewById<CardView>(R.id.circle_check)
            item.background = view.context.resources.getDrawable(R.drawable.category_background)
            check.visibility = View.GONE

        }

        fun bind(category: Category) {
            this.category = category
            view.category_name_ob.text = category.name?.toUpperCase()

            val imageName = "ic_${category.name?.toLowerCase()?.replace("-", "_")}"
            getDrawableFromName(view.context, imageName)?.let {
                view.category_image.setImageDrawable(it)
            }
        }

        private fun getDrawableFromName(context: Context, name: String): Drawable? {
            val resources = context.resources
            val resourceId = resources.getIdentifier(name, "drawable", context.packageName)

            return try {
                resources.getDrawable(resourceId, null)
            } catch (e: Resources.NotFoundException) {
                null
            }

        }
    }
}