package com.example.animationplayer

import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animationplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mContext: Context
    private lateinit var mAnimations: MutableList<Animation>
    private val mImages = arrayOf(R.drawable.jessica_square, R.drawable.tiffany_square,
            R.drawable.taeyeon_square, R.drawable.yoona_square,
            R.drawable.yuri_square, R.drawable.soo_square,
            R.drawable.seo_square, R.drawable.kim_square,
            R.drawable.sunny_square)

    private var mFrames = R.drawable.anim_images

    private val mTexts = arrayOf("平移", "缩放", "旋转", "透明", "混合",
            "自定", "帧动", "Wrapper", "差值")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = applicationContext
        initAnimations(mContext)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = GridAdapter(mContext, mAnimations, mFrames, mTexts, mImages)

    }

    private fun initAnimations(context: Context) {
        mAnimations = ArrayList()
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_translate));  // 平移动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_scale));  // 缩放动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_rotate));  // 旋转动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_alpha));  // 透明动画
        mAnimations.add(AnimationUtils.loadAnimation(context, R.anim.anim_all));  // 动画合集

        val anim = Rotate3dAnimation(0.0f, 720.0f, 100.0f, 100.0f, 0.0f, false)
        anim.duration = 2000
        mAnimations.add(anim) // 自定义动画


    }

    inner class GridAdapter(
            context: Context, animations: MutableList<Animation>,
            frame: Int, texts: Array<String>, images: Array<Int>
    ) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

        private val mAnimations = animations
        private val mFrame: Int = frame
        private val mTexts = texts
        private val mImages = images
        var mLastPosition = -1
        private val mContext = context

        // 列表适配器的ViewHolder
        inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mImageView: ImageView = view.findViewById(R.id.item_iv_img)
            var mButton: Button = view.findViewById(R.id.item_b_start)
            val mContainer: View = view
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anim, parent, false)
            return GridViewHolder(view)
        }

        override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
            setAnimation(holder.mContainer, position)
            holder.mButton.text = mTexts[position]
            holder.mImageView.setImageResource(mImages[position]);
            when (position) {
                6 -> {
                    //使用帧动画
                    holder.mImageView.setImageResource(mFrame)
                    holder.mButton.setOnClickListener {
                        (holder.mImageView.drawable as AnimationDrawable).start()
                    }
                }
                7 -> {
                    //使用Wrapper属性动画
                    performWrapperAnimation(holder.mImageView, 0, Utils.dp2px(mContext, 120))
                    holder.mButton.setOnClickListener {
                        performWrapperAnimation(holder.mImageView, 0, Utils.dp2px(mContext, 120))
                    }
                }
                8 -> {
                    performListenerAnimation(holder.mImageView, 0, Utils.dp2px(mContext, 120))
                    holder.mButton.setOnClickListener {
                        performListenerAnimation(holder.mImageView, 0, Utils.dp2px(mContext,120))
                    }
                }
                else -> {
                    holder.mImageView.animation = mAnimations[position]
                    holder.mButton.setOnClickListener {
                        holder.mImageView.startAnimation(mAnimations[position])
                    }
                }
            }
        }

        /**
         * RecyclerList设置每一项的加载动画
         *
         * @param viewToAnimate 项目视图
         * @param position      位置
         */
        private fun setAnimation(viewToAnimate: View, position: Int){
            if (position > mLastPosition || mLastPosition == -1){
                val animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left)
                animation.duration = 3000
                viewToAnimate.startAnimation(animation)
                mLastPosition = position
            }
        }
        /**
         * 通过差值执行属性动画
         *
         * @param view  目标视图
         * @param start 起始宽度
         * @param end   终止宽度
         */
        private fun performListenerAnimation(view: View, start: Int, end: Int){
            val valueAnimator = ValueAnimator.ofInt(1, 100)
            valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                val mEvaluator = IntEvaluator()
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    val currentValue = animation.animatedValue

                    val fraction = animation.animatedFraction
                    view.layoutParams.width = mEvaluator.evaluate(fraction, start, end)
                    view.requestLayout()
                }
            })
            valueAnimator.setDuration(2000).start()
        }

        /**
         * 通过Wrapper实现属性动画
         *
         * @param view  目标视图
         * @param start 起始宽度
         * @param end   终止宽度
         */
        private fun performWrapperAnimation(view: View, start: Int, end: Int){
            val vw = ViewWrapper(view)
            ObjectAnimator.ofInt(vw, "width", start, end).setDuration(2000).start()
        }

        // 视图包装, 提供Width的get和set方法
        inner class ViewWrapper(private val mView: View) {
            var width: Int
                get() = mView.layoutParams.width
                set(width) {
                    mView.layoutParams.width = width
                    mView.requestLayout()
                }
        }

        override fun getItemCount() = mTexts.size

    }
}

