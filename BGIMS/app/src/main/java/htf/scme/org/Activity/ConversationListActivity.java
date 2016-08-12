package htf.scme.org.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import htf.scme.org.ben.UserInfoBen;
import htf.scme.org.bgims.R;
import htf.scme.org.bgims.RefreshLayout;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;


public class ConversationListActivity extends FragmentActivity/* implements RefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener*/ {
    private RefreshLayout srl = null;
    private ArrayList<UserInfoBen> UserInfoList=new ArrayList<>();
    private ListView mListview;
    private int cont;
    private List<Integer> conts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concersaction_list);
      mListview=(ListView) findViewById(R.id.rc_list);
      Button mbuttn=(Button) findViewById(R.id.list_buttn);
      conts = new ArrayList();
      cont = 0;
      try {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
          @Override
          public void onSuccess(List<Conversation> conversations) {

            for (int i=0; i<conversations.size(); i++) {

              if(conversations.get(i).getUnreadMessageCount()!=0){
                conts.add(i);
              }
            }}
          @Override
          public void onError(RongIMClient.ErrorCode errorCode) {
          }
        },new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
          Conversation.ConversationType.GROUP,
          Conversation.ConversationType.SYSTEM});
      }catch (Exception e){
      }

      mbuttn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          for (int i = 0; i < conts.size(); i++) {
            //判断是否是最后一条消息的
            if (i == conts.size()-1) {
              mListview.setSelection(cont);
              cont = conts.get(0);
              //设置listView显示cont这条未读消息，这个设置的放的是你的
              break;
            }
            if (cont == conts.get(i)) {
              mListview.setSelection(cont);
              cont = conts.get(i+1);
              //设置listView显示cont这条未读消息，这个设置的放的是你的

              break;
            }
          }
          mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
              if(mListview.getFirstVisiblePosition() == 0){
                cont = conts.get(0);
              }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
              int lastItem = i + i1;
              if(lastItem == i2) {
                if(conts!=null){
                  cont = conts.get(0);
                }

              }
            }
          });
        }

      });

      /*  srl = (RefreshLayout)
          findViewById(R.id.id_swipe_ly);
        srl.setOnRefreshListener(this);
        srl.setOnLoadListener(this);
        srl.setColorSchemeResources(android.R.color.holo_blue_bright,
          android.R.color.holo_green_light,
          android.R.color.holo_red_light,
          android.R.color.holo_orange_light);*/
        //enterFragment();



      //  RongIM.getInstance().disconnect();
    }
    private void enterFragment() {

        ConversationListFragment fragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.ConversationList);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
          .appendPath("conversationlist")
          .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
          .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
        .build();

      fragment.setUri(uri);
    }
/*0
    @Override
    public void onLoad() {
        srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConversationListActivity.this, "已经到底了", Toast.LENGTH_SHORT).show();
                srl.setLoading(false);
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserInfoList.add(new UserInfoBen("10","我还没改","http://img.jgzyw.com:8000/d/img/touxiang/2015/12/14/2015121410523218600.jpeg"));
                UserInfoList.add(new UserInfoBen("20","我改名了","http://p2.wmpic.me/article/2014/11/27/1417067254_zsUTqfiY.jpg"));
                for (UserInfoBen userinfo : UserInfoList) {
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(userinfo.UserId,userinfo.getUserName(), Uri.parse(userinfo.getUserImageUrl())));
                }
                srl.setRefreshing(false);
            }
        }, 1000);
    }*/
  private void forlistview() {
    int shuzu[] = {0, 5, 9};
    for (int b : shuzu) {
      mListview.setSelection(b);
      if(b==9){
        forlistview();
      }
    }
  }
}
