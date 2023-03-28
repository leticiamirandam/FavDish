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

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""

    private lateinit var mCustomListDialog: Dialog
    private var mFavDishDetails: FavDish? = null

    private val addUpdateViewModel: AddUpdateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setupActionBar()
        mFavDishDetails?.let { setupFavDishOnUI(it) }
        setupListeners()
    }

    private fun setupFavDishOnUI(favDish: FavDish) {
        if (favDish.id != 0) {
            mImagePath = favDish.image
            Glide.with(this@AddUpdateDishActivity)
                .load(mImagePath)
                .centerCrop()
                .into(mBinding.ivDishImage)
            mBinding.etTitle.setText(favDish.title)
            mBinding.etType.setText(favDish.type)
            mBinding.etCategory.setText(favDish.category)
            mBinding.etIngredients.setText(favDish.ingredients)
            mBinding.etCookingTime.setText(favDish.cookingTime)
            mBinding.etDirectionToCook.setText(favDish.directionToCook)
            mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
        }
    }

    private fun setupListeners() {
        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_add_dish_image -> {
                customImageSelectionDialog()
                return
            }
            R.id.et_type -> {
                customItemsDialog(
                    resources.getString(R.string.title_select_dish_type),
                    resources.getStringArray(R.array.dish_types).toList(),
                    Constants.DISH_TYPE
                )
                return
            }
            R.id.et_category -> {
                customItemsDialog(
                    resources.getString(R.string.title_select_dish_category),
                    resources.getStringArray(R.array.dish_categories).toList(),
                    Constants.DISH_CATEGORY
                )
                return
            }
            R.id.btn_add_dish -> {
                addDish()
            }
        }
    }

    private fun addDish() {
        val title = mBinding.etTitle.getTextFormatted()
        val type = mBinding.etType.getTextFormatted()
        val category = mBinding.etCategory.getTextFormatted()
        val ingredients = mBinding.etIngredients.getTextFormatted()
        val cookingTimeInMinutes = mBinding.etCookingTime.getTextFormatted()
        val cookingDirection = mBinding.etDirectionToCook.getTextFormatted()
        when {
            TextUtils.isEmpty(mImagePath) -> {
                showFeedbackToast(
                    this@AddUpdateDishActivity,
                    getString(R.string.err_msg_select_dish_image)
                )
            }
            else -> {
                var dishID = 0
                var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                var favoriteDish = false

                mFavDishDetails?.let {
                    if (it.id != 0) {
                        dishID = it.id!!
                        imageSource = it.imageSource
                        favoriteDish = it.favoriteDish
                    }
                }

                val favDishDetails = FavDish(
                    image = mImagePath,
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
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
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
                        .into(mBinding.ivDishImage)
                    mImagePath = saveImageToInternalStorage(thumbnail)
                    mBinding.ivAddDishImage.setImageDrawable(
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
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }
                        })
                        .centerCrop()
                        .into(mBinding.ivDishImage)
                    mBinding.ivAddDishImage.setImageDrawable(
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
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if (mFavDishDetails != null && mFavDishDetails!!.id != 0) {
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

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
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
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun customItemsDialog(title: String, itemsList: List<String>, selection: String) {
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
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
