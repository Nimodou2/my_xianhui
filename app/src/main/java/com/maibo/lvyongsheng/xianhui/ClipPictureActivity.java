package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.view.ClipView;
import com.maibo.lvyongsheng.xianhui.view.TouchImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by LYS on 2016/11/14.
 */
public class ClipPictureActivity extends Activity implements View.OnTouchListener {
    // 头部
    private TextView tv_right, tv_title;
    private ImageView srcPic, iv_left;

    private ClipView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;
    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;//缩放倍数
    private Bitmap bitmap;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_picture);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initview();
        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(srcPic.getTop());
            }
        });

    }

    private void initview() {

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        srcPic = (TouchImageView) findViewById(R.id.src_pic);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_title.setText("图片裁剪");
        srcPic.setOnTouchListener(this);
        url = getIntent().getStringExtra("picPath");
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void save() {
        Bitmap clipBitmap = getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//将图片转换成数组
        byte[] bitmapByte = baos.toByteArray();
        Intent intent = new Intent("account_active");//发广播   将图片的数组传过去
        intent.putExtra("bitmap", bitmapByte);
        sendBroadcast(intent);
        finish();
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                        + statusBarHeight, clipview.getClipWidth(),
                clipview.getClipHeight());
        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        ImageView view = (ImageView) v;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(motionEvent.getX(), motionEvent.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN://第二个手指按下
                oldDist = spacing(motionEvent);
                if (oldDist > 10f) {//缩放
                    savedMatrix.set(matrix);
                    midPoint(mid, motionEvent);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(motionEvent.getX() - start.x, motionEvent.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(motionEvent);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);//根据mid进行缩放
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        bitmap = getBitmapByPath(url);//将原图压缩到原来的1/2
        clipview = new ClipView(ClipPictureActivity.this);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {
            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ImageView.ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
            }
        });

        this.addContentView(clipview, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 根据图片路径获取图片对象
     *
     * @param path
     * @return
     */
    private Bitmap getBitmapByPath(String path) {
        File file = new File(path);
        Bitmap bm = null;
        if (file.exists()) {
            bm = BitmapFactory.decodeFile(path);
        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inSampleSize = 2;// 图片宽高都为原来的二分之一，即图片为原来的四分之一
        return bm;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
