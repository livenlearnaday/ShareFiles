package io.github.livenlearnaday.sharefiles

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import io.github.livenlearnaday.sharefiles.databinding.FragmentShareFilesBinding


class ShareFilesFragment : Fragment() {

//    private val REQUEST_SHARE_ALL_FILES_CODE = 1110
//    private val REQUEST_SHARE_FILES_CODE = 1111
//    private val REQUEST_SHARE_AN_IMAGE_CODE = 1112
//    private val REQUEST_SHARE_A_VIDEO_FILE_CODE = 1113
//    private val REQUEST_SHARE_A_PDF_FILE_CODE = 1114

    private var _binding: FragmentShareFilesBinding? = null
    private val binding get() = _binding!!

    var fileUriList = arrayListOf<Uri>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareFilesBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()

    }


    private fun initListener() {
        binding.shareAllFilesWithDialog.setOnClickListener {
            shareFilesWithDialog()
        }
        binding.shareAllFiles.setOnClickListener {
            pickAllFiles()
        }
        binding.shareImageVideoPdfFiles.setOnClickListener {
            pickFiles()
        }
        binding.shareImageFile.setOnClickListener {
            pickImageFile()
        }
        binding.shareVideoFile.setOnClickListener {
            pickVideoFile()
        }
        binding.sharePdfFile.setOnClickListener {
            pickPdfFile()
        }
    }


    private fun shareFilesWithDialog() {

        val choice =
            arrayOf<CharSequence>("Share files", "Cancel")
        val myAlertDialog = AlertDialog.Builder(requireContext())
        myAlertDialog.setTitle("Select files")
        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { _, item ->
            when {
                choice[item] == "Share files" -> {
                    pickAllFiles()
                }
                else -> {
                    myAlertDialog.setOnDismissListener { dialog -> dialog.dismiss() }
                }
            }
        })
        myAlertDialog.setCancelable(true)
        myAlertDialog.show()
    }


    private fun pickImageFile() {
        val fileUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val intent = Intent().apply {
            action = Intent.ACTION_PICK
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
//        startActivityForResult(intent, REQUEST_SHARE_AN_IMAGE_CODE)
        shareAnImageFileForResult.launch(intent)
    }

    private fun pickVideoFile() {
        val fileUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val intent = Intent().apply {
            action = Intent.ACTION_PICK
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
//        startActivityForResult(intent, REQUEST_SHARE_A_VIDEO_FILE_CODE)
        shareAVideoFileForResult.launch(intent)

    }


    private fun pickPdfFile() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
//        startActivityForResult(intent, REQUEST_SHARE_A_PDF_FILE_CODE)
        shareAPdfFileForResult.launch(intent)

    }

    private fun pickFiles() {
        val intent = Intent().apply {
            type = "image/*, video/*, application/pdf"
            val mimeTypes = arrayOf("image/*", "video/*", "application/pdf")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            if (Build.VERSION.SDK_INT < 19) {
                action = Intent.ACTION_GET_CONTENT
            } else {
                action = Intent.ACTION_OPEN_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
//        startActivityForResult(intent, REQUEST_SHARE_FILES_CODE)
        shareFilesWithDefinedFormatsForResult.launch(intent)
    }


    private fun pickAllFiles() {
        val intent = Intent().apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            if (Build.VERSION.SDK_INT < 19) {
                action = Intent.ACTION_GET_CONTENT
            } else {
                action = Intent.ACTION_OPEN_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
//        startActivityForResult(intent, REQUEST_SHARE_ALL_FILES_CODE)
        shareAllFilesForResult.launch(intent)
    }

    private fun shareAllSelectedFiles() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUriList)
        }
        try {
            startActivity(Intent.createChooser(sendIntent, "Share files using ..."))
            fileUriList.clear()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareSelectedFilesWithDefinedFormats() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/*, video/*, application/pdf"
            val mimeTypes = arrayOf("image/*", "video/*", "application/pdf")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUriList)
        }
        try {
            startActivity(Intent.createChooser(sendIntent, "Share files using ..."))
            fileUriList.clear()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No App Available", Toast.LENGTH_SHORT).show()
        }
    }


    private fun shareImageFile(fileUri: Uri) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share an image file using ...")
        startActivity(shareIntent)
    }

    private fun shareVideoFile(fileUri: Uri) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share a video file using ...")
        startActivity(shareIntent)
    }


    private fun sharePdfFile(fileUri: Uri) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share a pdf file using ...")
        startActivity(shareIntent)
    }

    private val shareAllFilesForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data?.getClipData() != null) {
                    val count = result.data!!.clipData?.itemCount
                    if (count != null) {
                        for (i in 0..count - 1) {
                            val fileUriFromClipData = result.data!!.clipData?.getItemAt(i)?.uri
                            if (fileUriFromClipData != null) {
                                if (!containsSuchFileUri(fileUriFromClipData, fileUriList)) {
                                    fileUriList.add(fileUriFromClipData)
                                }
                            }
                        }
                    }

                } else if (result.data?.getData() != null) {
                    val fileUri = result.data!!.data!!
                    fileUriList.add(fileUri)
                }

                shareAllSelectedFiles()
            }
        }

    private val shareFilesWithDefinedFormatsForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data?.getClipData() != null) {
                    val count = result.data!!.clipData?.itemCount
                    if (count != null) {
                        for (i in 0..count - 1) {
                            val fileUriFromClipData = result.data!!.clipData?.getItemAt(i)?.uri
                            if (fileUriFromClipData != null) {
                                if (!containsSuchFileUri(fileUriFromClipData, fileUriList)) {
                                    fileUriList.add(fileUriFromClipData)
                                }
                            }
                        }
                    }

                } else if (result.data?.getData() != null) {
                    val fileUri = result.data!!.data!!
                    fileUriList.add(fileUri)
                }

                shareSelectedFilesWithDefinedFormats()

            }
        }

    private val shareAnImageFileForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val fileUri: Uri = result.data!!.data!!
                    shareImageFile(fileUri)
                }
            }
        }

    private val shareAVideoFileForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val fileUri: Uri = result.data!!.data!!
                    shareVideoFile(fileUri)
                }
            }
        }

    private val shareAPdfFileForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val fileUri: Uri = result.data!!.data!!
                    sharePdfFile(fileUri)
                }
            }
        }

    private fun containsSuchFileUri(uri: Uri, listUri: ArrayList<Uri>): Boolean {
        for (uriInList in listUri) {
            if (uriInList == uri) {
                return true
            }
        }
        return false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {

                REQUEST_SHARE_ALL_FILES_CODE -> {
                    if (data?.getClipData() != null) {
                        val count = data.clipData?.itemCount
                        if (count != null) {
                            for (i in 0..count - 1) {
                                val fileUriFromClipData = data.clipData?.getItemAt(i)?.uri
                                if (fileUriFromClipData != null) {
                                    if (!containsSuchFileUri(fileUriFromClipData, fileUriList)) {
                                        fileUriList.add(fileUriFromClipData)
                                    }
                                }
                            }
                        }

                    } else if (data?.getData() != null) {
                        val fileUri = data.data!!
                        fileUriList.add(fileUri)
                    }

                    shareAllSelectedFiles()

                }

                REQUEST_SHARE_FILES_CODE -> {
                    if (data?.getClipData() != null) {
                        val count = data.clipData?.itemCount
                        if (count != null) {
                            for (i in 0..count - 1) {
                                val fileUriFromClipData = data.clipData?.getItemAt(i)?.uri
                                if (fileUriFromClipData != null) {
                                    if (!containsSuchFileUri(fileUriFromClipData, fileUriList)) {
                                        fileUriList.add(fileUriFromClipData)
                                    }
                                }
                            }
                        }

                    } else if (data?.getData() != null) {
                        val fileUri = data.data!!
                        fileUriList.add(fileUri)
                    }

                    shareSelectedFilesWithDefinedFormats()

                }

                REQUEST_SHARE_AN_IMAGE_CODE -> {
                    val fileUri: Uri = data?.data!!
                    shareImageFile(fileUri)
                }

                REQUEST_SHARE_A_VIDEO_FILE_CODE -> {
                    val fileUri: Uri = data?.data!!
                    shareVideoFile(fileUri)
                }

                REQUEST_SHARE_A_PDF_FILE_CODE -> {
                    val fileUri: Uri = data?.data!!
                    sharePdfFile(fileUri)
                }
            }
        }
    }*/


}