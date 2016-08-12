package htf.scme.org.Activity;


import android.support.v4.app.FragmentActivity;

import htf.scme.org.bgims.R;
import io.rong.imkit.tools.PhotoFragment;
import io.rong.imlib.RongIMClient;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

import android.net.Uri;
import android.os.Bundle;

import android.view.Window;


public class PhotoActivity extends FragmentActivity {
	    PhotoFragment mPhotoFragment;
	    Uri mUri;
	    Uri mDownloaded;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);
		initView();
		initData();

	}

	    protected void initView() {
	        mPhotoFragment = (PhotoFragment) getSupportFragmentManager().getFragments().get(0);

	    }

	    protected void initData() {

			Message message = getIntent().getParcelableExtra("message");
			ImageMessage imageMessage = (ImageMessage) message.getContent();
			Uri uri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
	        mUri = uri;
	        if (uri != null)


	            mPhotoFragment.initPhoto(message, new PhotoFragment.PhotoDownloadListener() {
	                @Override
	                public void onDownloaded(Uri uri) {
	                    mDownloaded = uri;
	                }
	                @Override
	                public void onDownloadError() {

	                }
	            });


	    }

}
