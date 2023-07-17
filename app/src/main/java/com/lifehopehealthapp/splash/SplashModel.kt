package com.lifehopehealthapp.splash

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class SplashModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {

}