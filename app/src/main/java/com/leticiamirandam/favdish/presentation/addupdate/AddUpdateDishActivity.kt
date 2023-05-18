package com.leticiamirandam.favdish.presentation.addupdate

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.databinding.ActivityAddUpdateDishBinding
import com.leticiamirandam.favdish.databinding.DialogCustomImageSelectionBinding
import com.leticiamirandam.favdish.databinding.DialogCustomListBinding
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.presentation.common.adapters.CustomListItemAdapter
import com.leticiamirandam.favdish.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

private const val IMAGE_QUALITY = 100

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateDishBinding
    private lateinit var customListDialog: Dialog
    private var favDishDetails: FavDish? = null
    private var imagePath: String = ""
    private val addUpdateViewModel: AddUpdateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_fade_out)

        setContentView(binding.root)
        setupOnClickListeners()
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            favDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }
        favDishDetails?.let {
            if (it.id != 0)
                showFavDishDetails(it)
        }
    }

    private fun showFavDishDetails(favDish: FavDish) {
        imagePath = favDish.image
        Glide.with(this@AddUpdateDishActivity)
            .load(imagePath)
            .centerCrop()
            .into(binding.ivDishImage)
        binding.etTitle.setText(favDish.title)
        binding.etType.setText(favDish.type)
        binding.etCategory.setText(favDish.category)
        binding.etIngredients.setText(favDish.ingredients)
        binding.etCookingTime.setText(favDish.cookingTime)
        binding.etDirectionToCook.setText(favDish.directionToCook)
        binding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
    }

    private fun setupOnClickListeners() {
        binding.ivAddDishImage.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etCookingTime.setOnClickListener(this)
        binding.btnAddDish.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_add_dish_image -> {
                customImageSelectionDialog()
            }
            R.id.et_type -> {
                showCustomItemsDialog(
                    resources.getString(R.string.title_select_dish_type),
                    resources.getStringArray(R.array.dish_types).toList(),
                    Constants.DISH_TYPE
                )
            }
            R.id.et_category -> {
                showCustomItemsDialog(
                    resources.getString(R.string.title_select_dish_category),
                    resources.getStringArray(R.array.dish_categories).toList(),
                    Constants.DISH_CATEGORY
                )
            }
            R.id.btn_add_dish -> {
                addDish()
            }
        }
    }

    private fun addDish() {
        val title = binding.etTitle.getTextFormatted()
        val type = binding.etType.getTextFormatted()
        val category = binding.etCategory.getTextFormatted()
        val ingredients = binding.etIngredients.getTextFormatted()
        val cookingTimeInMinutes = binding.etCookingTime.getTextFormatted()
        val cookingDirection = binding.etDirectionToCook.getTextFormatted()
        when {
            TextUtils.isEmpty(imagePath) -> {
                showFeedbackToast(
                    this@AddUpdateDishActivity,
                    getString(R.string.err_msg_select_dish_image)
                )
            }
            else -> {
                var dishID = 0
                var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                var favoriteDish = false

                favDishDetails?.let {
                    if (it.id != 0) {
                        dishID = it.id!!
                        imageSource = it.imageSource
                        favoriteDish = it.favoriteDish
                    }
                }

                val favDishDetails = FavDish(
                    image = imagePath,
                    imageSource = imageSource,
                    title = title,
                    type = type,
                    category = category,
                    ingredients = ingredients,
                    cookingTime = cookingTimeInMinutes,
                    directionToCook = cookingDirection,
                    favoriteDish = favoriteDish,
                    id = dishID
                )
                val error = addUpdateViewModel.validateDish(resources, favDishDetails)
                if (error.isNullOrEmpty()) {
                    if (dishID == 0) {
                        addNewDish(favDishDetails)
                    } else {
                        updateDish(favDishDetails)
                    }
                    finish()
                } else {
                    showFeedbackToast(this, error)
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_slide_out)
    }

    private fun addNewDish(favDish: FavDish) {
        addUpdateViewModel.insert(favDish)
        showFeedbackToast(this, getString(R.string.dish_details_added_successfully))
    }

    private fun updateDish(favDish: FavDish) {
        addUpdateViewModel.update(favDish)
        showFeedbackToast(this, getString(R.string.dish_details_updated_successfully))
    }

    private fun EditText.getTextFormatted(): String {
        return text.toString().trim { it <= ' ' }
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.DISH_TYPE -> {
                customListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                customListDialog.dismiss()
                binding.etCategory.setText(item)
            }
            else -> {
                customListDialog.dismiss()
                binding.etCookingTime.setText(item)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap =
                        data.extras!!.get("data") as Bitmap
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(binding.ivDishImage)
                    imagePath = saveImageToInternalStorage(thumbnail)
                    binding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            } else if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    imagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }
                        })
                        .centerCrop()
                        .into(binding.ivDishImage)
                    binding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddDishActivity)
        if (favDishDetails != null && favDishDetails!!.id != 0) {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        } else {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        binding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun customImageSelectionDialog() {
        val mCustomListDialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

            mCustomListDialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {

            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        showFeedbackToast(
                            this@AddUpdateDishActivity,
                            getString(R.string.storage_permission_denied_message)
                        )
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalDialogForPermissions()
                    }
                })
                .onSameThread()
                .check()

            mCustomListDialog.dismiss()
        }
        mCustomListDialog.show()
    }


    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.permission_settings_message))
            .setPositiveButton(getString(R.string.permission_settings_positive_button)) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.png")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            Log.e("Image error: ", e.stackTraceToString())
        }
        return file.absolutePath
    }

    private fun showCustomItemsDialog(title: String, itemsList: List<String>, selection: String) {
        customListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        binding.rvList.adapter = adapter
        customListDialog.show()
    }

    private fun showFeedbackToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}
