package htf.scme.org.bgims;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/6/29.
 */
public class Utils{
  public static Bitmap createVideoThumbnail(String filePath) {
  // MediaMetadataRetriever is available on API Level 8
  // but is hidden until API Level 10
  Class<?> clazz = null;
  Object instance = null;
  try {
    clazz = Class.forName("android.media.MediaMetadataRetriever");
    instance = clazz.newInstance();

    Method method = clazz.getMethod("setDataSource", String.class);
    method.invoke(instance, filePath);

    // The method name changes between API Level 9 and 10.
    if (Build.VERSION.SDK_INT <= 9) {
      return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
    } else {
      byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
      if (data != null) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap != null) return bitmap;
      }
      return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
    }
  } catch (IllegalArgumentException ex) {
    // Assume this is a corrupt video file
  } catch (RuntimeException ex) {
    // Assume this is a corrupt video file.
  } catch (InstantiationException e) {
    Log.e("TAG", "createVideoThumbnail", e);
  } catch (InvocationTargetException e) {
    Log.e("TAG", "createVideoThumbnail", e);
  } catch (ClassNotFoundException e) {
    Log.e("TAG", "createVideoThumbnail", e);
  } catch (NoSuchMethodException e) {
    Log.e("TAG", "createVideoThumbnail", e);
  } catch (IllegalAccessException e) {
    Log.e("TAG", "createVideoThumbnail", e);
  } finally {
    try {
      if (instance != null) {
        clazz.getMethod("release").invoke(instance);
      }
    } catch (Exception ignored) {
    }
  }
  return null;
}

}
