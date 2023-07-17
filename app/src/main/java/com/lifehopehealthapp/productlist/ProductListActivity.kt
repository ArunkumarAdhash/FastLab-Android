package com.lifehopehealthapp.productlist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ProductListActivityBinding
import com.lifehopehealthapp.myProfile.MapsActivity
import com.lifehopehealthapp.productcart.ProductCartListAdapter
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants.ImageURL
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.ImageViewActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.productDeliveryAddress
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.avoidDoubleClick
import kotlinx.android.synthetic.main.layout_number_button.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.NumberFormat
import java.util.*


class ProductListActivity : BaseActivity<ProductListViewModel,ProductListModel>(),ProductListAdapter.ProductDetailInfo {
    private lateinit var binding: ProductListActivityBinding
    override fun getViewModel() = ProductListViewModel::class.java
    lateinit var productListAdapter: ProductListAdapter
    private var progressDialog: Dialog? = null
    private var token: String? = ""
    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1
    private var mPrefs: SharedPreferences? = null
   private var imageUrl: String=""
    private var productLimit: Int=0
    private var aDummyList: ArrayList<ProductListResponse.Datum> = ArrayList()
    private var results: ArrayList<ProductListResponse.Datum>? = null
    private var mProductSelectedValue: ArrayList<ProductListResponse.Datum> = ArrayList()
    private var position: Int=0
    lateinit var productCartListAdapter: ProductCartListAdapter
    private var amount: Int=0
    private var totalPrice: Int=0


    override fun getActivityRepository() = ProductListModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductListActivityBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken


        binding.productCartLay.isVisible=false
        binding.productDetailView.isVisible=false
        binding.productListView.isVisible=true

        if(mPrefs!!.productDeliveryAddress!!.isNotEmpty())
        {
            binding.address.text= mPrefs!!.productDeliveryAddress
        }
        else
        {
            binding.address.text= resources.getString(R.string.toast_enter_address)
        }


        binding.rvProductList.setPullRefreshEnabled(false)
        binding.rvProductCartList.setPullRefreshEnabled(false)
        binding.rvProductCartList.setLoadingMoreEnabled(false)

        productListAdapter = ProductListAdapter(
            this, aDummyList, this

        )
        binding.rvProductList.adapter = productListAdapter

        getProductList(1)


        binding.rvProductList.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    getProductList(nextPageNumber!!)
                } else {
                    binding.rvProductList.setNoMore(false)
                    binding.rvProductList.refreshComplete()
                }
            }
        })

        binding.rvProductList.setHasFixedSize(true)

        binding.imageViewCart.setOnClickListener {

            /* for(k in 0 until mProductListSelectedValue.size)
            {
                Log.e("product_list_values::::", Gson().toJson(mProductListSelectedValue[k])+"position:::"+k)
            }*/

        }


        binding.editAddress.setOnClickListener {
            googleMap()
        }


        binding.imageViewBackArrow.setOnClickListener {
            finish()
        }
        binding.ivBackArrow.setOnClickListener {
           onBackPressed()
        }

        binding.imageViewCart.setOnClickListener {


            binding.productCartLay.isVisible=true
            binding.productDetailView.isVisible=false
            binding.productListView.isVisible=false

            setValues()

        }

        binding.ivCart.setOnClickListener {

            binding.productCartLay.isVisible=true
            binding.productDetailView.isVisible=false
            binding.productListView.isVisible=false

            setValues()
        }

        binding.addQuantity.setOnClickListener {
            binding.addQuantity.isVisible=false
            binding.btnQuantityValue.isVisible=true
            binding.btnQuantityValue.number_counter.text= "1"
            binding.addToCart.isEnabled=true
        }


        binding.productDetailImage.setOnClickListener(object : avoidDoubleClick() {
            override fun onSingleClick(v: View) {
                val intent = Intent(this@ProductListActivity, ImageViewActivity::class.java)
                intent.putExtra(
                    ImageURL,imageUrl

                )
                startActivity(intent)
            }
        })


        binding.addToCart.setOnClickListener {
            productListAdapter.update(
                binding.btnQuantityValue.number_counter.text.toString().toInt(),
                position,
                binding.rvProductList,
                true,
                totalPrice
            )



            binding.addToCart.isEnabled=false

            if(productListAdapter.itemCount >0)
            {
                println("values1::" + productListAdapter.itemCount)


                if(mProductSelectedValue.size>0)
                {
                    mProductSelectedValue.clear()
                }


                for (i in 0 until productListAdapter.getArrayList().size) {
                    println("values2::" + Gson().toJson(productListAdapter.getArrayList()[i].isSelected))

                    if(productListAdapter.getArrayList()[i].isSelected) {
                        mProductSelectedValue.add(productListAdapter.getArrayList()[i])
                    }


                }

                binding.productBadgeCount.text=mProductSelectedValue.size.toString()
                binding.productDetailBadgeCount.text=mProductSelectedValue.size.toString()

            }


        }

        binding.btnQuantityValue.number_counter.addTextChangedListener(object : TextWatcher {

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {

                val strValue: String = s.toString()


                 if(strValue.toInt()>productLimit) {
                    binding.btnQuantityValue.number_counter.text= (strValue.toInt()-1).toString()
                    Utils.showToast(this@ProductListActivity,"Product maximum quantity reached", true)
                }
                else
                {
                    binding.addToCart.isEnabled=true
                }


                if(strValue.toInt()==0)
                {
                    binding.btnQuantityValue.isVisible=false
                    binding.addQuantity.isVisible=true
                    binding.addToCart.isEnabled=false
                    productListAdapter.update(
                        binding.btnQuantityValue.number_counter.text.toString().toInt(),
                        position,
                        binding.rvProductList,
                        false,0
                    )



                    if(productListAdapter.itemCount >0)
                    {
                        println("values1::" + productListAdapter.itemCount)


                        if(mProductSelectedValue.size>0)
                        {
                            mProductSelectedValue.clear()
                        }


                        for (i in 0 until productListAdapter.getArrayList().size) {
                            println("values2::" + Gson().toJson(productListAdapter.getArrayList()[i].isSelected))

                            if(productListAdapter.getArrayList()[i].isSelected) {
                                mProductSelectedValue.add(productListAdapter.getArrayList()[i])
                            }


                        }

                        binding.productBadgeCount.text=mProductSelectedValue.size.toString()
                        binding.productDetailBadgeCount.text=mProductSelectedValue.size.toString()

                    }

                }


                if(strValue.toInt() in 1..productLimit) {

                    totalPrice=strValue.toInt() * amount

                    binding.tvDetailPrice.text = NumberFormat.getCurrencyInstance(
                        Locale("en", "US")
                    ).format(strValue.toInt() * amount)
                }
                else
                {

                    if(strValue.toInt()==0 || strValue.toInt()==1)
                    {
                        binding.tvDetailPrice.text = NumberFormat.getCurrencyInstance(
                            Locale("en", "US")
                        ).format(amount)
                    }

                }



            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })


    }

    private fun setValues() {

        productCartListAdapter = ProductCartListAdapter(
            this,productListAdapter.getArrayList())
        binding.rvProductCartList.adapter = productCartListAdapter
    }


    private fun googleMap() {
        val str = binding.address.text
        if (str!!.contains("LandMark")) {
            val separated: List<String> = str.split(":")
            Log.e("LandMark", separated[1])
            intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("LandMark", separated[1])
            startActivity(intent)
        } else {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        binding.address.text=event

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getProductList(currentPage: Int) {

        if (Utils.isNetworkAvailable(this)) {
            if (currentPage == 1) {
                if (progressDialog == null) {
                    progressDialog = Utils.getDialog(this)
                }
                progressDialog!!.show()
            }
            val datum = MyOrderListAPIRequest()
            datum.pageNumber = currentPage
            datum.pageSize = 10

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(datum)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getProductList(token!!, aJsonObject)

            viewModel.Response!!.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }

                        if (it.value.getStatusCode() == 200) {

                            results =
                                it.value.getValue()!!.data as ArrayList<ProductListResponse.Datum>

                            if (it.value.getValue()!!.data!!.size != 0) {
                                binding.textviewNoData.isVisible = false
                                binding.rvProductList.isVisible = true

                                //newly added
                                TotalPageNumber = it.value.getValue()!!.totalPages
                                if (nextPageNumber!! <= TotalPageNumber!!) {
                                    if (it.value.getValue()!!.nextPage != null) {
                                        if (nextPageNumber == TotalPageNumber) {
                                            nextPageNumber = 0
                                        } else {
                                            nextPageNumber = nextPageNumber!! + 1
                                        }
                                    } else {
                                        nextPageNumber = 0
                                    }
                                }
                            } else {
                                if (currentPage == 1) {
                                    binding.textviewNoData.isVisible = true
                                    binding.rvProductList.isVisible = false
                                }
                            }
                            if (results!!.size > 0) {
                                productListAdapter.addAllSearch(results!!)
                                productListAdapter.notifyDataSetChanged()
                            }

                            if (it.value.getValue()!!.nextPage == null) {
                                binding.rvProductList.setNoMore(true)
                            } else {
                                binding.rvProductList.setNoMore(false)
                            }

                            binding.rvProductList.refreshComplete()

                        }


                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.code == 401) {
                            val data = RefreshAPIRequest()
                            val removeChar = mPrefs!!.authToken!!.replace("Bearer ", "")
                            data.token = removeChar
                            data.refreshToken = mPrefs!!.refreshToken

                            val gson = Gson()
                            var json: String? = ""
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())

                            val aJsonParser = JsonParser()
                            val aJsonObject = aJsonParser.parse(json) as JsonObject

                            val refresh = ErrorHandling(this)
                            refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                        }
                    }
                    else -> {

                    }

                }
            }

        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }


    }
    override fun productDetail(value: ProductListResponse.Datum, position: Int) {
        binding.productListView.isVisible = false
        binding.productDetailView.isVisible = true

        imageUrl= value.imagePath.toString()
        productLimit=value.limit!!
        this.position=position

        Glide.with(this).load(value.imagePath)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(binding.productDetailImage)

        binding.tvDetailTitle.text = value.title
        binding.tvProductDescription.text = value.description
        binding.tvDetailPrice.text = NumberFormat.getCurrencyInstance(
            Locale("en", "US")
        ).format(value.amount)

        amount=value.amount!!

        binding.btnQuantityValue.number_counter.text=value.totalQuantity.toString()

      if(value.totalQuantity==0)
        {
            binding.addQuantity.isVisible=true
            binding.btnQuantityValue.isVisible=false
            binding.addToCart.isEnabled=false

        }
        else
        {
            binding.addQuantity.isVisible=false
            binding.btnQuantityValue.isVisible=true
            binding.addToCart.isEnabled=false
        }
    }

    override fun onBackPressed() {
        if (binding.productDetailView.isVisible) {
            binding.productListView.isVisible = true
            binding.productDetailView.isVisible = false
        }
        else
        {
           finish()
        }
    }


}