package de.handler.mobile.guerillaprose

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import org.koin.android.ext.android.inject
import java.io.File
import kotlin.coroutines.experimental.CoroutineContext

class CreateFragment : Fragment() {
    private val guerillaProseProvider: GuerillaProseProvider by inject()
    private val guerillaProseRepository: GuerillaProseRepository by inject()
    private var file: File? = null

    private val job = Job()
    private val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guerilla_image.setOnClickListener {
            openGallery()
        }

        sendGuerillaProseFab.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            createGuerillaProse(GuerillaProse(
                    text = "There is so much beauty in the world",
                    imageUrl = "http://4.bp.blogspot.com/-1lMLh4GjtKw/VavHUYZ6f8I/AAAAAAAAZHo/dPilDnabLiM/s1600/AB%2B11131103504_2e47079f32_b.jpg",
                    label = "beauty",
                    userId = "0"))

            progressBar.visibility = View.GONE

            fragmentManager?.popBackStack()
        }
    }

    private fun createGuerillaProse(guerillaProse: GuerillaProse): Deferred<GuerillaProse?> {
        return GlobalScope.async(coroutineContext) {
            try {
                val createdGuerillaProse = guerillaProseProvider.createGuerillaProse(guerillaProse).await()
                guerillaProseRepository.getGuerillaProses()
                return@async createdGuerillaProse
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                return@async null
            }
        }
    }

    private fun openGallery() {
        if (activity == null) return

        if (PermissionManager.permissionPending(
                        activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionManager.requestPermission(
                    activity!!,
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
            PermissionManager.requestPermission(
                    activity!!,
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

    }

    private fun parseGalleryResult(data: Intent?) {
        file = activity?.contentResolver?.let { FileManager.resolveFileFromIntent(it, data) }

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
                    else -> PermissionManager.requestPermission(
                            activity!!,
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

    companion object {
        private const val REQUEST_CODE_CAMERA: Int = 942
        private const val REQUEST_CODE_GALLERY: Int = 933
        private const val REQUEST_CODE_CAMERA_PERMISSION: Int = 924
        private const val REQUEST_CODE_STORAGE_GALLERY_PERMISSION: Int = 915
    }
}
