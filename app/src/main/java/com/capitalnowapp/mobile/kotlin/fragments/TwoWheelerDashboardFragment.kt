package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.FragmentTwoWheelerDashboardBinding
import com.capitalnowapp.mobile.kotlin.activities.ChooseDealerActivity
import com.capitalnowapp.mobile.kotlin.activities.HaveDealerActivity
import com.capitalnowapp.mobile.kotlin.adapters.ImageSliderAdapter
import com.capitalnowapp.mobile.models.Image
import com.capitalnowapp.mobile.models.TwlBannerImagesReq
import com.capitalnowapp.mobile.models.TwlBannerImagesResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations


class TwoWheelerDashboardFragment : Fragment() {
    private var binding: FragmentTwoWheelerDashboardBinding? = null

    @SuppressLint("NotConstructor")
    fun TwoWheelerDashboardFragment() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTwoWheelerDashboardBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.ivHaveDealer.setOnClickListener {
            val intent = Intent(context, HaveDealerActivity::class.java)
            startActivity(intent)
        }

        binding!!.ivChooseDealer.setOnClickListener {
            val intent = Intent(context, ChooseDealerActivity::class.java)
            startActivity(intent)
        }

        twlBannerImages()
    }


    private fun twlBannerImages() {
        try {
            val genericAPIService = GenericAPIService(context)
            val twlBannerImagesReq = TwlBannerImagesReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.twlBannerImages(twlBannerImagesReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val twlBannerImagesResponse = Gson().fromJson(
                    responseBody, TwlBannerImagesResponse::class.java
                )
                if (twlBannerImagesResponse != null && twlBannerImagesResponse.status == Constants.STATUS_SUCCESS) {
                    if (twlBannerImagesResponse.images?.isNotEmpty()!!) {
                        setSliderImages(twlBannerImagesResponse.images)
                    }
                } else {

                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSliderImages(images: List<Image>?) {
        try {
            val imageSliderAdapter = ImageSliderAdapter()
            imageSliderAdapter.renewItems(images as MutableList<Image>)
            binding!!.imageSlider.setSliderAdapter(imageSliderAdapter)

            binding!!.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            binding!!.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            binding!!.imageSlider.startAutoCycle()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}