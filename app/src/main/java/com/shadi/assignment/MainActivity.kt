package com.shadi.assignment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        matchAdapter = MatchAdapter(
            onAccept = { uuid -> viewModel.acceptMatch(uuid) },
            onDecline = { uuid -> viewModel.declineMatch(uuid) }
        )
        recyclerView.adapter = matchAdapter

        lifecycleScope.launch {
            viewModel.matches.collectLatest { pagingData: PagingData<UserProfile> ->
                matchAdapter.submitData(pagingData)
            }
        }
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        lifecycleScope.launch {
            matchAdapter.loadStateFlow.collectLatest { loadStates ->
                when {
                    loadStates.refresh is LoadState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    loadStates.refresh is LoadState.Error -> {
                        progressBar.visibility = View.GONE
                        val errorMsg =
                            (loadStates.refresh as LoadState.Error).error.localizedMessage
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.error, errorMsg),
                            Toast.LENGTH_LONG
                        )
                            .show()
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
                        )
                            .show()
                    }

                    loadStates.refresh is LoadState.NotLoading -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        // Offline indicator
        val networkStatusText = findViewById<TextView>(R.id.networkStatusText)
        if (NetworkUtils.isNetworkAvailable(this)) {
            networkStatusText.text = getString(R.string.online)
            networkStatusText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            networkStatusText.text = getString(R.string.offline)
            networkStatusText.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }
}
