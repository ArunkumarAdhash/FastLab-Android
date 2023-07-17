package com.lifehopehealthapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifehopehealthapp.retrofitService.RemoteDataSource

abstract class BaseActivity<VM : ViewModel, R : BaseRepository> :
    AppCompatActivity() {

    //TODO The lateinit keyword stands for late initialization. when a non-null initializer cannot be supplied in the constructor.
    protected lateinit var viewModel: VM
    //protected lateinit var userPreferences: UserPreferences
    protected val remoteDataSource = RemoteDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        //userPreferences = UserPreferences()
        //lifecycleScope.launch { userPreferences.authToken.first() }
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getActivityRepository(): R

}