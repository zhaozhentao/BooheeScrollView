# BooheeScrollView

模仿薄荷食物图书馆效果的一个小demo

# ScreenShot


![image](https://github.com/zhaozhentao/BooheeScrollView/blob/master/screenshot/screen.gif)


# Usage
###step 1
    在布局文件里实现类似布局
    
    <com.zzt.library.BooheeScrollView
        android:layout_alignParentBottom="true"
        android:id="@+id/horizon"
        android:overScrollMode="never"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <com.zzt.library.BuildLayerLinearLayout
            android:id="@+id/linear"
            android:orientation="horizontal"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">
        </com.zzt.library.BuildLayerLinearLayout>
    </com.zzt.library.BooheeScrollView>
    
###step 2
    根据合适的大小创建出要显示的内容 并添加到 buildLayerLinearLayout中
        BezelImageView imageView2 = new BezelImageView(this);
        imageView2.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        buildLayerLinearLayout.addView(imageView2);
        imageView2.setImageBitmap(decodeSampledBitmapFromResource(getResources(),  R.drawable.pic2, width, height));
        imageView2.setMaskDrawable(getResources().getDrawable(R.drawable.roundrect));
###step 3
    将要旋转的内容添加到这里
    booheeScrollView.setChildViews(new View[]{
        cardView1, imageView1,imageView2, imageView3,
        imageView4, imageView5, imageView6, imageView7});
        

