package htf.scme.org.MessageContent;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2016/7/5.
 */

@MessageTag(value = "app:BGvideoMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class VideoFileMessage extends MessageContent {
  private Uri mThumUri;
  private Uri mLocalUri;
  private Uri mRemoteUri;
  private boolean mUpLoadExp = false;
  private String mBase64;
  boolean mIsFull;
  protected String extra;

  /**
   * 获取消息附加信息
   *
   * @return 附加信息
   */
  public String getExtra() {
    return extra;
  }

  /**
   * 设置消息附加信息
   *
   * @param extra 附加信息
   */
  public void setExtra(String extra) {
    this.extra = extra;
  }

  public VideoFileMessage(byte[] data) {
    String jsonStr = new String(data);

    try {
      JSONObject jsonObj = new JSONObject(jsonStr);

      if (jsonObj.has("imageUri")) {
        String uri = jsonObj.optString("imageUri");
        if (!TextUtils.isEmpty(uri))
          setRemoteUri(Uri.parse(uri));
        if (getRemoteUri() != null && getRemoteUri().getScheme() != null && getRemoteUri().getScheme().equals("file")) {
          setLocalUri(getRemoteUri());
        }
      }

      if (jsonObj.has("content")) {
        setBase64(jsonObj.optString("content"));
      }
      if (jsonObj.has("extra")) {
        setExtra(jsonObj.optString("extra"));
      }
      if (jsonObj.has("exp")) {
        setUpLoadExp(true);
      }
      if (jsonObj.has("isFull")) {
        setIsFull(jsonObj.optBoolean("isFull"));
      }
      if (jsonObj.has("user")) {
        setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
      }
    } catch (JSONException e) {
      Log.e("JSONException", e.getMessage());
    }
  }

  public VideoFileMessage() {

  }

  private VideoFileMessage(Uri thumbUri, Uri localUri) {
    mThumUri = thumbUri;
    mLocalUri = localUri;
  }

  private VideoFileMessage(Uri thumbUri, Uri localUri, boolean original) {
    mThumUri = thumbUri;
    mLocalUri = localUri;
    mIsFull = original;
  }

  /**
   * 生成VideoFileMessage对象。
   *
   * @param thumUri  缩略图地址。
   * @param localUri 大图地址。
   * @return VideoFileMessage对象实例。
   */
  public static VideoFileMessage obtain(Uri thumUri, Uri localUri) {
    return new VideoFileMessage(thumUri, localUri);
  }

  /**
   * 生成VideoFileMessage对象。
   *
   * @param thumUri  缩略图地址。
   * @param localUri 大图地址。
   * @param isFull 是否发送原图。
   * @return VideoFileMessage对象实例。
   */
  public static VideoFileMessage obtain(Uri thumUri, Uri localUri, boolean isFull) {
    return new VideoFileMessage(thumUri, localUri, isFull);
  }

  /**
   * 生成VideoFileMessage对象。
   *
   * @return VideoFileMessage对象实例。
   */
  public static VideoFileMessage obtain() {
    return new VideoFileMessage();
  }

  /**
   * 获取缩略图Uri。
   *
   * @return 缩略图Uri（收消息情况下此为内部Uri，需要通过ResourceManager.getInstance().getFile(new Resource(Uri))方式才能获取到真实地址）。
   */
  public Uri getThumUri() {
    return mThumUri;
  }

  /**
   * 是否是原图。
   *
   * @return true / false
   */
  public boolean isFull() {
    return mIsFull;
  }

  /**
   * 设置发送原图标志位。
   *
   * @param isFull 是否原图。
   */
  public void setIsFull(boolean isFull) {
    this.mIsFull = isFull;
  }

  /**
   * 设置缩略图Uri。
   *
   * @param thumUri 缩略图地址
   */
  public void setThumUri(Uri thumUri) {
    this.mThumUri = thumUri;
  }

  /**
   * 获取本地图片地址（file:///）。
   *
   * @return 本地图片地址（file:///）。
   */
  public Uri getLocalUri() {
    return mLocalUri;
  }

  /**
   * 设置本地图片地址（file:///）。
   *
   * @param localUri 本地图片地址（file:///）.
   */
  public void setLocalUri(Uri localUri) {
    this.mLocalUri = localUri;
  }

  /**
   * 获取网络图片地址（http://）。
   *
   * @return 网络图片地址（http://）。
   */
  public Uri getRemoteUri() {
    return mRemoteUri;
  }

  /**
   * 设置网络图片地址（http://）。
   *
   * @param remoteUri 网络图片地址（http://）。
   */
  public void setRemoteUri(Uri remoteUri) {
    this.mRemoteUri = remoteUri;
  }

  /**
   * 设置需要传递的Base64数据
   *
   * @param base64 base64数据。
   */
  public void setBase64(String base64) {
    mBase64 = base64;
  }

  /**
   * 获取需要传递的Base64数据。
   *
   * @return base64数据。
   */
  public String getBase64() {
    return mBase64;
  }

  /**
   * 是否上传失败。
   *
   * @return 是否上传失败。
   */
  public boolean isUpLoadExp() {
    return mUpLoadExp;
  }

  /**
   * 设置是否上传失败。
   *
   * @param upLoadExp 上传是否失败。
   */
  public void setUpLoadExp(boolean upLoadExp) {
    this.mUpLoadExp = upLoadExp;
  }

  @Override
  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();

    try {
      if (!TextUtils.isEmpty(mBase64)) {
        jsonObj.put("content", mBase64);
      } else {
        Log.d("VideoFileMessage", "base64 is null");
      }

      if (mRemoteUri != null) {
        jsonObj.put("imageUri", mRemoteUri.toString());
      } else if (getLocalUri() != null) {
        jsonObj.put("imageUri", getLocalUri().toString());
      }

      if (mUpLoadExp) {
        jsonObj.put("exp", true);
      }
      jsonObj.put("isFull", mIsFull);
      if (!TextUtils.isEmpty(getExtra()))
        jsonObj.put("extra", getExtra());
      if (getJSONUserInfo() != null)
        jsonObj.putOpt("user", getJSONUserInfo());
    } catch (JSONException e) {
      Log.e("JSONException", e.getMessage());
    }
    mBase64 = null;
    return jsonObj.toString().getBytes();
  }

  /**
   * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
   *
   * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
   */
  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * 构造函数。
   *
   * @param in 初始化传入的 Parcel。
   */
  public VideoFileMessage(Parcel in) {
    setExtra(ParcelUtils.readFromParcel(in));
    mLocalUri = ParcelUtils.readFromParcel(in, Uri.class);
    mRemoteUri = ParcelUtils.readFromParcel(in, Uri.class);
    mThumUri = ParcelUtils.readFromParcel(in, Uri.class);
    setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    mIsFull = ParcelUtils.readIntFromParcel(in) == 1;
  }

  /**
   * 将类的数据写入外部提供的 Parcel 中。
   *
   * @param dest  对象被写入的 Parcel。
   * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    ParcelUtils.writeToParcel(dest, getExtra());
    ParcelUtils.writeToParcel(dest, mLocalUri);
    ParcelUtils.writeToParcel(dest, mRemoteUri);
    ParcelUtils.writeToParcel(dest, mThumUri);
    ParcelUtils.writeToParcel(dest, getUserInfo());
    ParcelUtils.writeToParcel(dest, mIsFull ? 1 : 0);
  }

  /**
   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
   */
  public static final Creator<VideoFileMessage> CREATOR = new Creator<VideoFileMessage>() {

    @Override
    public VideoFileMessage createFromParcel(Parcel source) {
      return new VideoFileMessage(source);
    }

    @Override
    public VideoFileMessage[] newArray(int size) {
      return new VideoFileMessage[size];
    }
  };
}
