package com.jerrellmardis.amphitheatre.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public final class BlurTransform implements Transformation {
    private final Context context;

    public BlurTransform(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap transform(Bitmap in) {
        Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), in.getConfig());
        out.setDensity(in.getDensity());

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, in);
        Allocation output = Allocation.createFromBitmap(rs, out);

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, getElement(in, rs));
        script.setInput(input);
        script.setRadius(20);
        script.forEach(output);

        output.copyTo(out);
        in.recycle();

        rs.destroy();

        return out;
    }

    private static Element getElement(Bitmap in, RenderScript rs) {
        switch (in.getConfig()) {
            case ARGB_8888:
                return Element.U8_4(rs);
            default:
                throw new IllegalArgumentException("Unsupported config: " + in.getConfig());
        }
    }

    @Override
    public String key() {
        return "blur()";
    }
}
