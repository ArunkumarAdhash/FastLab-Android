package com.lifehopehealthapp.symptomSearch.Concerns

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ConcernsModel (val api: APIManager, val preferences: PreferenceHelper) : BaseRepository() {

}