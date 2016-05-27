package cn.finalteam.galleryfinal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import cn.finalteam.galleryfinal.widget.camera.CameraPreview;
import cn.finalteam.galleryfinal.widget.camera.FocusView;

public class TakePhoteActivity extends Activity implements CameraPreview.OnCameraStatusListener{
    private static final String TAG = "TakePhoteActivity";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private String imagename = "temp.jpg";
    CameraPreview mCameraPreview;
    RelativeLayout mTakePhotoLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagename = getIntent().getStringExtra("imageName");
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_phote);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        FocusView focusView = (FocusView) findViewById(R.id.view_focus);
        mTakePhotoLayout = (RelativeLayout) findViewById(R.id.take_photo_layout);

        mCameraPreview.setFocusView(focusView);
        mCameraPreview.setOnCameraStatusListener(this);

    }

    boolean isRotated = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void takePhoto(View view) {
        if(mCameraPreview != null) {
            mCameraPreview.takePicture();
            findViewById(R.id.ok).setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }
    }

    public void close(View view) {
    	int id = view.getId();
    	if(id == R.id.ok){
    		setResult(Activity.RESULT_OK);
    		finish();
    	}
    	if(id == R.id.close){
    		File tempFile = new File(GalleryFinal.getCoreConfig().getTakePhotoFolder() + imagename);
    		if(tempFile.exists()){
    			tempFile.delete();
    		}
    		setResult(Activity.RESULT_CANCELED);
    		finish();
    	}
    }

    /**
     * 拍照成功后回调
     * @param data
     */
    @Override
    public void onCameraStopped(byte[] data) {
        // 系统时间
        long dateTaken = System.currentTimeMillis();
        // 存储图像
        insertImage(getContentResolver(), dateTaken, GalleryFinal.getCoreConfig().getTakePhotoFolder().getAbsolutePath(),
        		imagename, data);
    }

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private Uri insertImage(ContentResolver cr, long dateTaken,
                            String directory, String filename, byte[] jpegData) {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                outputStream.write(jpegData);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        ContentValues values = new ContentValues(5);
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        return cr.insert(IMAGE_URI, values);
    }

}
