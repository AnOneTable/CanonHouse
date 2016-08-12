package htf.scme.org.bgims;

import android.content.Context;
import android.content.res.TypedArray;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MovieRecorderView extends LinearLayout implements MediaRecorder.OnErrorListener {

  /**
   * SurfaceView Android中一个拥有独立绘图表面的视图，由于拥有独立的绘图表面，因此SufaceView的Ui就
   * 可以在一个独立的线程中进行绘制。又由于不会占用主线程资源，往往用于实现高效而复杂的Ui。
   */
  private SurfaceView mSurfaceView;
  /**
   * 显示一个surface的抽象接口，使你可以控制surface的大小和格式， 以及在surface上编辑像素，
   * 和监视surace的改变。
   */
  private SurfaceHolder mSurfaceHolder;
  /**
   * Android 的进度条
   */
  private ProgressBar mProgressBar;

  /**
   * 用来录制和拍摄视频的类
   */
  private MediaRecorder mMediaRecorder;

  private Camera mCamera;

  private Timer mTimer;// 计时器

  private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

  private int mWidth;// 视频分辨率宽度

  private int mHeight;// 视频分辨率高度

  private boolean isOpenCamera;// 是否一开始就打开摄像头

  private int mRecordMaxTime;// 一次拍摄最长时间

  private int mTimeCount;// 时间计数

  private File mRecordFile = null;// 文件


  public MovieRecorderView(Context context) {
    this(context, null);
  }

  public MovieRecorderView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // 初始化各项组件
    /**
     * TypedArray主要有两个作用，第一是内部去转换attrid和属性值数组的关系；
     * 第二是提供了一些类型的自动转化，比如我们getString时，如果你是通过@string/hello这种方式设置的，
     * TypedArray会自动去将ResId对应的string从资源文件中读出来。
     * 说到底，都是为了方便我们获取属性参数。
     */
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);

    mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 640);// 默认320
    mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 480);// 默认240


    isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
    mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// 默认为10


    LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
    mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

    mSurfaceHolder = mSurfaceView.getHolder();
    mSurfaceHolder.addCallback(new CustomCallBack());
    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    a.recycle();
  }


  private class CustomCallBack implements SurfaceHolder.Callback {

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      if (!isOpenCamera)
        return;
      try {
        initCamera();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      if (!isOpenCamera)
        return;
      freeCameraResource();
    }

  }


  /**
   * 初始化 摄像头
   *
   * @throws IOException
   */
  private void initCamera() throws IOException {
    if (mCamera != null) {
      freeCameraResource();
    }
    try {
      mCamera = Camera.open();
    } catch (Exception e) {
      e.printStackTrace();
      freeCameraResource();
    }
    if (mCamera == null)
      return;


    mCamera.setDisplayOrientation(90);
    mCamera.setPreviewDisplay(mSurfaceHolder);
    mCamera.startPreview();
    mCamera.unlock();
  }

  /**
   * 释放摄像头资源
   */
  private void freeCameraResource() {
    if (mCamera != null) {
      mCamera.setPreviewCallback(null);
      mCamera.stopPreview();
      mCamera.lock();
      mCamera.release();
      mCamera = null;
    }
  }

  private void createRecordDir() {
    File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "im/video/");
    if (!sampleDir.exists()) {
      sampleDir.mkdirs();
    }
    File vecordDir = sampleDir;
    // 创建文件
    try {
      mRecordFile = File.createTempFile("recording", ".mp4", vecordDir); //mp4格式
      Log.i("TAG", mRecordFile.getAbsolutePath());
    } catch (IOException e) {
    }
  }


  /**
   * 初始化
   */
  private void initRecord() throws IOException {
    mMediaRecorder = new MediaRecorder();
    mMediaRecorder.reset();
    if (mCamera != null)
      mMediaRecorder.setCamera(mCamera);
    mMediaRecorder.setOnErrorListener(this);
    mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
    mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
    // mMediaRecorder.setVideoFrameRate(16);// 这个我把它去掉了，感觉没什么用
    mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);// 设置帧频率，然后就清晰了
    mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
    // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);

    mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
    mMediaRecorder.prepare();
    try {
      mMediaRecorder.start();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 开始录制视频
   * @param
   * @param onRecordFinishListener 达到指定时间之后回调接口

   */
  public void record(final OnRecordFinishListener onRecordFinishListener) {
    this.mOnRecordFinishListener = onRecordFinishListener;
    createRecordDir();
    try {
      if (!isOpenCamera)// 如果未打开摄像头，则打开
        initCamera();
      initRecord();
      mTimeCount = 0;// 时间计数器重新赋值
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {

        @Override
        public void run() {
          // TODO Auto-generated method stub
          mTimeCount++;
          mProgressBar.setProgress(mTimeCount);// 设置进度条
          if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
            stop();
            if (mOnRecordFinishListener != null)
              mOnRecordFinishListener.onRecordFinish();
          }
        }
      }, 0, 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 停止拍摄
   */
  public void stop() {
    stopRecord();
    releaseRecord();
    freeCameraResource();
  }


  /**
   * 停止录制
   */
  public void stopRecord() {
    mProgressBar.setProgress(0);
    if (mTimer != null)
      mTimer.cancel();
    if (mMediaRecorder != null) {
      // 设置后不会崩
      mMediaRecorder.setOnErrorListener(null);
      try {
        mMediaRecorder.stop();
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (RuntimeException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
      mMediaRecorder.setPreviewDisplay(null);
    }
  }


  /**
   * 释放资源
   */
  private void releaseRecord() {
    if (mMediaRecorder != null) {
      mMediaRecorder.setOnErrorListener(null);
      try {
        mMediaRecorder.release();
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    mMediaRecorder = null;
  }

  public int getTimeCount() {
    return mTimeCount;
  }

  /**
   * @return the mVecordFile
   */
  public File getmRecordFile() {
    return mRecordFile;
  }

  /**
   * 录制完成回调接口
   */
  public interface OnRecordFinishListener {
    public void onRecordFinish();
  }

  @Override
  public void onError(MediaRecorder mediaPlayer, int i, int i1) {
    try {
      if (mediaPlayer != null)
        mediaPlayer.reset();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
