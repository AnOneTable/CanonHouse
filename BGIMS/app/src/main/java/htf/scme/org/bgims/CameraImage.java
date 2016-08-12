package htf.scme.org.bgims;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/30.
 */
public class CameraImage {

    /**
     * 如果方向不正确就转换方向
     * @author HTF
     * created at 2015/12/23 16:23
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取图片的位置
     *
     * @author HTF
     * created at 2015/12/23 16:22
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0) {
            return b;
        }
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth(),
                    (float) b.getHeight());
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {

            }
        }
        return b;
    }


    /**
     * 打开照相机
     */
    public static Uri openCarcme(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        Uri photoUri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        ((Activity)context).startActivityForResult(intent, 0);
        return photoUri;
    }

    /**
     * 本地相册选择图片
     */
    public static void chosePic(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //* 开启Pictures画面Type设定为image *//*
            // intent.setType("image/*");
            //* 使用Intent.ACTION_GET_CONTENT这个Action *//*
            //  intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            //* 取得相片后返回本画面 *//*
            /*SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            photoUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
           intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);*/
            ((Activity)context).startActivityForResult(intent, 1);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("-------------------选择照片", e.toString());
        }

    }
    /**
     * 转物理路径
     * @author HTF
     * created at 2015/12/30 10:20
     */
    public  static String getRealFilePath(final Uri uri,Context context) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    /**
     * 检查sdka
     * @author HTF
     * created at 2015/12/23 9:56
     */
    public  static boolean checkSDcard(Context c) {
        boolean flag = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(c, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    /**
     * 把正确方向的bitmap图片，转换成2进制
     * @author HTF
     * created at 2015/12/23 16:24
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);//将bitmap放入字节数组流中
                bos.flush();//将bos流缓存在内存中的数据全部输出，清空缓存
                bos.close();
                byte[] bitmapByte = bos.toByteArray();
                result = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /** 保存方法 */
    public static File saveBitmap(Bitmap bm) {
        Log.e("", "保存图片");
        File f = new File("/sdcard/namecard/", "thumb.jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("", "已经保存");
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  f;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
