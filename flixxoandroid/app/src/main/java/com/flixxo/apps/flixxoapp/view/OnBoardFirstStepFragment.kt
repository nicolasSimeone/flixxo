package com.flixxo.apps.flixxoapp.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.LocaleHelper
import com.flixxo.apps.flixxoapp.viewModel.FirstStepViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class OnBoardFirstStepFragment : OBStepFragment() {
    private val viewModel: FirstStepViewModel by viewModel()

    private lateinit var nameField: TextView
    private lateinit var languageField: Spinner
    private lateinit var countryField: Spinner
    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var otherButton: Button
    private lateinit var continueButton: Button
    var selectedGender = "m"
    private val calendar = Calendar.getInstance()
    private var yearSelected = calendar.get(Calendar.YEAR)
    private var monthSelected = calendar.get(Calendar.MONDAY)
    private var daySelected = calendar.get(Calendar.DAY_OF_MONTH)

    companion object {
        fun newInstance() = OnBoardFirstStepFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.onbording_step1, container, false)

        nameField = view.findViewById(R.id.name)
        languageField = view.findViewById(R.id.language)
        countryField = view.findViewById(R.id.country)
        continueButton = view.findViewById(R.id.continue_step1)

        maleButton = view.findViewById(R.id.male)
        femaleButton = view.findViewById(R.id.female)
        otherButton = view.findViewById(R.id.other)

        viewModel.language.observe(this, androidx.lifecycle.Observer { value ->
            value?.let {
                val adapter = ArrayAdapter(
                    context!!,
                    R.layout.item_onboarding,
                    R.id.ob_autocomplete,
                    it.map { language -> language.nameNative })
                languageField.adapter = adapter
            }
        })

        viewModel.getLanguages()

        val birthDate = view.findViewById<TextView>(R.id.birth_date)
        birthDate.setOnClickListener {
            val alert =
                DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    birthDate.text = """$dayOfMonth/${month + 1}/$year"""
                    yearSelected = year
                    monthSelected = month + 1
                    daySelected = dayOfMonth
                }, yearSelected, monthSelected, daySelected)
            alert.datePicker.maxDate = System.currentTimeMillis()
            alert.show()
        }
        viewModel.country.observe(this, androidx.lifecycle.Observer { value ->
            value?.let {
                val countries = it.sortedWith(compareBy { it.name.value })
                val adapter = ArrayAdapter(
                    context!!,
                    R.layout.item_onboarding,
                    R.id.ob_autocomplete,
                    countries.map { country -> country.name.value })
                countryField.adapter = adapter
            }
        })

        viewModel.loadCountries()


        maleButton.setOnClickListener {
            maleButton.setBackgroundResource(R.drawable.button_step_background)
            femaleButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)
            otherButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)

            selectedGender = "m"
        }

        femaleButton.setOnClickListener {
            femaleButton.setBackgroundResource(R.drawable.button_step_background)
            maleButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)
            otherButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)
            selectedGender = "f"
        }

        otherButton.setOnClickListener {
            femaleButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)
            maleButton.setBackgroundResource(R.drawable.shape_edit_text_onboarding)
            otherButton.setBackgroundResource(R.drawable.button_step_background)
            selectedGender = "o"
        }


        continueButton.setOnClickListener {

            val name = nameField.nonEmpty {
                nameField.error = getString(R.string.incorrectName)
            }

            val birthDateText = birthDate.nonEmpty {
                birthDate.error = getString(R.string.incorrect_birthdate)
            }

            val language = languageField.selectedItem?.toString()

            val country = countryField.selectedItem?.toString()

            if (!name || language.isNullOrEmpty() || country.isNullOrEmpty() || !birthDateText) {
                return@setOnClickListener
            }

            val getLanguage = viewModel.getLanguageCode(language)
            LocaleHelper.setLocale(context!!, getLanguage)

            FirebaseMessaging.getInstance().subscribeToTopic(getLanguage)

            viewModel.updateProfile(
                name = nameField.text.toString(),
                lang = getLanguage,
                country = country,
                gender = selectedGender,
                birthdate = validationDate(yearSelected, monthSelected, daySelected)
            )
            super.continueTo()

        }

        return view
    }


    private fun validationDate(year: Int, month: Int, day: Int): String {
        val date = SimpleDateFormat("$year-$month-$day").toPattern()
        return date.format(date)
    }

}