package com.wjxl.appintro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_GALLERY = 1001;
    private final String TAG = "MainActivity";

    BridgeWebView webView;
    Button button;
    Button btn_change;
    ImageView image;
    //模拟用户获取本地位置
    User user = new User();

    final List<String> listBitmap = new ArrayList<String>();

    private ArrayList<String> path = new ArrayList<>();

    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
            "/wjhl/"));

    String pathUrl = String.valueOf(Environment.getExternalStorageDirectory());

    ValueCallback<Uri> mUploadMessage;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    webView.callHandler("lalala", listBitmap.toString(), new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {
                            Log.d(TAG, "onCallBack: " + data);
                            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
//                webView.callHandler("getpicturebyte", new Gson().toJson(user), new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//
//                    }
//                });

                //带配置

                break;

            case R.id.btn_change:
                String str = listBitmap.toString();
                byte[] by = Base64.decode(str, Base64.NO_WRAP);
                image.setImageBitmap(Bytes2Bimap(by));
                break;
        }
    }

    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static Bitmap getBitmapByPath(String path) {
        if (path == null)
            return null;
        Bitmap temp = null;
        if (temp == null) {
            try {
                temp = BitmapFactory.decodeFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    public void SaveBitmap(Bitmap img) {
        File myCaptureFile = new File(Environment.getExternalStorageDirectory() + "/aaaaaaa.jpg");
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            img.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 返回bitmap的大小
     * returns the bytesize of the give bitmap
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }


    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (BridgeWebView) findViewById(R.id.webView);
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.btn_send);
        btn_change = (Button) findViewById(R.id.btn_change);
        button.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        webView.setDefaultHandler(new DefaultHandler());
        Location location = new Location();
        location.address = "上海";
        user.location = location;
        user.name = "Bruce";


        webView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });
        //加载本地网页
        webView.loadUrl("file:///android_asset/testWebViewJsBridge.html");
        //加载服务器网页
        //webView.loadUrl("https://www.baidu.com");

        //必须和js同名函数，注册具体执行函数，类似java实现类。
        webView.registerHandler("takepicture", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {

                //String str = "这是html返回给java的数据:" + data;
                // 弹出选择框
                showPopFormBottom();
                function.onCallBack("图片字节流" + imageUri);
            }

        });
        webView.registerHandler("openFile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String str = "这是html返回给java的数据:" + data;
                //pickFile();
                function.onCallBack(str);
            }
        });

        webView.registerHandler("saveFile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String str = "这是java返回给html的数据:" + data;
                function.onCallBack(str);
            }
        });
        webView.send("hello");

    }

    TakePhotoPopWin takePhotoPopWin;

    //弹出图片选择框
    public void showPopFormBottom() {
        View view = LayoutInflater.from(this).inflate(R.layout.take_photo_pop, null);
        takePhotoPopWin = new TakePhotoPopWin(this, onClickListener);
        //showAtLocation(View parent, int gravity, int x, int y)
        takePhotoPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_take_photo:
                    pickFile();
                    break;
                case R.id.btn_pick_photo:
//                    //带配置
                    FunctionConfig config = new FunctionConfig.Builder()
                            .setMutiSelectMaxSize(10)
                            .setEnablePreview(true)
                            .build();
                    GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, new GalleryFinal.OnHanlderResultCallback() {
                        @Override
                        public void onHanlderSuccess(int reqeustCode, final List<PhotoInfo> resultList) {
                            //Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_SHORT).show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {


                                    //Log.d(TAG, "onHanlderSuccess: 压缩之前 " + byteSizeOf(bitmap));
                                    //Log.d(TAG, "onHanlderSuccess: 压缩之前图片宽高 " +resultList.get(0).getWidth()+"==="+bitmap.getHeight());
                                    for (int i = 0; i < resultList.size(); i++) {
                                        Bitmap bitmap = getimage(resultList.get(i).getPhotoPath());
                                        SaveBitmap(bitmap);
                                        //image.setImageBitmap(bitmap);
                                        byte[] by = Bitmap2Bytes(bitmap);
                                        String str = Base64.encodeToString(by, Base64.NO_WRAP);
                                        listBitmap.add(str);
                                        Log.d(TAG, "onHanlderSuccess: 压缩之后 " + listBitmap.get(0).toString());
                                    }
                                    handler.sendEmptyMessage(1);
                                }
                            }).start();
                        }

                        @Override
                        public void onHanlderFailure(int requestCode, String errorMsg) {
                            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    };

    private Bitmap comp(Bitmap image, float width, float height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap BitmapCompressUtil(Bitmap image, int size) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 85, out);
        float zoom = (float) Math.sqrt(size * 1024 / (float) out.toByteArray().length);

        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);

        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        out.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        while (out.toByteArray().length > size * 1024) {
            System.out.println(out.toByteArray().length);
            matrix.setScale(0.9f, 0.9f);
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            out.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        }
        return result;
    }

    public void pickFile() {
        Date date = new Date();
        pathUrl = Environment.getExternalStorageDirectory()+
                "/wjhl/" + date.getTime() + ".jpg";
        imageUri = Uri.fromFile(new File(pathUrl));
        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(chooserIntent, 1);
    }

    String params;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Log.d(TAG, "onActivityResult: ----------------"+data.getExtras().get("data").toString());
            Bitmap bitmap1 = getimage(pathUrl);
            //image.setImageBitmap(bitmap1);
            byte[] by = Bitmap2Bytes(bitmap1);
            String str = Base64.encodeToString(by, Base64.NO_WRAP);
            Log.d(TAG, "onActivityResult: 拍照图片" + str);
            listBitmap.add(str);
            handler.sendEmptyMessage(1);

        }
    }


    /**
     * @param
     * @param bytes
     * @param opts
     * @return Bitmap
     */
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 质量压缩法
     *
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 按比例大小压缩方法
     *
     * @param srcPath
     * @return
     */
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
}
