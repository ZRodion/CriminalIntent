package com.example.criminalintent.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat.format
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.R
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.getScaledBitmap
import com.example.criminalintent.model.Crime
import com.example.criminalintent.viewModel.CrimeDetailViewModel
import com.example.criminalintent.viewModel.CrimeDetailViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.String.format
import java.sql.Time
import java.text.DateFormat
import java.util.*

private const val CRIME_FRAGMENT_TAG = "crime fragment"

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding     //переменная ссылка-проверялка
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    //accessing navigation arguments
    //a xml arg = a class property
    private val args: CrimeDetailFragmentArgs by navArgs()

    private val viewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { parseContactSelection(it) }
    }

    private var photoName: String? = null
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            viewModel.updateCrime { oldCrime ->
                oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.crimeTitle.text.isEmpty()) {
                binding.crimeTitle.error = "title must not be empty"
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
            crimeSolved.setOnCheckedChangeListener { _, isSolved ->
                viewModel.updateCrime {
                    it.copy(isSolved = isSolved)
                }
            }
            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            val intent = selectSuspect.contract.createIntent(requireContext(), null)
            crimeSuspect.isEnabled = canResolveIntent(intent)

            crimeCamera.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.criminalintent.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            //only on foreground
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //срабатывает при каждом изменении
                viewModel.crime.collect { crime ->
                    crime?.let { updateUI(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            viewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newTime = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            viewModel.updateCrime { it.copy(date = newTime) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                deleteCrime()
                true
            }
            R.id.save_crime ->{
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeSolved.isChecked = crime.isSolved

            crimeDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(crime.date)
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    //вот почему нужно заносить это в collect
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }

            crimeTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(crime.date)
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }

            crimeReport.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                }
                startActivity(Intent.createChooser(intent, getString(R.string.send_report)))
            }
            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

            if(crime.photoFileName==null) crimePhoto.isEnabled = false
            crimePhoto.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.zoomPhoto(crime.photoFileName!!)
                )
            }
        }
        updatePhoto(crime.photoFileName)
    }

    private fun deleteCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteCrime()
            //findNavController().navigate( R.id.crimeListFragment )
            findNavController().popBackStack()
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = crime.date.toString()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }

    private fun parseContactSelection(contactUri: Uri) {

        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver
            .query(
                contactUri, queryFields, null, null, null
            )

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        //intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager: PackageManager? = requireActivity().packageManager
        packageManager?.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return packageManager != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.crimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.crimePhoto.setImageBitmap(scaledBitmap)
                    binding.crimePhoto.tag = photoFileName
                }
            } else {
                binding.crimePhoto.setImageBitmap(null)
                binding.crimePhoto.tag = null
            }
        }
    }


    private fun createLog(msg: String) {
        Log.d(CRIME_FRAGMENT_TAG, msg)
    }
}