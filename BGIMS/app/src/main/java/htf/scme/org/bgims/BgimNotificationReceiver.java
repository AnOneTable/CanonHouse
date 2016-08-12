package htf.scme.org.bgims;
import android.content.Context;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

public class BgimNotificationReceiver extends PushMessageReceiver {

  @Override
  public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
    pushNotificationMessage.getPushData();
    pushNotificationMessage.getExtra();
    pushNotificationMessage.getTargetId();
    return false;
  }

  @Override
  public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
   pushNotificationMessage.getPushContent();
    pushNotificationMessage.getPushData();
    return false;

  }


}
