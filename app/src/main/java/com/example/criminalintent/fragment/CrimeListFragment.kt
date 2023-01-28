package com.example.criminalintent.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintent.CrimeListAdapter
import com.example.criminalintent.R
import com.example.criminalintent.databinding.FragmentCrimeListBinding
import com.example.criminalintent.model.Crime
import com.example.criminalintent.viewModel.CrimeListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

private const val TAG="crimeListTag"

class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "null"
        }

    private val viewModel: CrimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newCrimeButton.setOnClickListener { showNewCrime() }

        //слушатель на состояние, в котором слушатель на изменения
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crimes.collect{ crimes ->
                    val adapter = CrimeListAdapter(crimes){crimeId->
                        //navigation
                        findNavController().navigate(
                            CrimeListFragmentDirections.showCrimeDetail(crimeId)
                        )

                    }
                    binding.crimeRecyclerView.adapter = adapter

                    if(crimes.isEmpty()){
                        binding.newCrimeButton.visibility = View.VISIBLE
                        binding.emptyListTextView.visibility = View.VISIBLE
                    }else{
                        binding.newCrimeButton.visibility = View.GONE
                        binding.emptyListTextView.visibility = View.GONE
                    }

                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime->{
                showNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewCrime() {
        //is active on background
        viewLifecycleOwner.lifecycleScope.launch {
            val newCrime = Crime(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false
            )
            viewModel.addCrime(newCrime)
            //delay(1000)
            findNavController().navigate(
                CrimeListFragmentDirections.showCrimeDetail(newCrime.id)
            )
        }
    }
}