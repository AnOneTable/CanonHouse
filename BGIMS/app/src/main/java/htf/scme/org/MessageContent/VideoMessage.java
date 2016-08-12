package htf.scme.org.MessageContent;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2016/6/30.
 */
@MessageTag(value = "app:BGvideoMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class VideoMessage extends MessageContent {

  private String mThumUri;
  private String mLocalUri;
  public VideoMessage() {

  }
  public static VideoMessage obtain(String mThumUri, String mLocalUri) {
    VideoMessage videoMessage = new VideoMessage();
    videoMessage.setmLocalUri(mLocalUri);
    videoMessage.setmThumUri(mThumUri);
    return videoMessage;
  }

  // 给消息赋值。
  public VideoMessage(byte[] data) {

    try {
      String jsonStr = new String(data, "UTF-8");
      JSONObject jsonObj = new JSONObject(jsonStr);
      setmThumUri(jsonObj.getString("mThumUri"));
      setmLocalUri(jsonObj.getString("mLocalUri"));
      if (jsonObj.has("user")) {
        setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
      }
    } catch (JSONException e) {
      Log.e("JSONException", e.getMessage());
    } catch (UnsupportedEncodingException e1) {

    }
  }

  /**
   * 构造函数。
   *
   * @param in 初始化传入的 Parcel。
   */
  public VideoMessage(Parcel in) {
    setmThumUri(ParcelUtils.readFromParcel(in));
    setmLocalUri(ParcelUtils.readFromParcel(in));
    setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
  }

  /**
   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
   */
  public static final Creator<VideoMessage> CREATOR = new Creator<VideoMessage>() {

    @Override
    public VideoMessage createFromParcel(Parcel source) {
      return new VideoMessage(source);
    }

    @Override
    public VideoMessage[] newArray(int size) {
      return new VideoMessage[size];
    }
  };

  /**
   * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
   *
   * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
   */
  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * 将类的数据写入外部提供的 Parcel 中。
   *
   * @param dest  对象被写入的 Parcel。
   * @param flags 对象如何被写入的附加标志。
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    // 这里可继续增加你消息的属性
    ParcelUtils.writeToParcel(dest, mThumUri);
    ParcelUtils.writeToParcel(dest, mLocalUri);
    ParcelUtils.writeToParcel(dest, getUserInfo());

  }

  /**
   * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
   */
  @Override
  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();
    try {

      jsonObj.put("mThumUri", mThumUri);
      jsonObj.put("mLocalUri", mLocalUri);

      if (getJSONUserInfo() != null)
        jsonObj.putOpt("user", getJSONUserInfo());

    } catch (JSONException e) {
      Log.e("JSONException", e.getMessage());
    }

    try {
      return jsonObj.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public String getmThumUri() {
    return mThumUri;
  }

  public String getmLocalUri() {
    return mLocalUri;
  }

  public void setmThumUri(String mThumUri) {
    this.mThumUri = mThumUri;
  }

  public void setmLocalUri(String mLocalUri) {
    this.mLocalUri = mLocalUri;
  }
}
