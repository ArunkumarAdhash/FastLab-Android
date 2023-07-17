package com.lifehopehealthapp.covidstatus

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.ResponseModel.*
import org.greenrobot.eventbus.EventBus

class CovidStatusListDataSource(
    val token: String,
    val invoke: CovidStatusModel
) :
    PagingSource<Int, CovidStatusResponse.Data>() {

    private var nextPageNumber: Int? = 1
    private var totalPageNumber: Int? = 0

    override fun getRefreshKey(state: PagingState<Int, CovidStatusResponse.Data>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CovidStatusResponse.Data> {
        return try {
            val data = CovidStatusAPIRequest()
            data.setPageSize(10)
            data.setPageNumber(nextPageNumber)


            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject


            var response: CovidStatusResponse? = null
            if (nextPageNumber!! > totalPageNumber!!) {
                //response = invoke.api.getCovidStatusDetails(token, aJsonObject)
                Log.e("PagingResponse", response!!.value!!.data.toString())
            }

            if (!response!!.value!!.nextPage.equals("")) {
                totalPageNumber = response.value!!.totalPages
                nextPageNumber = nextPageNumber!! + 1
            } else {
                nextPageNumber = 0
            }

            if (response!!.value!!.data!!.isEmpty()) {
                if (totalPageNumber == 0) {
                    val data = NoDataFoundModel()
                    data.arraySize = 0
                    EventBus.getDefault().post(data)
                }
            } else {
                val data = NoDataFoundModel()
                data.arraySize = response.value!!.data!!.size
                EventBus.getDefault().post(data)
            }



            LoadResult.Page(
                data = response.value!!.data!!,
                prevKey = null,
                nextKey = nextPageNumber!! + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
