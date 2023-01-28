package com.example.criminalintent.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.sql.Time
import java.util.*
import kotlin.time.Duration.Companion.hours

class TimePickerFragment: DialogFragment() {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        calendar.time = args.crimeDate

        val timeListener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            val date = calendar.time
            date.hours = h
            date.minutes = m

            setFragmentResult(REQUEST_KEY_TIME, bundleOf(BUNDLE_KEY_TIME to date))
        }

        return TimePickerDialog(
            requireContext(),
            timeListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    companion object{
        const val REQUEST_KEY_TIME = "REQUEST_KEY_TIME"
        const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
    }
}