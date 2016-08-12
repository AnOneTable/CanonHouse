package htf.scme.org.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

import htf.scme.org.MessageContent.VideoMessage;
import htf.scme.org.bgims.Constants;
import htf.scme.org.bgims.R;
import htf.scme.org.bgims.Tool;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.push.RongPushClient;


public class ConversationActivity extends AppCompatActivity implements RongIM.LocationProvider, RongIM.ConversationBehaviorListener {
  /**
   * 会话类型
   */
  private Conversation.ConversationType mConversationType;
  /**
   * 获得群组的targetIds
   */
  private String mTargetIds;

  /**
   * 对方id
   */
  private String mTargetId;
  private TextView toolbar_edit_fanhui, toolbar_tetle;
  private RelativeLayout btn_fanhui;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_concersaction);
    initview();


  /*  RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
      @Override
      public boolean onReceived(Message message, int i) {
        String s = message.getContent().toString();
        return false;
      }
    });*/
  }

  private void initview() {

    String s = getIntent().getData().getQueryParameter("targetId");
    toolbar_edit_fanhui = (TextView) findViewById(R.id.toolbar_edit_fanhui);
    btn_fanhui = (RelativeLayout) findViewById(R.id.btn_fanhui);
    toolbar_tetle = (TextView) findViewById(R.id.toolbar_tetle);

    toolbar_edit_fanhui.setText("返回");
    toolbar_tetle.setText(s);
    btn_fanhui.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    RongIM.setLocationProvider(this);
    RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
    Intent intent = getIntent();

    if (intent == null || intent.getData() == null)
      return;
    //intent.getData().getLastPathSegment();//获得当前会话类型
    mConversationType = Conversation.ConversationType.valueOf(intent.getData()
      .getLastPathSegment().toUpperCase(Locale.getDefault()));
    //群组 @ 消息
    groupTextInputEditTextChanged();


   /* RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
      @Override
      public Message onSend(Message message) {

        return message;
      }

      @Override
      public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        sentMessageErrorCode.getValue();
        return false;
      }
    });*/
  }

  /**
   * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
   *
   * @param context  上下文
   * @param callback 回调
   */
  @Override
  public void onStartLocation(Context context, LocationCallback callback) {
    /**
     * demo 代码  开发者需替换成自己的代码。
     */
    Tool.mLastLocationCallback = callback;
    Intent intent = new Intent(context, MapLocationActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);


  }

  @Override
  public boolean onMessageClick(Context arg0, View arg1, Message arg2) {
    // TODO Auto-generated method stub
    if (arg2.getContent() instanceof LocationMessage) {
      Intent intent = new Intent(ConversationActivity.this, MapLocationActivity.class);
      intent.putExtra("location", arg2.getContent());
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
    else if (arg2.getContent() instanceof VideoMessage) {
      Intent intent = new Intent(ConversationActivity.this, Main3Activity.class);
      intent.putExtra("message", arg2);
      startActivity(intent);
    }
    return false;
  }

  @Override
  public boolean onMessageLinkClick(Context arg0, String arg1) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onMessageLongClick(Context arg0, View arg1, Message arg2) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onUserPortraitClick(Context arg0, Conversation.ConversationType arg1,
                                     UserInfo arg2) {
    Log.d("BG","头像点击事件");
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onUserPortraitLongClick(Context arg0, Conversation.ConversationType arg1,
                                         UserInfo arg2) {
    Log.d("BG","头像长按事件");
    // TODO Auto-generated method stub
    return true;
  }

  private String mEditText;

  private void groupTextInputEditTextChanged() {
    InputProvider.MainInputProvider provider = RongContext.getInstance().getPrimaryInputProvider();
    if (provider instanceof TextInputProvider) {
      TextInputProvider textInputProvider = (TextInputProvider) provider;
      textInputProvider.setEditTextChangedListener(new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

          if (mConversationType.equals(Conversation.ConversationType.GROUP)) {

            if (s.length() > 0) {
              String str = s.toString().substring(s.toString().length() - 1, s.toString().length());

              if (str.equals("@")) {

                Intent intent = new Intent(ConversationActivity.this, NewTextMessageActivity.class);
                intent.putExtra("DEMO_REPLY_CONVERSATIONTYPE", mConversationType.toString());

                if (mTargetIds != null) {
                  UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);

                  mTargetId = fragment.getUri().getQueryParameter("targetId");
                }
                intent.putExtra("DEMO_REPLY_TARGETID", mTargetId);
                startActivityForResult(intent, 29);

                mEditText = s.toString();
              }
            }
          }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
      });
    }

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 29 && resultCode == Constants.MESSAGE_REPLY) {
      if (data != null && data.hasExtra("REPLY_NAME") && data.hasExtra("REPLY_ID")) {
        String id = data.getStringExtra("REPLY_ID");
        String name = data.getStringExtra("REPLY_NAME");
        TextInputProvider textInputProvider = (TextInputProvider) RongContext.getInstance().getPrimaryInputProvider();
        textInputProvider.setEditTextContent(mEditText + name + " ");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }


  }


}

