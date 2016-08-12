package htf.scme.org.Activity;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import htf.scme.org.bgims.R;

/**
 * Created by Administrator on 2016/6/29.
 */
public class VideoFragment extends Fragment {

  private static final String VIDEO_PATH = "video_path";

  private String videoPath;

  private VideoView mVideoView;

  private Button btnClose;

  public static VideoFragment newInstance(String videoPath) {
    VideoFragment fragment = new VideoFragment();
    Bundle args = new Bundle();
    args.putString(VIDEO_PATH, videoPath);
    fragment.setArguments(args);
    return fragment;
  }

  public VideoFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      videoPath = getArguments().getString(VIDEO_PATH);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_video, container, false);
    mVideoView = (VideoView) view.findViewById(R.id.video_view);
    btnClose = (Button) view.findViewById(R.id.btn_close);
    // 播放相应的视频
    mVideoView.setMediaController(new MediaController(getActivity()));
    mVideoView.setVideoURI(Uri.parse(videoPath));
    mVideoView.start();
    //mVideoView.requestFocus();

    btnClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getFragmentManager().beginTransaction().remove(VideoFragment.this).commit();
      }
    });
    return view;
  }
}
