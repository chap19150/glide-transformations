package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.wasabeef.glide.transformations.R;

/**
 * Created by kchapman on 6/15/15.
 */
public class InkwellFilterTransformation implements Transformation<Bitmap> {

    private Context mContext;
    private BitmapPool mBitmapPool;

    private InkwellFilter mFilter;

    public InkwellFilterTransformation(Context context, BitmapPool pool) {
        mContext = context;
        mBitmapPool = pool;
        mFilter = new InkwellFilter(context);
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        GPUImage gpuImage = new GPUImage(mContext);
        gpuImage.setImage(source);
        gpuImage.setFilter(mFilter);
        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();

        source.recycle();

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return "InkwellFilterTransformation";
    }

    public static class InkwellFilter extends BaseImageFilter {
        public static final String SHADER = "precision lowp float;\n" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
                "     texel = vec3(dot(vec3(0.3, 0.6, 0.1), texel));\n" +
                "     texel = vec3(texture2D(inputImageTexture2, vec2(texel.r, .16666)).r);\n" +
                "     gl_FragColor = vec4(texel, 1.0);\n" +
                " }";

        public InkwellFilter(Context context) {
            super(SHADER, 1);
            bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.inkwell_map);
        }
    }
}
