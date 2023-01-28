package com.example.criminalintent.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.getScaledBitmap
import java.io.File

class ZoomFragment: DialogFragment(){
    private val args: ZoomFragmentArgs by navArgs()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.criminalintent.fileprovider",
            File(requireContext().applicationContext.filesDir, args.photoName)
        )

        val imageView = ImageView(requireContext())
        imageView.setImageURI(photoUri)

        dialog.addContentView(imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))

        return dialog
    }
}