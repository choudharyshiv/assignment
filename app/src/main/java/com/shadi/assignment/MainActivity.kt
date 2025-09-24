package com.shadi.assignment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.presentation.ui.MatchAdapter
import com.shadi.assignment.presentation.viewmodel.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var matchAdapter: MatchAdapter
    private val viewModel: MatchViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        observeMatches()
        observeLoadState()
        setupNetworkCallback()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        matchAdapter = MatchAdapter(
            onAccept = { uuid -> viewModel.acceptMatch(uuid) },
            onDecline = { uuid -> viewModel.declineMatch(uuid) }
        )
        recyclerView.adapter = matchAdapter
    }

    private fun observeMatches() {
        lifecycleScope.launch {
            viewModel.matches.collectLatest { pagingData: PagingData<UserProfile> ->
                matchAdapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        lifecycleScope.launch {
            matchAdapter.loadStateFlow.collectLatest { loadStates ->
                when {
                    loadStates.refresh is LoadState.Loading -> progressBar.visibility = View.VISIBLE
                    loadStates.refresh is LoadState.Error -> {
                        progressBar.visibility = View.GONE
                        val errorMsg =
                            (loadStates.refresh as LoadState.Error).error.localizedMessage
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.error, errorMsg),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    loadStates.append is LoadState.Error -> {
                        progressBar.visibility = View.GONE
                        val errorMsg = (loadStates.append as LoadState.Error).error.localizedMessage
                        Snackbar.make(
                            recyclerView,
                            getString(R.string.error_loading_more, errorMsg),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    loadStates.refresh is LoadState.NotLoading && matchAdapter.itemCount == 0 -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.no_matches_found),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    loadStates.refresh is LoadState.NotLoading -> progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupNetworkCallback() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread { updateNetworkStatus(true) }
            }

            override fun onLost(network: Network) {
                runOnUiThread { updateNetworkStatus(false) }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        // Set initial status
        updateNetworkStatus(NetworkUtils.isNetworkAvailable(this))
    }

    private fun updateNetworkStatus(isOnline: Boolean) {
        val networkStatusText = findViewById<TextView>(R.id.networkStatusText)
        if (isOnline) {
            networkStatusText.text = getString(R.string.online)
            networkStatusText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            networkStatusText.text = getString(R.string.offline)
            networkStatusText.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::connectivityManager.isInitialized && ::networkCallback.isInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
