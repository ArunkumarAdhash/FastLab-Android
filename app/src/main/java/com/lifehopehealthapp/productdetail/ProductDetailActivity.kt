package com.lifehopehealthapp.productdetail

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.databinding.ProductDetailActivityBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.Utils


class ProductDetailActivity : BaseActivity<ProductDetailViewModel,ProductDetailModel>()
{
    private lateinit var binding: ProductDetailActivityBinding
    override fun getViewModel() = ProductDetailViewModel::class.java


    override fun getActivityRepository() = ProductDetailModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductDetailActivityBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)


        binding.tvProductDescription.movementMethod = ScrollingMovementMethod()


        binding.imageViewBackArrow.setOnClickListener {
            finish()
        }





    }




}