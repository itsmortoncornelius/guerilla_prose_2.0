package de.handler.mobile.guerillaprose.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import de.handler.mobile.guerillaprose.BuildConfig
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.*
import de.handler.mobile.guerillaprose.loadUrl
import kotlinx.android.synthetic.main.fragment_create_prose.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import kotlin.coroutines.experimental.CoroutineContext

class CreateProseFragment : Fragment(), CoroutineScope {
    private val guerillaProseRepository: GuerillaProseRepository by inject()
    private val flickrRepository: FlickrRepository by inject()
    private val userRepository: UserRepository by inject()
    private val picasso: Picasso by inject()

    private var file: File? = null
    private var flickrInfo: FlickrInfo? = null

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_prose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        proseImageView.setOnClickListener {
            // openGallery()
            openCamera()
        }

        flickrRepository.getRandomFlickrImages("street art").observe(this, Observer {
            flickrInfo = it.shuffled().firstOrNull()
            proseImageView.loadUrl(picasso, flickrInfo?.imageUrl, height = 800)
            textViewAuthor.text = getString(R.string.text_image_copyright, flickrInfo?.owner)
        })

        textViewAuthor.setOnClickListener {
            if (flickrInfo?.ownerUrl?.isEmpty() == true) return@setOnClickListener
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flickrInfo?.ownerUrl))
            activity?.packageManager?.let { packageManager ->
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        sendProseFab.setOnClickListener {
            launch {
                val fileInfo: FileInfo? = if (file != null) {
                    guerillaProseRepository.uploadImage(file!!).await()
                } else {
                    flickrInfo?.imageUrl?.let { it1 -> FileInfo(url = it1, name = "") }
                }

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val userId = sharedPreferences.getString(MainActivity.KEY_USER_ID, null)
                val user = userRepository.getUser(userId).await()
                if (user?.id.isNullOrBlank()) {
                    Timber.e("No user is set")
                    val navController = view.findNavController()
                    navController.navigate(R.id.actionCreateProfile)
                } else {
                    createGuerillaProseAndNavigate(GuerillaProse(
                            text = proseText.text.toString(),
                            imageUrl = fileInfo?.url,
                            label = "street art",
                            userId = userRepository.user?.id!!))
                }
            }
        }
    }

    private fun createGuerillaProseAndNavigate(guerillaProse: GuerillaProse) = launch {
        progressBar.visibility = View.VISIBLE

        guerillaProseRepository.createGuerillaProse(guerillaProse).await()

        progressBar.visibility = View.GONE
        fragmentManager?.popBackStack()
    }

    private fun openGallery() {
        if (activity == null) return

        if (PermissionManager.permissionPending(
                        activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionManager.requestPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    REQUEST_CODE_STORAGE_GALLERY_PERMISSION)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_GALLERY)
            }
        }
    }

    private fun openCamera() {
        if (activity == null) return

        if (PermissionManager.permissionPending(
                        activity!!,
                        Manifest.permission.CAMERA)) {
            PermissionManager.requestPermissions(
                    this,
                    Manifest.permission.CAMERA,
                    REQUEST_CODE_CAMERA_PERMISSION)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                file = FileManager.createFile(activity!!, ".jpg")
                file?.let {
                    val currentPhotoUri = FileProvider.getUriForFile(
                            activity!!,
                            BuildConfig.APPLICATION_ID,
                            it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                    startActivityForResult(intent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    private fun parseCameraResult() {
        // File has been created before camera intent is fired as it
        // has to be added as extra to the camera intent -> here the
        // file should contain the camera image
        setImage(file)
    }

    private fun parseGalleryResult(data: Intent?) {
        file = activity?.contentResolver?.let { FileManager.resolveFileFromIntent(it, data) }
        setImage(file)
    }

    private fun setImage(file: File?) {
        file?.path?.let {
            val bitmap = getScaledImage(proseImageView.measuredHeight.toFloat(), it)
            proseImageView.setImageBitmap(bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> parseCameraResult()
                REQUEST_CODE_GALLERY -> parseGalleryResult(data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val action = when (requestCode) {
            REQUEST_CODE_CAMERA_PERMISSION -> { granted: Boolean ->
                when (granted) {
                    true -> openCamera()
                    else -> PermissionManager.requestPermission(
                            activity!!,
                            Manifest.permission.CAMERA,
                            REQUEST_CODE_CAMERA_PERMISSION)
                }
            }
            REQUEST_CODE_STORAGE_GALLERY_PERMISSION -> { granted: Boolean ->
                when (granted) {
                    true -> openGallery()
                    else -> PermissionManager.requestPermissions(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            REQUEST_CODE_STORAGE_GALLERY_PERMISSION)
                }
            }
            else -> {
                {}
            }
        }
        PermissionManager.handlePermissionsResult(activity!!, grantResults, permissions, action)
    }

    private fun getScaledImage(size: Float, filePath: String): Bitmap? {
        return try {
            // Get the dimensions of the bitmap
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            val photoW = options.outWidth
            val photoH = options.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / size, photoH / size)

            // Decode the image file into a Bitmap sized to fill the View
            options.inJustDecodeBounds = false
            options.inSampleSize = Math.round(scaleFactor)

            val exif = ExifInterface(filePath)
            val orientationString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation = when {
                orientationString != null -> Integer.parseInt(orientationString)
                else -> ExifInterface.ORIENTATION_NORMAL
            }

            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                else -> 0F
            }

            val bitmap = BitmapFactory.decodeFile(filePath, options)

            val matrix = Matrix()
            matrix.setRotate(rotationAngle, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())

            Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA: Int = 942
        private const val REQUEST_CODE_GALLERY: Int = 933
        const val REQUEST_CODE_CAMERA_PERMISSION: Int = 924
        const val REQUEST_CODE_STORAGE_GALLERY_PERMISSION: Int = 915
    }
}
