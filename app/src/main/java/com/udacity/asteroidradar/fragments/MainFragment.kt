package com.udacity.asteroidradar.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapters.AsteroidsListAdapter
import com.udacity.asteroidradar.adapters.AsteroidsListClickItemListener
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.viewmodels.MainViewModel
import kotlinx.coroutines.*
import org.json.JSONObject

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: AsteroidsListAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application)).get(MainViewModel::class.java)
    }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        setupRecyclerViewList()
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.asteroids.observe(viewLifecycleOwner, Observer<Array<Asteroid>> { asteroids ->
            asteroids?.apply {
                adapter.submitList(asteroids.toList())
//                asteroids.forEach {
//                    Toast.makeText(requireContext(), it.codename, Toast.LENGTH_LONG).show()
//                }
            }
        })
        viewModel.clickedAsteroid.observe(
            viewLifecycleOwner,
            Observer<Asteroid> { asteroidClicked ->
                asteroidClicked?.let {
                    this.findNavController()
                        .navigate(MainFragmentDirections.actionShowDetail(asteroidClicked))
                    viewModel.onAsteroidClickEventComplete()
                }
            })
    }

    private fun setupRecyclerViewList(){
        adapter = AsteroidsListAdapter(AsteroidsListClickItemListener { asteroid ->
            viewModel.onAsteroidClickEvent(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter
    }

    private fun testGetTodayPictureMethod() {
        CoroutineScope(Job() + Dispatchers.Main).launch {
            val pictureOfDay = Network.NASANeoAPI.getTodayPicture()
            Toast.makeText(requireContext(), pictureOfDay.url, Toast.LENGTH_LONG).show()
        }
    }

    private fun testFetchAsteroidsListMethod() {
        CoroutineScope(Job() + Dispatchers.Main).launch {
            val startDate: String = getToday()
            val endDate: String = getSeventhDay()
            val apiResponse = Network.NASANeoAPI.fetchAsteroids(startDate, endDate)
            val apiResponseJson = JSONObject(apiResponse)
            val listOfAsteroids = parseAsteroidsJsonResult(apiResponseJson)
            listOfAsteroids.forEach {
                Toast.makeText(requireContext(), it.codename, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_week_asteroids -> viewModel.changeQuery("week")
            R.id.view_today_asteroids -> viewModel.changeQuery("today")
            R.id.view_all_asteroids -> viewModel.changeQuery(null)
        }
        return true
    }
}
