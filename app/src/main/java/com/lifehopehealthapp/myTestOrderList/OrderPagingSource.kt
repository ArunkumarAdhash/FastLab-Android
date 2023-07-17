package com.lifehopehealthapp.myTestOrderList

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.ResponseModel.MyOrderListAPIRequest
import com.lifehopehealthapp.ResponseModel.OrderListData
import com.lifehopehealthapp.retrofitService.APIManager

class OrderPagingSource(val remote: APIManager, val token: String) :
    PagingSource<Int, OrderListData>() {

    override fun getRefreshKey(state: PagingState<Int, OrderListData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderListData> {
        return try {
            var prevPageNumber: Int? = null
            var nextPageNumber: Int? = null
            val nextPageCount: Int = params.key ?: FIRST_PAGE_INDEX
            val data = MyOrderListAPIRequest()
            data.pageNumber = nextPageCount
            data.pageSize = 10

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            val response = remote.getOrderTestList(token, aJsonObject)

            if (!response.getValue()!!.previousPage.equals("")) {
                val uri = Uri.parse(response.getValue()!!.previousPage!!)
                Log.e("previousPage", response.getValue()!!.previousPage.toString())
                val prevPageQuery = uri.getQueryParameter("pageNumber")

                prevPageNumber = prevPageQuery?.toInt()
            }

            if (!response.getValue()!!.nextPage.equals("")) {
                val mNextPage: Uri = Uri.parse(response.getValue()!!.nextPage)
                Log.e("nextPage", response.getValue()!!.nextPage.toString())
                val nextPageQuery = mNextPage.getQueryParameter("pageNumber")
                nextPageNumber = nextPageQuery!!.toInt()
                Log.e("nextPageNumber", nextPageNumber.toString())
            }
            LoadResult.Page(
                data = response.getValue()!!.data!!,
                prevKey = prevPageNumber,
                nextKey = nextPageNumber
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}