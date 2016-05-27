package cn.finalteam.galleryfinal.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**
 * 图片压缩工具类
 * 
 * @author Administrator
 * 
 */
public class ImageCompress {

    public static final String CONTENT = "content";
    public static final String FILE = "file";

    /**
     * 图片压缩参数
     * 
     * @author Administrator
     * 
     */
    public static class CompressOptions {
        public static final int DEFAULT_limit = 960;

        public int maxWidth = DEFAULT_limit;
        public int maxHeight = DEFAULT_limit;
        /**
         * 压缩后图片保存的文件路径
         */
        public String destFilePath;
        /**
         * 图片压缩格式,默认为jpg格式
         */
        public CompressFormat imgFormat = CompressFormat.JPEG;

        /**
         * 图片压缩比例 默认为80
         */
        public int quality = 75;

        public String srcFilePath;
    }

    public Bitmap compressFromFilePath(Context context,
            CompressOptions compressOptions) {

        if (null == compressOptions.srcFilePath) {
            return null;
        }
        if(!new File(compressOptions.srcFilePath).exists()){
        	return null;
        }
        
        int degree  = getBitmapDegree(compressOptions.srcFilePath);
        //读取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(compressOptions.srcFilePath, options);
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        float desiredWidth = compressOptions.maxWidth;
        float desiredHeight = compressOptions.maxHeight;
        if(actualHeight > desiredHeight || actualWidth > desiredWidth){
        	if(actualWidth > actualHeight){
        		desiredHeight = desiredWidth / actualWidth * actualHeight;
        	}else if(actualHeight > actualWidth){
        		desiredWidth = desiredHeight / actualHeight * actualWidth;
        	}
        }else{
        	//保持原图大小
        	desiredHeight = actualHeight;
        	desiredWidth = actualWidth;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = findBestSampleSize(actualWidth/desiredWidth );
        Bitmap srcBitmap = BitmapFactory.decodeFile(compressOptions.srcFilePath, options);
    	Bitmap bitmap = null;
        if (srcBitmap.getWidth() > desiredWidth
                || srcBitmap.getHeight() > desiredHeight) {
        	//缩放之后大小超过目标大小,再次压缩
            bitmap = Bitmap.createScaledBitmap(srcBitmap, (int)desiredWidth,
                    (int)desiredHeight, true);
            srcBitmap.recycle();
        } else {
            bitmap = srcBitmap;
        }
        if(degree > 0){
        	bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        if (null != compressOptions.destFilePath) {
            saveToFile(compressOptions, bitmap);
        }
        return null;
    }

    /**
     * compress file from bitmap with compressOptions
     * 
     * @param compressOptions
     * @param bitmap
     */
    private void saveToFile(CompressOptions compressOptions, Bitmap bitmap) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(compressOptions.destFilePath);
        } catch (FileNotFoundException e) {
            Log.e("ImageCompress", e.getMessage());
        }
        bitmap.compress(compressOptions.imgFormat, compressOptions.quality,
                stream);
        if(bitmap != null){
        	bitmap.recycle();
        }
        if(stream != null){
        	try {
				stream.close();
			} catch (IOException e) {
			}
        }
    }

    private static int findBestSampleSize(double ratio) {
        float n = 1.0f;
        while ((n * 2) < ratio) {
            n *= 2;
        }
        return (int) n;
    }
    
    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    @SuppressLint("NewApi") 
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            //当内存溢出时，利用递归进行重新旋转
            while (returnBm == null) {
                System.gc();
                System.runFinalization();
                returnBm = rotateBitmapByDegree(bm, degree);
            }
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
}
