package com.lifehopehealthapp.labResult

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.retrofitService.APIManager
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class LabResultsListDataSource(
    val token: String,
    val categoryID: String,
    val invoke: APIManager
) :
    PagingSource<Int, LabResultPDFResponse.Datum>() {

    private var nextPageNumber: Int? = 1

    override fun getRefreshKey(state: PagingState<Int, LabResultPDFResponse.Datum>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LabResultPDFResponse.Datum> {
        return try {
            val data = LabResultAPIRequest()
            data.setCategoryId(categoryID)
            val datam = LabResultAPIRequest.PaginationQuery()
            datam.pageNumber = nextPageNumber
            datam.pageSize = 10
            data.setPaginationQuery(datam)

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("ModelPaging---->", json.toString())

            var response: LabResultPDFResponse? = null

            if (nextPageNumber!! > 0) {
                response = invoke.getLabResult(token, data)
                Log.e("PagingResponse", response.getValue()!!.data!!.size.toString())
            }

            if (!response!!.getValue()!!.nextPage!!.equals("")) {
                //nextPageNumber = response.getValue()!!.nextPage!!.toInt()
                nextPageNumber = nextPageNumber!! + 1
            } else {
                nextPageNumber = 0
            }
            if (response.getValue()!!.data!!.isEmpty()) {
                if (nextPageNumber == 1 || nextPageNumber==0) {
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
