package com.example.animationplayer

import android.graphics.Camera
import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 *
 *****************************************
 *  Created By LiXin  2/23/21 2:59 PM
 *****************************************
 *
 */
class Rotate3dAnimation(
    fromDegrees: Float, toDegrees: Float,
    centerX: Float, centerY: Float,
    depthZ: Float, reverse: Boolean
) : Animation() {
    // 起始角度
    private val mFromDegrees: Float = fromDegrees
    // 目标角度
    private val mToDegrees: Float = toDegrees
    // 旋转中心的X轴
    private val mCenterX: Float = centerX
    // 旋转中心的Y轴
    private val mCenterY: Float = centerY
    // 深度Z轴
    private val mDepthZ: Float = depthZ
    // 是否反转
    private val mReverse: Boolean = reverse
    private lateinit var mCamera: Camera
    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime // 结尾度数

        // 中心点
        val centerX = mCenterX
        val centerY = mCenterY
        val camera: Camera = mCamera
        val matrix: Matrix = t.matrix
        camera.save() // 照相机

        // Z轴平移
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime)
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime))
        }
        camera.rotateY(degrees) // Y轴旋转
        camera.getMatrix(matrix)
        camera.restore()

        // View的中心点进行旋转
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerX)
        super.applyTransformation(interpolatedTime, t)
    }
}