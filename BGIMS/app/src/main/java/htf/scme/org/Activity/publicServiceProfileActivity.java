package htf.scme.org.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import htf.scme.org.bgims.R;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.PublicServiceProfile;

public class publicServiceProfileActivity extends AppCompatActivity implements RongIM.PublicServiceBehaviorListener{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_public_service_profile);
    RongIM.setPublicServiceBehaviorListener(this);
  }

  @Override
  public boolean onFollowClick(Context context, PublicServiceProfile publicServiceProfile) {
    return true;
  }

  @Override
  public boolean onUnFollowClick(Context context, PublicServiceProfile publicServiceProfile) {
    return true;
  }

  @Override
  public boolean onEnterConversationClick(Context context, PublicServiceProfile publicServiceProfile) {


    return true;
  }
}
