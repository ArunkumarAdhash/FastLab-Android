package com.lifehopehealthapp.vaccineReport

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.retrofitService.APIManager
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class VaccineListDataSource(
    val token: String,
    val invoke: APIManager
) :
    PagingSource<Int, VaccinationListDatum>() {

    private var nextPageNumber: Int? = 1
    private var totalPageNumber: Int? = 0

    override fun getRefreshKey(state: PagingState<Int, VaccinationListDatum>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VaccinationListDatum> {
        return try {
            val data = VaccinationListRequest()
            data.pageSize = 10
            data.pageNumber = nextPageNumber

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("ModelPaging---->", json.toString())

            var response: VaccinationListResponse? = null

            if (nextPageNumber!! > 0) {
                response = invoke.getVaccinationReportList(token, data)
                totalPageNumber = response.getValue()!!.getTotalPages()
                Log.e("PagingResponse", response.getValue()!!.data!!.size.toString())
            }

            if ((!response!!.getValue()!!.nextPage.equals("")) && response!!.getValue()!!.nextPage != null) {
                //nextPageNumber = response.getValue()!!.nextPage!!.toInt()
                nextPageNumber = nextPageNumber!! + 1
            } else {
                nextPageNumber = 0
            }
            if (response.getValue()!!.data!!.isEmpty()) {
                if (totalPageNumber == 0) {
                    val data = NoDataFoundModel()
                    data.arraySize = 0
                    EventBus.getDefault().post(data)
                }
            } else {
                val data = NoDataFoundModel()
                data.arraySize = response.getValue()!!.data!!.size
                EventBus.getDefault().post(data)
            }

            LoadResult.Page(
                data = response.getValue()!!.data!!,
                prevKey = null,
                nextKey = nextPageNumber!! + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
