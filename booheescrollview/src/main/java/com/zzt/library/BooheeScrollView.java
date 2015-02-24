package com.zzt.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by zzt on 2015/2/15.
 */
public class BooheeScrollView extends HorizontalScrollView{

    private static final int DEFAULT_DURATION = 300;
    private ObjectAnimator scrollAnimator;
    private VelocityTracker mVelocityTracker;
    private Spring mSpring;
    private float mLastMotionX, mLastMotionY;
    private float mTouchSlop;
    private int mMaxVelocity;
    private boolean mIsDragging;
    private boolean doReboundAnim = false;
    private View[] mChildViews;
    private View centerView;
    private int childCount = 0;
    private int centerViewIndex;
    private int mCurrentScrollX,mCurrentScrollXEnd;
    private int dstScrollX;
    private int mWidth;
    public static final int NORMAL_ANIM = 0;
    public static final int REBOUND_ANIM = 1;
    private int ANIM_TYPE = NORMAL_ANIM;

    public BooheeScrollView(Context context) {
        this(context, null, 0);
    }

    public BooheeScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BooheeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setLayerType(LAYER_TYPE_HARDWARE, null);
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaxVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        scrollAnimator = ObjectAnimator.ofInt(this, scrollAnim, 0, 0);
        scrollAnimator.setDuration(DEFAULT_DURATION);
        scrollAnimator.setInterpolator(new DecelerateInterpolator());
        scrollAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mVelocityTracker = VelocityTracker.obtain();

        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();
        SpringConfig config = new SpringConfig(70, 9);
        mSpring.setSpringConfig(config);
        mSpring.setCurrentValue(0);
        mSpring.addListener(new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                if(doReboundAnim){
                    int x = -(int)(spring.getCurrentValue()*scrollDx)+beginScrollX;
                    if(x <= 0 || (x+mWidth) >= computeHorizontalScrollRange()){ //这里具体的值在不同情况下不同  当scrollView的宽度是match parent时候用mWidth
                        doReboundAnim = false;
                    }
                    scrollTo(x, 0);
                }
            }
        });
    }

    private boolean onDownAllowDrag(float lastMotionX, float lastMotionY){
        return true;
    }

    private void stopScrolling(){
        if(scrollAnimator.isRunning()){
            scrollAnimator.cancel();
        }
    }

    private boolean checkTouchSlop(float dx, float dy){
        return Math.abs(dx)>mTouchSlop && Math.abs(dx)>Math.abs(dy);
    }

    private boolean onMoveAllowDrag(float dx, float dy){
        return Math.abs(dx)>Math.abs(dy);
    }

    private void onMoveEvent(float dx, float dy){
        scrollTo(mCurrentScrollX-(int)dx, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mCurrentScrollX = l;
        mCurrentScrollXEnd = l + mWidth;
        setVisibleChildRotation();
    }

    /**
     * 旋转可见的部分
     * */
     private int scrollViewCenterX;
     private int viewLeft, viewRight;
     private float rotation;
     private float windowCenterX;
     private boolean isFirstVisible = false;
     private void setVisibleChildRotation(){
        isFirstVisible = false;
        scrollViewCenterX = (mCurrentScrollX + mCurrentScrollXEnd)/2;
        windowCenterX = mCurrentScrollX + mWidth/2;

        for(int i=0; i<childCount; ++i){
            viewLeft = mChildViews[i].getLeft();
            viewRight= mChildViews[i].getRight();
            if (mCurrentScrollX <= viewLeft && mCurrentScrollXEnd >= viewLeft
                    || mCurrentScrollX <= viewRight && mCurrentScrollXEnd >= viewRight){
                isFirstVisible = true;
                rotation = (((viewLeft +viewRight)/2 - scrollViewCenterX) *10 / (float)mWidth)*1.5f;
                mChildViews[i].setRotation(rotation);
                mChildViews[i].setTranslationY(Math.abs(rotation * 3));
                /**
                 * 找出处于屏幕中间的view
                 * */
                if(!(viewLeft <= windowCenterX && viewRight >= windowCenterX)){
                    continue;
                }
                if(centerView != mChildViews[i]) {
                    centerView = mChildViews[i];
                    centerViewIndex = i;
                    if(mScrollChangeListener != null){
                        mScrollChangeListener.OnScrollChange(i);
                    }
                }
            }else if(isFirstVisible){
                /**
                 * 不再遍历后面不可见部分
                 * */
                break;
            }
        }
    }

    private int scrollDx;
    private int beginScrollX;
    private void startReboundAnim(){
        scrollDx = mCurrentScrollX - dstScrollX;
        beginScrollX = mCurrentScrollX;
        mSpring.setCurrentValue(0f);
        doReboundAnim = true;
        mSpring.setEndValue(1);
    }

    private void stopReboundAnim(){
        doReboundAnim = false;
        double stopValue = mSpring.getCurrentValue();
        mSpring.setCurrentValue(stopValue);
    }

    private void startScrollAnim(){
        scrollAnimator.setIntValues(mCurrentScrollX, dstScrollX);
        scrollAnimator.start();
    }

    private void onActionUp(){
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        final int initialVelocity = (int) mVelocityTracker.getXVelocity();
        mVelocityTracker.clear();

        calcScrollDst(initialVelocity);

        if(ANIM_TYPE == NORMAL_ANIM)
            startScrollAnim();
        else
            startReboundAnim();
    }

    private void calcScrollDst(int initialVelocity){
        if(Math.abs(initialVelocity)<300){
            dstScrollX = centerView.getLeft() + centerView.getWidth()/2 - mWidth/2;
            startScrollAnim();
            return ;
        }

        if(initialVelocity>0){
            if(centerViewIndex>=2){
                dstScrollX = mChildViews[centerViewIndex-1].getLeft() + mChildViews[centerViewIndex-1].getWidth()/2 - mWidth/2;
            }else{
                dstScrollX = centerView.getLeft() + centerView.getWidth()/2 - mWidth/2;
            }
        }else{
            if(centerViewIndex <= childCount - 2){
                dstScrollX = mChildViews[centerViewIndex+1].getLeft() + mChildViews[centerViewIndex+1].getWidth()/2 - mWidth/2;
            }else{
                dstScrollX = centerView.getLeft() + centerView.getWidth()/2 - mWidth/2;
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();

        if(mChildViews!=null){
            for(int i=0; i<childCount; ++i){
                view = mChildViews[i];
                view.setPivotX(view.getWidth()/2);
                view.setPivotY(view.getHeight());
            }
            mCurrentScrollX = getScrollX();
            mCurrentScrollXEnd = mCurrentScrollX + mWidth;
            setVisibleChildRotation();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                if(onDownAllowDrag(mLastMotionX, mLastMotionY)){
                    mIsDragging = false;// action down 未开始drag
                    if(ANIM_TYPE == NORMAL_ANIM)
                        stopScrolling();
                    else
                        stopReboundAnim();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                if(checkTouchSlop(x - mLastMotionX, y - mLastMotionY)){
                    mIsDragging = true;
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                onActionUp();
                break;
        }
        return mIsDragging;
    }

   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);

        switch(ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                onMoveEvent(x - mLastMotionX, y - mLastMotionY);
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                onActionUp();
                break;
        }
        return true;
    }

    public void setChildViews(View[] views){
        mChildViews = views;
        childCount = mChildViews.length;
    }

    private int dx;
    Property<BooheeScrollView, Integer> scrollAnim = new Property<BooheeScrollView, Integer>(Integer.class, "mCurrentScrollX"){
        @Override
        public Integer get(BooheeScrollView object) {
            return object.mCurrentScrollX;
        }

        @Override
        public void set(BooheeScrollView object, Integer value) {
            scrollTo(value, 0);
        }
    };

    private OnScrollChangeListener mScrollChangeListener;
    public interface OnScrollChangeListener{
        public void OnScrollChange(int centerViewIndex);
    }

    public void setScrollChangeListener(OnScrollChangeListener scrollChangeListener){
        mScrollChangeListener = scrollChangeListener;
    }

    public void setAnimType(int animType){
        if(animType != NORMAL_ANIM && animType != REBOUND_ANIM ){
            throw new IllegalArgumentException("animType should be NORMAL_ANIM or REBOUND_ANIM");
        }
        ANIM_TYPE = animType;
    }

}
