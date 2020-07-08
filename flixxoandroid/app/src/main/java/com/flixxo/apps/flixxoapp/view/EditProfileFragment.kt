package com.flixxo.apps.flixxoapp.view

import android.Manifest
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.BuildConfig
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.*
import com.flixxo.apps.flixxoapp.viewModel.EditProfileViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModel()
    private var selectedGenderEdit = "m"
    private lateinit var customProgressView: CustomProgressView
    private var lang: String = ""
    private val completeDate: String = ""
    private val date: String = ""
    private var yearSelected: Int = 0
    private var monthSelected: Int = 0
    private var daySelected: Int = 0
    private var photoCamera: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)// Config
    private var currentPhotoPath: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var builder = StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        customProgressView = CustomProgressView(context!!)

        viewModel.user.observe(this, Observer { value ->
            value?.let {
                val nameEdit = view.findViewById<TextView>(R.id.name_edit)
                nameEdit.text = it.profile.realName.toString().trim()

                val birthDate = view.findViewById<TextView>(R.id.birth_date)

                val selectedDate = date.divideDate(it.profile.birthDate)
                yearSelected = selectedDate.first
                monthSelected = selectedDate.second
                daySelected = selectedDate.third

                birthDate.text = viewModel.getFormatBirthdate()
                birthDate.setOnClickListener {
                    val alert = DatePickerDialog(
                        context!!,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            yearSelected = year
                            monthSelected = month
                            daySelected = dayOfMonth
                            birthDate.text = completeDate.formatDate(daySelected, monthSelected, yearSelected)
                        },
                        yearSelected,
                        monthSelected,
                        daySelected
                    )
                    alert.datePicker.maxDate = System.currentTimeMillis()
                    alert.show()
                }
                val selectedGenderProfile = it.profile.gender

                when (selectedGenderProfile) {
                    "1" -> {
                        female_edit.setBackgroundResource(R.drawable.button_step_background)
                        male_edit.setBackgroundResource(R.drawable.edit_text_background)
                        other_edit.setBackgroundResource(R.drawable.edit_text_background)
                    }
                    "2" -> {
                        male_edit.setBackgroundResource(R.drawable.button_step_background)
                        female_edit.setBackgroundResource(R.drawable.edit_text_background)
                        other_edit.setBackgroundResource(R.drawable.edit_text_background)
                    }
                    else -> {
                        other_edit.setBackgroundResource(R.drawable.button_step_background)
                        female_edit.setBackgroundResource(R.drawable.edit_text_background)
                        male_edit.setBackgroundResource(R.drawable.edit_text_background)
                    }
                }

                lang = it.profile.lang!!

                lang_edit.setSelection(viewModel.getLanguagePosition(lang))
                country_edit.setSelection(viewModel.getCountryNamePosition())

                it.profile.avatar?.let { url ->
                    avatar_edit.loadFrom(url)
                } ?: run {
                    avatar_edit.setBackgroundResource(R.drawable.ic_profile_user)
                }
            }
        })


        val countryAuto = view.findViewById<Spinner>(R.id.country_edit)

        viewModel.country.observe(this, Observer { value ->
            value?.let {
                val adapter = ArrayAdapter(
                    activity!!.applicationContext,
                    R.layout.item_language,
                    R.id.language_autocomplete,
                    it.map { country -> country.name.value })
                countryAuto.adapter = adapter
            }
        })

        viewModel.loadCountries()

        val langAuto = view.findViewById<Spinner>(R.id.lang_edit)

        viewModel.language.observe(this, Observer { value ->
            value?.let {
                val adapter = ArrayAdapter(
                    activity!!.applicationContext,
                    R.layout.item_language,
                    R.id.language_autocomplete,
                    it.map { language -> language.nameNative })
                langAuto.adapter = adapter
            }
        })

        viewModel.getLanguages()



        male_edit.setOnClickListener {
            male_edit.setBackgroundResource(R.drawable.button_step_background)
            female_edit.setBackgroundResource(R.drawable.edit_text_background)
            other_edit.setBackgroundResource(R.drawable.edit_text_background)

            selectedGenderEdit = "m"
        }

        female_edit.setOnClickListener {
            female_edit.setBackgroundResource(R.drawable.button_step_background)
            male_edit.setBackgroundResource(R.drawable.edit_text_background)
            other_edit.setBackgroundResource(R.drawable.edit_text_background)

            selectedGenderEdit = "f"
        }

        other_edit.setOnClickListener {
            other_edit.setBackgroundResource(R.drawable.button_step_background)
            female_edit.setBackgroundResource(R.drawable.edit_text_background)
            male_edit.setBackgroundResource(R.drawable.edit_text_background)

            selectedGenderEdit = "o"
        }

        change_photo.setOnClickListener {
            showAlertDialog()
        }

        submit_edit.setOnClickListener {
            val name = name_edit.nonEmpty {
                name_edit.error = getString(R.string.incorrectName)
            }

            if (!name) {
                return@setOnClickListener
            }

            val getLanguage = viewModel.getLanguageCode(lang_edit.selectedItem.toString())

            if (lang != getLanguage) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(lang)
                FirebaseMessaging.getInstance().subscribeToTopic(getLanguage)
            }

            viewModel.updateProfile(
                name = name_edit.text.toString(),
                lang = getLanguage,
                country = country_edit.selectedItem.toString(),
                gender = selectedGenderEdit,
                birthdate = validationDate(yearSelected, monthSelected, daySelected)
            )

            viewModel.userUpdate.observe(this, Observer { value ->
                value?.let {
                    Toast.makeText(activity, getString(R.string.changedSuccesfully), Toast.LENGTH_SHORT).show()
                }
            })

            viewModel.error.observe(this, Observer { value ->
                value?.let { message ->
                    Toast.makeText(activity, getStringById(message), Toast.LENGTH_SHORT).show()
                }
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

            var mLanguageCode = getLanguage
            LocaleHelper.setLocale(context!!, mLanguageCode)

            val intent = Intent(context!!, MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()

        }

        viewModel.loadProfile()
    }

    private fun showAlertDialog() {
        val alert = AlertDialog.Builder(context!!, R.style.CustomDialog)

        alert.setTitle(getString(R.string.changeProfilePhoto))
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 1)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    val permissionStorage = ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    val permissionCamera = ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.CAMERA
                    )

                    if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                        makeRequestStorage()
                    }
                    if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                        makeRequestCamera()
                    } else {
                        dispatchTakePictureIntent()
                    }
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    avatar_edit.setImageBitmap(null)
                    viewModel.deletePhoto()
                }
            }
        }

        alert.setPositiveButton(getString(R.string.chooseFromLibrary), dialogClickListener)
        alert.setNeutralButton(getString(R.string.removePhoto), dialogClickListener)
        alert.setNegativeButton(getString(R.string.takePhoto), dialogClickListener)
        alert.setCancelable(true)
        alert.show()
    }

    private fun validationDate(year: Int, month: Int, day: Int): String {
        val date = SimpleDateFormat("$year-$month-$day").toPattern()
        return date.format(date)
    }

    private val STORAGE_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val REQUEST_TAKE_PHOTO = 0

    private fun makeRequestStorage() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE
        )
    }


    private fun makeRequestCamera() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                activity!!,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {

                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity!!.applicationContext,
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        when (requestCode) {
            0 -> {
                photoCamera = BitmapFactory.decodeFile(currentPhotoPath)
                photoCamera = rotateImage(currentPhotoPath)

                avatar_edit.setImageBitmap(photoCamera)
                viewModel.changePhoto(avatar = photoCamera.getImagePath(activity!!.applicationContext))
            }
            1 -> {
                if (data == null) return
                val photoGallery = data.data
                avatar_edit.setImageURI(photoGallery)
                viewModel.changePhoto(avatar = photoGallery!!.getFullPath(activity!!.applicationContext))
            }
        }
    }

    private fun rotateImage(path: String): Bitmap {
        var exif = ExifInterface(currentPhotoPath)
        var orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        var matrix: Matrix = Matrix()
        if (orientation == ORIENTATION_ROTATE_90) {
            matrix.postRotate(90F)
        } else if (orientation == ORIENTATION_ROTATE_180) {
            matrix.postRotate(180F)
        } else if (orientation == ORIENTATION_ROTATE_270) {
            matrix.postRotate(270F)
        }
        return Bitmap.createBitmap(
            photoCamera,
            0,
            0,
            photoCamera.getWidth(),
            photoCamera.getHeight(),
            matrix,
            true
        )

    }
}