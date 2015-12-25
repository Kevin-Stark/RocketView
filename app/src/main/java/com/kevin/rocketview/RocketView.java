package com.kevin.rocketview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Kevin on 2015/10/29.
 */
public class RocketView extends View {

	private int		mSpeed;		// 火箭来回运转速度，控制的是休眠事件，speed越大运动其实越慢

	private boolean	mHasPower;	// 是否驱动火箭运转

	private Bitmap	mRocketBitmapLeft, mRocketBitmapRight;	// 火箭背景图片(左右)

	private int		mViewWidth, mViewHeight;				// 整体控件的宽高

	private int		mBitmapWidth, mBitmapHeight;			// bitmap宽高

	private int		mProgress;								// 火箭走过的进度

	private boolean	isNext	= false;						// 火箭是否可以反转方向

	private Rect	mRect;

	private Paint	mPaint;

	public RocketView(Context context) {
		this(context, null);
	}

	public RocketView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RocketView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RocketView, defStyleAttr, 0);
		mSpeed = a.getInt(R.styleable.RocketView_speed, 0);
		mHasPower = a.getBoolean(R.styleable.RocketView_hasPower, false);
		mRocketBitmapLeft = BitmapFactory.decodeResource(getResources(),
				a.getResourceId(R.styleable.RocketView_rocketBgLeft, 0));
		mRocketBitmapRight = BitmapFactory.decodeResource(getResources(),
				a.getResourceId(R.styleable.RocketView_rocketBgRight, 0));
		mViewWidth = a.getDimensionPixelSize(R.styleable.RocketView_viewWidth,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 100, getResources().getDisplayMetrics()));
		mViewHeight = a.getDimensionPixelSize(R.styleable.RocketView_viewWidth,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 200, getResources().getDisplayMetrics()));
		a.recycle();
		mRect = new Rect();
		mPaint = new Paint();

		mBitmapWidth = mRocketBitmapLeft.getWidth();
		mBitmapHeight = mRocketBitmapLeft.getHeight();

		// 绘图线程
		new Thread() {
			public void run() {
				while (true) {
					mProgress = mProgress + 20;
					// 火箭走完了整个屏幕
					if (mProgress >= mViewWidth + mBitmapWidth) {
						mProgress = 0;
						if (!isNext)
							isNext = true;
						else
							isNext = false;
					}
					postInvalidate();
					try {
						Thread.sleep(mSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(20); // 设置圆环的宽度

		//火箭运动
		if (mHasPower) {
			if (isNext) {
				mRect.left = -mBitmapWidth + mProgress;
				mRect.top = (mViewHeight - mBitmapHeight) / 2;
				mRect.right = mProgress;
				mRect.bottom = (mViewHeight + mBitmapHeight) / 2;
				canvas.drawBitmap(mRocketBitmapLeft, null, mRect, mPaint);
			} else {
				mRect.left = mViewWidth - mProgress;
				mRect.top = (mViewHeight - mBitmapHeight) / 2;
				mRect.right = mViewWidth + mBitmapWidth - mProgress;
				mRect.bottom = (mViewHeight + mBitmapHeight) / 2;
				canvas.drawBitmap(mRocketBitmapRight, null, mRect, mPaint);
			}
		} else {
			// 火箭图片归起始位置
			mRect.left = -mBitmapWidth;
			mRect.top = (mViewHeight - mBitmapHeight) / 2;
			mRect.right = 0;
			mRect.bottom = (mViewHeight + mBitmapHeight) / 2;
			canvas.drawBitmap(mRocketBitmapLeft, null, mRect, mPaint);
			mRect.left = mViewWidth;
			mRect.top = (mViewHeight - mBitmapHeight) / 2;
			mRect.right = mViewWidth + mViewWidth;
			mRect.bottom = (mViewHeight + mBitmapHeight) / 2;
			canvas.drawBitmap(mRocketBitmapRight, null, mRect, mPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		mViewWidth = widthSize;// 宽度不设置了
		if (heightMode == MeasureSpec.EXACTLY) {
			mViewHeight = heightSize;
		} else {
			int desired = (int) (getPaddingTop() + 2 * mBitmapHeight + getPaddingBottom());
			mViewWidth = desired;
		}

		setMeasuredDimension(mViewWidth, mViewHeight);
	}

	public int getSpeed() {
		return mSpeed;
	}

	public void setSpeed(int mSpeed) {
		this.mSpeed = mSpeed;
		invalidate();
	}

	public boolean isHasPower() {
		return mHasPower;
	}

	public void setHasPower(boolean mHasPower) {
		this.mHasPower = mHasPower;
	}
}
