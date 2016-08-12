package htf.scme.org.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import htf.scme.org.MessageContent.VideoMessage;
import htf.scme.org.bgims.CameraImage;
import htf.scme.org.bgims.MovieRecorderView;
import htf.scme.org.bgims.R;
import htf.scme.org.bgims.Utils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.imlib.model.Conversation;

public class VideoActivity extends AppCompatActivity  {

  private MovieRecorderView mRecorderView;
  private Button mStartvideo;
  private boolean isFinish = true;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video);

    mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
    mStartvideo = (Button) findViewById(R.id.startvideo);
    mStartvideo.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, final MotionEvent motionEvent) {



            setmStartvideo(motionEvent);


        return false;
      }
    });

  }


  private void setmStartvideo( MotionEvent motionEvent){
    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
      mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

        @Override
        public void onRecordFinish() {
          handler.sendEmptyMessage(1);
        }
      });
    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
      if (mRecorderView.getTimeCount() > 1)
        handler.sendEmptyMessage(1);
      else {
        if (mRecorderView.getmRecordFile() != null)
          mRecorderView.getmRecordFile().delete();
        mRecorderView.stop();
        Toast.makeText(VideoActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
      }
    }
  }
  @Override
  public void onResume() {
    super.onResume();
    isFinish = true;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    isFinish = false;
    mRecorderView.stop();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      finishActivity();
    }
  };

  private void finishActivity() {
    if (isFinish) {
      mRecorderView.stop();
      // 返回到会话页面
   Intent intent = new Intent();
    Log.d("TAG",mRecorderView.getmRecordFile().getAbsolutePath());
    intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
    setResult(RESULT_OK,intent);


    }
    finish();
  }




  }
