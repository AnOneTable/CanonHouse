package htf.scme.org.bgims;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import android.os.Environment;


import com.baidu.mapapi.SDKInitializer;


import htf.scme.org.MessageContent.VideoFileMessage;
import htf.scme.org.MessageContent.VideoMessage;
import htf.scme.org.MessageContent.VideoMessageItemProvider;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.imlib.model.Conversation;


public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    SDKInitializer.initialize(getApplicationContext());

    /**
     * 注意：
     *
     * IMKit SDK调用第一步 初始化
     *
     * context上下文
     *
     * 只有两个进程需要初始化，主进程和 push 进程
     */
    if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
      "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
     RongIM.init(this, "8w7jv4qb7hwky");

      RongIM.registerMessageType(VideoMessage.class);
      RongIM.registerMessageTemplate(new VideoMessageItemProvider());
      RongIM.getInstance().getCurrentConnectionStatus();
      settextInputProvider();
      RongIM.getInstance().setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
          connectionStatus.getMessage();
        }
      });
      /**
       * 融云SDK事件监听处理
       *
       * 注册相关代码，只需要在主进程里做。
       */
      if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
        DemoContext.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));
        try {

       /*   RongIM.registerMessageType(AgreedFriendRequestMessage.class);
          //@ 消息模板展示
        */
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }

  public static String getCurProcessName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager activityManager = (ActivityManager) context
      .getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
      .getRunningAppProcesses()) {
      if (appProcess.pid == pid) {
        return appProcess.processName;
      }
    }
    return null;
  }

  /*
   *设置自定义扩展框
   * */
  private void settextInputProvider(){
    TextInputProvider textInputProvider = new TextInputProvider(RongContext.getInstance());
    RongIM.setPrimaryInputProvider(textInputProvider);

    //			        扩展功能自定义
    InputProvider.ExtendProvider[] provider = {
      new ImageInputProvider(RongContext.getInstance()),//图片
      new LocationInputProvider(RongContext.getInstance()),//地理位置
      new VideoProvider(RongContext.getInstance())//自定义视频

    };
    RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
    RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, provider);
  }

}
