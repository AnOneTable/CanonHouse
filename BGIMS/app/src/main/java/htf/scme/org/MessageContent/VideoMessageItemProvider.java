package htf.scme.org.MessageContent;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import htf.scme.org.Activity.Main3Activity;
import htf.scme.org.bgims.CameraImage;
import htf.scme.org.bgims.R;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;


/**
 * Created by Administrator on 2016/6/30.
 */
@ProviderTag(messageContent = VideoMessage.class, showProgress = false)
public class VideoMessageItemProvider extends IContainerItemProvider.MessageProvider<VideoMessage> {

  class ViewHolder {
    ImageButton messagePlay;

  }
  @Override
  public void bindView(View view, int i, final VideoMessage videoMessage, UIMessage uiMessage) {
    ViewHolder holder = (ViewHolder) view.getTag();

    Bitmap bitmap=CameraImage.base64ToBitmap(videoMessage.getmThumUri());
    BitmapDrawable drawable = new BitmapDrawable(bitmap);
    holder.messagePlay.setBackgroundDrawable(drawable);
    // 更改气泡样式
    if (uiMessage.getMessageDirection() == UIMessage.MessageDirection.SEND) {
      // 消息方向，自己发送的
      view.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
    } else {
      // 消息方向，别人发送的
      view.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
    }
    holder.messagePlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), Main3Activity.class);
        intent.putExtra("path",videoMessage.getmLocalUri());
        view.getContext().startActivity(intent);
      }
    });
  }

  @Override
  public Spannable getContentSummary(VideoMessage videoMessage) {
    return new SpannableString("[视频]");
  }

  @Override
  public void onItemClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {

  /*  Intent intent = new Intent(view.getContext(), Main3Activity.class);
    intent.putExtra("path",videoMessage.getmLocalUri());
    view.getContext().startActivity(intent);*/
  }

  @Override
  public void onItemLongClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {

  }

  @Override
  public View newView(Context context, ViewGroup viewGroup) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_video, null);
    ViewHolder holder = new ViewHolder();
    holder.messagePlay = (ImageButton) view.findViewById(R.id.message_play);
    //holder.item_video_image = (RelativeLayout) view.findViewById(R.id.item_video_image);
    view.setTag(holder);
    return view;
  }

}
