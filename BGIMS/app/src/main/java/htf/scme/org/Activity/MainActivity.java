package htf.scme.org.Activity;

import android.app.Activity;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import htf.scme.org.ben.UserInfoBen;
import htf.scme.org.bgims.R;

import htf.scme.org.bgims.VideoProvider;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;

import io.rong.imlib.model.MessageContent;

import io.rong.imlib.model.UserInfo;

import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

public class MainActivity extends Activity implements View.OnClickListener {
  private ArrayList<UserInfoBen> UserInfoList = new ArrayList<>();
 private Map<String, Boolean>  map=new HashMap<>();

  private static   int oldid;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initview();

  }

  private void initview() {
    Button connect = (Button) findViewById(R.id.connect);
    Button connect2 = (Button) findViewById(R.id.connect2);
    Button startconverslist = (Button) findViewById(R.id.startconverslist);
    Button startdanliao = (Button) findViewById(R.id.startdanliao);
    Button startqunliao = (Button) findViewById(R.id.startqunliao);
    Button starttaolunzu = (Button) findViewById(R.id.starttaolunzu);
    Button startkefu = (Button) findViewById(R.id.startkefu);
    Button startliaotianshi = (Button) findViewById(R.id.startliaotianshi);
    Button startgongzhonghao = (Button) findViewById(R.id.startgongzhonghao);
    startgongzhonghao.setOnClickListener(this);
    connect.setOnClickListener(this);
    connect2.setOnClickListener(this);
    startconverslist.setOnClickListener(this);
    startdanliao.setOnClickListener(this);
    startqunliao.setOnClickListener(this);
    starttaolunzu.setOnClickListener(this);
    startkefu.setOnClickListener(this);
    startliaotianshi.setOnClickListener(this);
    getmessage();
    UserInfoList.add(new UserInfoBen("40", "我是40", "http://img.jgzyw.com:8000/d/img/touxiang/2015/12/13/2015121321313329334.jpeg"));
    UserInfoList.add(new UserInfoBen("10", "大家都停下听我说", "http://img4.duitang.com/uploads/item/201511/24/20151124110338_JFU2d.jpeg"));
    UserInfoList.add(new UserInfoBen("20", "你们都是", "http://p3.wmpic.me/article/2015/03/18/1426649933_mafqzZLi.jpeg"));
    RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
      @Override
      public UserInfo getUserInfo(String s) {
        for (UserInfoBen userinfo : UserInfoList) {
          if (userinfo.getUserId().equals(s)) {
            return new UserInfo(userinfo.getUserId(), userinfo.getUserName(), Uri.parse(userinfo.getUserImageUrl()));
          }
        }
        return null;
      }
    }, true);

    RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {
      @Override
      public Group getGroupInfo(String s) {
        return new Group("10002", "好声一片", Uri.parse("http://img3.duitang.com/uploads/item/201504/23/20150423H5551_k3JnC.jpeg"));
      }
    }, true);

  /* RongIM.getInstance().joinChatRoom("12530", 55,new RongIMClient.OperationCallback() {
     @Override
     public void onSuccess() {

     }

     @Override
     public void onError(RongIMClient.ErrorCode errorCode) {

     }
   });*/
  }

  String type;

  @Override
  public void onClick(View arg0) {
    // TODO Auto-generated method stub
    ArrayList<String> userIds = new ArrayList<>();
    userIds.add("10");//增加 userId。
    userIds.add("20");
    userIds.add("30");//增加 userId。
    if (arg0 == findViewById(R.id.connect)) {
     //生产环境 CorrectRing("0MYd71scq/cBCoztfr5e0MnqI8HFqCf5nFvQI2bNn9rHXw1fO8o57fRErYmzCwUNNJA3b4zjrSY=");
    CorrectRing("S/hy1urlxALW9kr84G7m5UPb0LcOVI9w8QLSwQUP4N/ebqSuq9/zZf6GbiKuexLkfTBNaxSR4S0=");//开发环境

        type = "20";
    }
    if (arg0 == findViewById(R.id.connect2)) {
      type = "10";
    //  CorrectRing("0Wt0E48xMImahRvT294mvyaQmOGG9xGFY3n/SyO+7YPIG7/tpYi4pjClRcyuK87CW2znj7TXXZXKA5ArL0w7rA==");
      CorrectRing("5hZvxnEKDCcCBNd1iirDfXpQt7Gb2hlvLgjvlhZWLqu1cL6ZQR4grhsF/pDY06fYKoYHrNzX+Dnv1oZl4l+RAw==");
    }
    if (arg0 == findViewById(R.id.startdanliao)) {
      setStatus();
      RongIM.getInstance().startConversation(MainActivity.this, Conversation.ConversationType.PRIVATE, type, "融融");

    }
    if (arg0 == findViewById(R.id.startliaotianshi)) {
      insertmessage();



    }
    if (arg0 == findViewById(R.id.startqunliao)) {
      RongIM.getInstance().startConversation(MainActivity.this, Conversation.ConversationType.GROUP, "10002", "融融");
    }
    if (arg0 == findViewById(R.id.startconverslist)) {
      map.put(Conversation.ConversationType.PRIVATE.getName(), false);
      map.put(Conversation.ConversationType.GROUP.getName(), false);
      map.put(Conversation.ConversationType.DISCUSSION.getName(), false);
      map.put(Conversation.ConversationType.SYSTEM.getName(), false);
      RongIM.getInstance().startConversationList(MainActivity.this, map);
    }
    if (arg0 == findViewById(R.id.startgongzhonghao)) {

      RongIM.getInstance().startPublicServiceProfile(MainActivity.this, Conversation.ConversationType.PUBLIC_SERVICE, "54321");
    }
    CSCustomServiceInfo.Builder csBuilder = new CSCustomServiceInfo.Builder();

    CSCustomServiceInfo csInfo = csBuilder
      .nickName("哈哈哈哈哈")
      .build();
    if (arg0 == findViewById(R.id.startkefu)) {
      RongIM.getInstance().startCustomerServiceChat(MainActivity.this, "KEFU146008961867241", "在线客服", csInfo);
    }
      if(arg0==findViewById(R.id.starttaolunzu)){
        RongIM.getInstance().createDiscussion("讨论组", userIds, new RongIMClient.CreateDiscussionCallback() {
          @Override
          public void onSuccess(String s) {
            RongIM.getInstance().startConversation(MainActivity.this, Conversation.ConversationType.DISCUSSION,s,"讨论组");
          }

          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {

          }
        });

        }


/*
    RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, "", new RongIMClient.ResultCallback<Boolean>() {
      @Override
      public void onSuccess(Boolean aBoolean) {

      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {

      }
    });*/

  }

  /**
   * 连接融云服务器
   */

  private void CorrectRing(String toenk) {

    RongIM.connect(toenk, new RongIMClient.ConnectCallback() {

      @Override
      public void onTokenIncorrect() {
        // TODO Auto-generated method stub

      }

      @Override
      public void onError(RongIMClient.ErrorCode arg0) {
        // TODO Auto-generated method stub

        Toast.makeText(MainActivity.this, "连接失败" + arg0, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onSuccess(String arg0) {
        // TODO Auto-generated method stub
        Log.d("连接成功", arg0 + "");
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒

        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
 /*   RongIM.getInstance().clearConversations(new RongIMClient.ResultCallback() {
      @Override
      public void onSuccess(Object o) {
        o.toString();
      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {
        errorCode.getValue();
      }
    },Conversation.ConversationType.PRIVATE);*/

    /*  RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.PRIVATE, "20", 1, new RongIMClient.ResultCallback<List<Message>>() {
          @Override
          public void onSuccess(List<Message> messages) {
            for (Message m:messages){
             oldid=   m.getMessageId();
            }
          }
          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {

          }
        });
        RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE, "20", 2, 50, new RongIMClient.ResultCallback<List<Message>>() {
          @Override
          public void onSuccess(List<Message> messages) {
            for(Message messages1:messages){
              messages1.getContent();
            }
          }

          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {

          }
        });*/
        //insertmessage();
     /*   RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new RongIM.OnReceiveUnreadCountChangedListener() {
          @Override
          public void onMessageIncreased(int i) {

          }
        }, new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
          Conversation.ConversationType.GROUP,
          Conversation.ConversationType.SYSTEM});

        RongIMClient.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
          @Override
          public void onChanged(ConnectionStatus connectionStatus) {
            Log.d("22222222", connectionStatus + "");
          }
        });*/
    /*RongIM.getInstance().subscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, "54321", new RongIMClient.OperationCallback() {
          @Override
          public void onSuccess() {
            Log.d("22222222",   "关注成功");
          }

          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {

          }
        });*/

     /*  RongIM.getInstance().getPublicServiceList(new RongIMClient.ResultCallback<PublicServiceProfileList>() {
          @Override
          public void onSuccess(PublicServiceProfileList publicServiceProfileList) {
            ArrayList<PublicServiceProfile> list=     publicServiceProfileList.getPublicServiceData();
            for(PublicServiceProfile PublicServiceProfile:list){
             String id= PublicServiceProfile.getTargetId();
             String name = PublicServiceProfile.getName();
            }

          }

          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {
             String S=errorCode.getMessage();

          }
        });*/
       /* RongIM.getInstance().getPublicServiceProfile(Conversation.PublicServiceType.PUBLIC_SERVICE, "54321", new RongIMClient.ResultCallback<PublicServiceProfile>() {
          @Override
          public void onSuccess(PublicServiceProfile publicServiceProfile) {
            String id= publicServiceProfile.getTargetId();
            String name = publicServiceProfile.getName();
          }

          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {
            String S=errorCode.getMessage();
          }
        });*/
             /* RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversations) {
                        for (Conversation message : conversations) {
                            MessageContent m= message.getLatestMessage();
                            if(m instanceof TextMessage) {
                                TextMessage textMessage=(TextMessage) m;//文本消息
                                String meg=textMessage.getContent();
                            }
                        }}
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }

                },new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                  Conversation.ConversationType.GROUP,
                  Conversation.ConversationType.SYSTEM});*/
        //  startmessage("哈哈","20");
        Toast.makeText(MainActivity.this, "连接成功" + arg0, Toast.LENGTH_SHORT).show();

			/*	 */
			 /*     ArrayList<String> userIds = new ArrayList<String>();
			      userIds.add("10");//增加 userId。
			      userIds.add("20");//增加 userId。
			      userIds.add("30");//增加 userId。

			       *//**
         * 添加一名或者一组用户加入讨论组。
         *
         * @param discussionId 讨论组 Id。
         * @param userIdList   邀请的用户 Id 列表。
         * @param callback     执行操作的回调。
         *//*
			      RongIM.getInstance().getRongIMClient().addMemberToDiscussion("9527", userIds, new RongIMClient.OperationCallback() {

			          @Override
			          public void onSuccess() {

			          }

			          @Override
			          public void onError(RongIMClient.ErrorCode errorCode) {

			          }
			      });*/

      }


    });

  }


  public void startmessage(String mesage, String UserId) {

   /* File imageFileSource = new File(getCacheDir(), "source.jpg");原图路径
    File imageFileThumb = new File(getCacheDir(), "thumb.jpg");缩略图路径

    ImageMessage imageMessage = ImageMessage.obtain(Uri.fromFile(imageFileThumb), Uri.fromFile(imageFileSource));

    Message message=Message.obtain("id", Conversation.ConversationType.PRIVATE,imageMessage);


    RongIM.getInstance().sendImageMessage(message,"" , "", new RongIMClient.SendImageMessageWithUploadListenerCallback() {

      @Override
      public void onAttached(Message message, RongIMClient.uploadImageStatusListener uploadImageStatusListener) {
      在这个回调里面自己上传图片，把上传的的进度传给下面的这个方法。

        uploadImageStatusListener.update(上传图片的进度);
        如果上传失败就调用error
        uploadImageStatusListener.error();
        上传成功就调用success
        uploadImageStatusListener.success();
      }

      @Override
      public void onError(Message message, RongIMClient.ErrorCode errorCode) {

      }

      @Override
      public void onSuccess(Message message) {

      }

      @Override
      public void onProgress(Message message, int i) {

      }
    });*/


    RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, UserId, TextMessage.obtain(mesage), "xiaoxi", "xiaoxi",
      new RongIMClient.SendMessageCallback() {

        @Override
        public void onSuccess(Integer arg0) {
          // TODO Auto-generated method stub


          Log.d("sendmessage1", arg0 + "");
        }

        @Override
        public void onError(Integer arg0, RongIMClient.ErrorCode arg1) {
          // TODO Auto-generated method stub
          Log.d("sendmessage1", arg0 + "");
        }

      }, new RongIMClient.ResultCallback<Message>() {

        @Override
        public void onSuccess(Message arg0) {
          // TODO Auto-generated method stub


        }

        @Override
        public void onError(RongIMClient.ErrorCode arg0) {
          // TODO Auto-generated method stub

        }
      }
    );


  }

  private  void insertmessage(){

      RongIM.getInstance().insertMessage(Conversation.ConversationType.CHATROOM, "12530", "10", InformationNotificationMessage.obtain("我已经加入聊天室"), new RongIMClient.ResultCallback<Message>() {
      @Override
      public void onSuccess(Message message) {
        RongIM.getInstance().startConversation(MainActivity.this, Conversation.ConversationType.CHATROOM, "12530", "大好聊天室");

      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {

      }
    });
  }

  public void getmessage(){

    RongIM.getInstance().setRequestPermissionListener(new RongIM.RequestPermissionsListener() {
      @Override
      public void onPermissionRequest(String[] strings, int i) {

      }
    });
    RongIM.getInstance().setMessageSentStatus(2, Message.SentStatus.SENT, new RongIMClient.ResultCallback<Boolean>() {
      @Override
      public void onSuccess(Boolean aBoolean) {

      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {

      }
    });
   RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
      @Override
      public boolean onReceived(Message message, int i) {
        message.getSenderUserId();
        MessageContent m= message.getContent();
        message.getObjectName();
        message.getSentStatus();
        if(m instanceof TextMessage) {
          TextMessage textMessage=(TextMessage) m;//文本消息
          String meg=textMessage.getContent();
          textMessage.getExtra();
        }
        return false;
      }
    });

/*
      RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, "", true, new RongIMClient.ResultCallback<Boolean>() {
        @Override
        public void onSuccess(Boolean aBoolean) {

        }

        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {

        }
      });*/


  }



  private void setStatus(){
    RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.PRIVATE, "20", Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
      @Override
      public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
        conversationNotificationStatus.toString();
      }

      @Override
      public void onError(RongIMClient.ErrorCode errorCode) {

      }
    });
  }

}
