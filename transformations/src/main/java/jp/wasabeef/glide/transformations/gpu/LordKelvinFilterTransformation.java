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
public class LordKelvinFilterTransformation implements Transformation<Bitmap> {

    private Context mContext;
    private BitmapPool mBitmapPool;

    private LordKelvinFilter mFilter;

    public LordKelvinFilterTransformation(Context context, BitmapPool pool) {
        mContext = context;
        mBitmapPool = pool;
        mFilter = new LordKelvinFilter(context);
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
        return "LordKelvinFilterTransformation";
    }

    public static class LordKelvinFilter extends BaseImageFilter {
        public static final String SHADER = "precision lowp float;\n" +
                " varying highp vec2 textureCoordinate;\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " void main()\n" +
                " {\n" +
                "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
                "     vec2 lookup;\n" +
                "     lookup.y = .5;\n" +
                "     lookup.x = texel.r;\n" +
                "     texel.r = texture2D(inputImageTexture2, lookup).r;\n" +
                "     lookup.x = texel.g;\n" +
                "     texel.g = texture2D(inputImageTexture2, lookup).g;\n" +
                "     lookup.x = texel.b;\n" +
                "     texel.b = texture2D(inputImageTexture2, lookup).b;\n" +
                "     gl_FragColor = vec4(texel, 1.0);\n" +
                " }";

        public LordKelvinFilter(Context context) {
            super(SHADER, 1);
            bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.kelvin_map);
        }
    }
}
