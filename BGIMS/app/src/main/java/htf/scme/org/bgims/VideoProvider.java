package htf.scme.org.bgims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

import htf.scme.org.Activity.VideoActivity;
import htf.scme.org.MessageContent.VideoFileMessage;
import htf.scme.org.MessageContent.VideoMessage;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;

import io.rong.imkit.activity.PictureSelectorActivity;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * Created by Administrator on 2016/6/8.
 */
public class VideoProvider extends InputProvider.ExtendProvider {

  // 文件路径


  public VideoProvider(RongContext context) {

    super(context);
  }

  @Override
  public Drawable obtainPluginDrawable(Context context) {

    return  context.getResources().getDrawable(R.drawable.rc_picsel_empty_pic);
  }

  @Override
  public CharSequence obtainPluginTitle(Context context) {

    return context.getString(R.string.de_add_contacts);
  }

  @Override
  public void onPluginClick(View view) {
    //upload();
/* InputProvider.MainInputProvider provider = RongContext.getInstance().getPrimaryInputProvider();
    TextInputProvider textInputProvider = (TextInputProvider) provider;
    textInputProvider.setEditTextContent("qqqq");*/
//
 Intent intent = new Intent();
    intent.setClass(view.getContext(), VideoActivity.class);
    startActivityForResult(intent,23);
    //sendmessage();

/* RongIM.getInstance().sendMessage(Message.obtain("210430577", Conversation.ConversationType.CHATROOM, TextMessage.obtain("565")), "", "", new RongIMClient.SendMessageCallback() {
     @Override
     public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
       errorCode.getValue();
     }

     @Override
     public void onSuccess(Integer integer) {
       integer.toString();
     }
   }*/




  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case 23:
      if (resultCode == Activity.RESULT_OK) {
          // 成功
          String    path = data.getStringExtra("path");
          Toast.makeText(getContext(),"存储路径为:"+path, Toast.LENGTH_SHORT).show();
          // 通过路径获取第一帧的缩略图并显示
          Bitmap bitmap = Utils.createVideoThumbnail(path);
          String imageFileThumb = CameraImage.bitmapToBase64(bitmap);

            Log.e("file", "f是文件且存在");
        Conversation conversation = getCurrentConversation();
       sendmessage(conversation.getConversationType(),conversation.getTargetId(),imageFileThumb,path);

       } else {
          // 失败
        }
        break;


    }
    super.onActivityResult(requestCode, resultCode, data);


  }


  private void upload(){
    RequestParams params = new RequestParams();
    params.put("video", "65656565656565");
    String url = "http://10.0.2.2:8080/test/UploadServlet";
    AsyncHttpClient client = new AsyncHttpClient();
    client.post(url, params, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int i, Header[] headers, byte[] bytes) {
     Log.d("111",bytes.toString());
      }

      @Override
      public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

      }
    });

}


  private void sendmessage(final Conversation.ConversationType conversationType, final String id, String base64, String uri){

    RongIM.getInstance().sendMessage(conversationType, id, VideoMessage.obtain(base64,uri), null, null, new RongIMClient.SendMessageCallback() {
      @Override
      public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
        errorCode.getValue();
      }

      @Override
      public void onSuccess(Integer integer) {
        integer.toString();
      }
    }, new RongIMClient.ResultCallback<Message>() {
      @Override
      public void onSuccess(Message message) {
        message.getContent();
      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {
        errorCode.getValue();
      }
    });




    /*RongIM.getInstance().sendImageMessage(conversationType, id, VideoMessage.obtain(base64,uri), "", "", new RongIMClient.SendImageMessageCallback() {
      @Override
      public void onAttached(Message message) {
        message.getContent();
      }

      @Override
      public void onError(Message message, RongIMClient.ErrorCode errorCode) {
        errorCode.getValue();
      }

      @Override
      public void onSuccess(Message message) {

        message.getContent();
      }

      @Override
      public void onProgress(Message message, int i) {

        message.getContent();
      }
    });*/
  }

  private void sendvideomessage(final Conversation.ConversationType conversationType, final String id, String base64, String uri){

  }
}
