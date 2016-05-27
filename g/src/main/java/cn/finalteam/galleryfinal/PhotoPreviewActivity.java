package cn.finalteam.galleryfinal;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.finalteam.galleryfinal.adapter.PhotoPreviewAdapter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFViewPager;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 14:43
 */
public class PhotoPreviewActivity extends PhotoBaseActivity implements ViewPager.OnPageChangeListener{

    static final String PHOTO_LIST = "photo_list";

    private RelativeLayout mTitleBar;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvIndicator;
//    private Button sendBtn;
//    private RadioButton radioButton;
    private boolean isSource = false;
    private GFViewPager mVpPager;
    private List<PhotoInfo> mPhotoList;
    private PhotoPreviewAdapter mPhotoPreviewAdapter;

    private ThemeConfig mThemeConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeConfig = GalleryFinal.getGalleryTheme();

        if ( mThemeConfig == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.gf_activity_photo_preview);
            findViews();
            setListener();
            setTheme();

            mPhotoList = (List<PhotoInfo>) getIntent().getSerializableExtra(PHOTO_LIST);
            mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, mPhotoList);
            mVpPager.setAdapter(mPhotoPreviewAdapter);
        }
    }

    private void findViews() {
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);

        mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
        boolean isTakePhoto = getIntent().getBooleanExtra("isTakePhoto", false);
        if(isTakePhoto){
        	mIvBack.setVisibility(View.GONE);
        	findViewById(R.id.bottombar).setVisibility(View.VISIBLE);
//        	sendBtn = (Button) findViewById(R.id.sendBtn);
//        	radioButton = (RadioButton) findViewById(R.id.CheckBox);
//        	radioButton.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					isSource = !isSource;
//					if(!isSource){
//						radioButton.setChecked(isSource);
//						radioButton.setText("原图");
//					}else{
//						radioButton.setChecked(isSource);
//						File imgFile = new File(mPhotoList.get(0).getPhotoPath());
//						if(imgFile.exists()){
//							double size = imgFile.length();
//							String type = "";
//							if(size >= 1024*1024){
//								//MB
//								size = size/(1024*1024);
//								type = new DecimalFormat("0.00").format(size)+"MB";
//							}else if(size >= 1024){
//								//KB
//								size = size/(1024);
//								type = new DecimalFormat("0.00").format(size)+"KB";
//							}else{
//								//B
//								type = new DecimalFormat("0.00").format(size)+"B";
//							}
//							radioButton.setText("原图("+type+")");
//						}
//					}
//				}
//			});
        }
    }

    private void setListener() {
        mVpPager.addOnPageChangeListener(this);
        mIvBack.setOnClickListener(mBackListener);
//        if(sendBtn != null){
//        	sendBtn.setOnClickListener(mBackListener);
//        }
    }

    @SuppressLint("NewApi") 
    private void setTheme() {
        mIvBack.setImageResource(mThemeConfig.getIconBack());
        if (mThemeConfig.getIconBack() == R.drawable.ic_gf_back) {
            mIvBack.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        mTitleBar.setBackgroundColor(mThemeConfig.getTitleBarBgColor());
        mTvTitle.setTextColor(mThemeConfig.getTitleBarTextColor());
        if(mThemeConfig.getPreviewBg() != null) {
            mVpPager.setBackgroundDrawable(mThemeConfig.getPreviewBg());
        }
    }

    @Override
    protected void takeResult(PhotoInfo info) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTvIndicator.setText((position + 1) + "/" + mPhotoList.size());
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	int id = v.getId();
        	if(id == R.id.iv_back){
        		finish();
        	}
//            else if(id == R.id.sendBtn){
//        		Intent data = new Intent();
//        		data.putExtra("path", mPhotoList.get(0).getPhotoPath());
//        		data.putExtra("isSource", isSource);
//        		setResult(Activity.RESULT_OK, data);
//        		finish();
//        	}
        }
    };

}
