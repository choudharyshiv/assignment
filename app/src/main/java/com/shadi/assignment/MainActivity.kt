package com.shadi.assignment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.shadi.assignment.data.local.AppDatabase
import com.shadi.assignment.data.remote.MatchApiService
import com.shadi.assignment.data.repository.MatchRepository
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.domain.usecase.AcceptMatchUseCase
import com.shadi.assignment.domain.usecase.DeclineMatchUseCase
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import com.shadi.assignment.presentation.ui.MatchAdapter
import com.shadi.assignment.presentation.viewmodel.MatchViewModel
import com.shadi.assignment.presentation.viewmodel.MatchViewModelFactory
import com.shadi.assignment.ui.theme.AssignmentTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var viewModel: MatchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Instantiate Room database and DAO
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "matchmate-db"
        ).build()
        val userProfileDao = db.userProfileDao()

        // Instantiate Retrofit and API service
        val retrofit = Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(MatchApiService::class.java)

        // Instantiate repository and use cases
        val repository = MatchRepository(apiService, userProfileDao)
        val getMatchesUseCase = GetMatchesUseCase(repository)
        val acceptMatchUseCase = AcceptMatchUseCase(repository)
        val declineMatchUseCase = DeclineMatchUseCase(repository)

        // Create ViewModel factory and get ViewModel
        val factory =
            MatchViewModelFactory(getMatchesUseCase, acceptMatchUseCase, declineMatchUseCase)
        viewModel = ViewModelProvider(this, factory)[MatchViewModel::class.java]

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
                        Toast.makeText(this@MainActivity, "Error: $errorMsg", Toast.LENGTH_LONG)
                            .show()
                    }

                    loadStates.append is LoadState.Error -> {
                        progressBar.visibility = View.GONE
                        val errorMsg = (loadStates.append as LoadState.Error).error.localizedMessage
                        Snackbar.make(
                            recyclerView,
                            "Error loading more: $errorMsg",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    loadStates.refresh is LoadState.NotLoading && matchAdapter.itemCount == 0 -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "No matches found.", Toast.LENGTH_LONG)
                            .show()
                    }

                    loadStates.refresh is LoadState.NotLoading -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        // Offline indicator
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Offline mode: showing cached data.", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssignmentTheme {
        Greeting("Android")
    }
}