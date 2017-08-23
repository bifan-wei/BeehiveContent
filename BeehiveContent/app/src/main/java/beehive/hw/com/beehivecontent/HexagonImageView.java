package beehive.hw.com.beehivecontent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by bifan-wei
 */

public class HexagonImageView extends ImageView {
    private String tag = "HexagonImageView";
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final Matrix mShaderMatrix = new Matrix();
    private static final Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mWidth = 0;
    private Bitmap mBitmap;

    public HexagonImageView(Context context) {
        super(context);
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() * getHeight() == 0) {
            return;
        }
        mBitmap = getBitmapFromDrawable(drawable);
        if (mBitmap == null) {
            return;
        }

        setup();
        canvas.drawPath(getHexagonPath(), mBitmapPaint);

    }

    private Path mPath = null;
    public Path getHexagonPath() {
        if (mPath == null) {
            mPath = new Path();
        }
            float d = (float) ((float)mWidth / 4 * (2 - Math.sqrt(3)));//六边形到边到内切圆的距离
            float r = mWidth/4;
            float p0x = r;
            float p0y = d;

            float p1x = r*3;
            float p1y = d;

            float p2x = mWidth;
            float p2y = mWidth/2;

            float p3x = p1x;
            float p3y = mWidth-d;

            float p4x = p0x;
            float p4y = p3y;

            float p5x = 0;
            float p5y = p2y;

            mPath.reset();
            mPath.moveTo(p0x,p0y);
            mPath.lineTo(p1x,p1y);
            mPath.lineTo(p2x,p2y);
            mPath.lineTo(p3x,p3y);
            mPath.lineTo(p4x,p4y);
            mPath.lineTo(p5x,p5y);
            mPath.lineTo(p0x,p0y);

        return mPath;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//限制为正方形
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(mWidth, mWidth);
    }

    public  int getmWidth() {
        return mWidth;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        invalidate();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        invalidate();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, mWidth, mWidth);
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (mBitmap != null) {
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        updateShaderMatrix();
        mBitmapPaint.setShader(mBitmapShader);
        }
    }



    private void updateShaderMatrix() {
        float scale;
        mShaderMatrix.set(null);
        if (mBitmapWidth != mBitmapHeight) {
            scale = Math.max((float) mWidth / mBitmapWidth, (float) mWidth / mBitmapHeight);
        } else {
            scale = (float) mWidth / mBitmapWidth;
        }

        mShaderMatrix.setScale(scale, scale);//放大铺满

        float dx = mWidth - mBitmapWidth * scale;
        float dy = mWidth - mBitmapHeight * scale;
        mShaderMatrix.postTranslate(dx / 2, dy / 2);//平移居中
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }


}
