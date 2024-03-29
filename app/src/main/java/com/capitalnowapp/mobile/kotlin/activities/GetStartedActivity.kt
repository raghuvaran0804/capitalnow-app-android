package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ActivityGetStartedBinding
import com.capitalnowapp.mobile.kotlin.adapters.GetStartedAdapter
import com.eftimoff.viewpagertransformers.BaseTransformer
import java.lang.reflect.Field


class GetStartedActivity : BaseActivity() {
    private var pos: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGetStartedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initView(binding)
    }

    private fun initView(binding: ActivityGetStartedBinding) {
        try {

            binding.tvNext.setOnClickListener {
                launchActivity()
            }

            val handler = Handler()
            binding.viewPager.rootView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.viewPager.adapter = GetStartedAdapter()
            binding.pageIndicatorView.count = 2


            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    handler.removeMessages(0)

                    val runnable =
                        Runnable { binding.viewPager.currentItem = ++binding.viewPager.currentItem }
                    if (position < (binding.viewPager.adapter?.itemCount ?: 0)) {
                        handler.postDelayed(runnable, 2500)
                    }
                }
            })

            //binding.viewPager.setPageTransformer(CubeInTransformer())
            //binding.pageIndicatorView.setViewPager(binding.viewPager)
            //  reduceDragSensitivity(binding.viewPager)
            binding.btnNext1.setOnClickListener {
                val pos = binding.viewPager.currentItem
                if (pos < 1)
                    binding.viewPager.setCurrentItem(pos + 1, true)
                else {
                    launchActivity()
                }
            }

            binding.btnNext2.setOnClickListener {
                launchActivity()
            }


            binding.btnSkip.setOnClickListener {
                launchActivity()
            }

            binding.btnPrv.setOnClickListener {
                pos = binding.viewPager.currentItem
                binding.viewPager.setCurrentItem(pos!! - 1, true)
            }

            /*   binding.ivDottedOne.setImageResource(R.drawable.ic_intro_dot_1_selected)
               binding.ivDottedTwo.setImageResource(R.drawable.ic_intro_dot_2_unselected)
               binding.ivDottedThree.setImageResource(R.drawable.ic_intro_dot_3_unselected)*/
            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.pageIndicatorView.setSelected(position)
                    //binding.btnSkip.text = "SKIP"
                    when (position) {
                        0 -> {
                            /* binding.ivDottedOne.setImageResource(R.drawable.ic_intro_dot_1_selected)
                             binding.ivDottedTwo.setImageResource(R.drawable.ic_intro_dot_2_unselected)
                             binding.ivDottedThree.setImageResource(R.drawable.ic_intro_dot_3_unselected)
                             binding.ivDottedOne.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.color_primary))
                             binding.ivDottedTwo.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                             binding.ivDottedThree.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                             binding.ivDottedOne.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_selected)
                             binding.ivDottedTwo.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)
                             binding.ivDottedThree.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)

                             binding.frame.background = ContextCompat.getDrawable(activityContext, R.drawable.intro_bg)
                            */ binding.btnSkip.visibility = GONE
                            binding.btnPrv.visibility = GONE
                            binding.pageIndicatorView.setSelected(0)
                            //  binding.btnNext.visibility = VISIBLE
                            binding.btnNext1.visibility = VISIBLE
                            binding.btnNext2.visibility = GONE
                            binding.btnNext1.text = "NEXT"
                        }

                        1 -> {
                            /*binding.ivDottedOne.setImageResource(R.drawable.ic_intro_dot_1_unselected)
                            binding.ivDottedTwo.setImageResource(R.drawable.ic_intro_dot_2_selected)
                            binding.ivDottedThree.setImageResource(R.drawable.ic_intro_dot_3_unselected)
                            binding.ivDottedOne.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                            binding.ivDottedTwo.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.color_primary))
                            binding.ivDottedThree.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                            binding.ivDottedOne.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)
                            binding.ivDottedTwo.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_selected)
                            binding.ivDottedThree.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)

                            binding.frame.background = ContextCompat.getDrawable(activityContext, R.drawable.intro_bg)
                            */binding.btnSkip.visibility = GONE
                            //    binding.btnPrv.visibility = VISIBLE
                            //     binding.btnNext.visibility = VISIBLE
                            binding.btnNext2.visibility = VISIBLE
                            binding.btnNext1.visibility = GONE
                            binding.btnNext1.text = "FINISH"
                        }
                        //2 -> {
                        /*binding.ivDottedOne.setImageResource(R.drawable.ic_intro_dot_1_unselected)
                        binding.ivDottedTwo.setImageResource(R.drawable.ic_intro_dot_2_unselected)
                        binding.ivDottedThree.setImageResource(R.drawable.ic_intro_dot_3_selected)
                        binding.ivDottedOne.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                        binding.ivDottedTwo.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.colorIntroUnSelectedDot))
                        binding.ivDottedThree.setBackgroundColor(ContextCompat.getColor(this@GetStartedActivity, R.color.color_primary))
                        binding.ivDottedOne.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)
                        binding.ivDottedTwo.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_unselected)
                        binding.ivDottedThree.background = ContextCompat.getDrawable(this@GetStartedActivity, R.drawable.ic_intro_dot_selected)

                        binding.frame.background = ContextCompat.getDrawable(activityContext, R.drawable.intro_bg)*/

                        /*binding.btnNext.visibility = GONE
                   //     binding.btnNext1.visibility = VISIBLE
                        binding.btnSkip.visibility = VISIBLE
                        binding.btnSkip.text = "FINISH"
                    }*/
                    }
                }
            })
            Handler().postDelayed(Runnable {
                sharedPreferences.putBoolean(
                    Constants.SP_IS_GET_STARTED_SHOWN,
                    true
                )
            }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchActivity() {
        // sharedPreferences.putBoolean(Constants.SP_IS_GET_STARTED_SHOWN, true)
        launchDesiredActivity()
    }

    class CubeInTransformer : BaseTransformer(), ViewPager2.PageTransformer {
        override fun onTransform(view: View, position: Float) {
            // Rotate the fragment on the left or right edge
            view.pivotX = (if (position > 0) 0 else view.width).toFloat()
            view.pivotY = 0F
            view.rotationY = -90f * position
        }

        public override fun isPagingEnabled(): Boolean {
            return true
        }
    }

    private fun reduceDragSensitivity(viewPager: ViewPager2) {
        try {
            val ff: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            ff.isAccessible = true
            val recyclerView = ff.get(viewPager) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 4)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}