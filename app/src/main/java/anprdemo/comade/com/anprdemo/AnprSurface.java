package anprdemo.comade.com.anprdemo;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Daniel on 30/01/2015.
 */
public class AnprSurface extends SurfaceView {
    private SurfaceHolder _holder;
    private Camera _camrera;

    public AnprSurface(Context context) {
        super(context);
    }
}
