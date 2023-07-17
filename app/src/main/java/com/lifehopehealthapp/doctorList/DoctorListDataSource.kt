package com.lifehopehealthapp.doctorList

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.lifehopehealthapp.ResponseModel.DoctorListRequest
import com.lifehopehealthapp.ResponseModel.DoctorListResponse
import com.lifehopehealthapp.ResponseModel.NoDataFoundModel
import com.lifehopehealthapp.ResponseModel.Pagination
import org.greenrobot.eventbus.EventBus

class DoctorListDataSource(
    val token: String,
    val searchData: String,
    val invoke: DoctorListModel
) :
    PagingSource<Int, DoctorListResponse.Datum>() {

    private var mSearchData: String? = ""
    private var nextPageNumber: Int? = 1

    init {
        mSearchData = searchData
    }

    override fun getRefreshKey(state: PagingState<Int, DoctorListResponse.Datum>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DoctorListResponse.Datum> {
        return try {
            val data = DoctorListRequest()
            data.setText(searchData)
            val paging = Pagination()
            paging.pageSize = 10
            paging.pageNumber = nextPageNumber
            data.setPaginationQuery(paging)

            val gson = Gson()
            val json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            var response: DoctorListResponse? = null
            if (nextPageNumber!! > 0) {
                response = invoke.api.getDoctorList(token, data)
                Log.e("PagingResponse", response.getValue()!!.data.toString())
            }

            if (!response!!.getValue()!!.nextPage.equals("")) {
                nextPageNumber = response.getValue()!!.pageNumber!! + 1
                Log.e("nextPageNumber", nextPageNumber.toString())
            } else {
                nextPageNumber = 0
            }
            if (response.getValue()!!.data!!.size == 0) {
                if (nextPageNumber == 1) {
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
