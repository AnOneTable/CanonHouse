package htf.scme.org.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import htf.scme.org.bgims.R;
import htf.scme.org.bgims.Utils;

public class Main3Activity extends AppCompatActivity {

  // 播放按钮
  private ImageButton btnPlay;
  // 文件路径
  private String path = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main3);
/*

    btnPlay = (ImageButton) findViewById(R.id.play);
*/

      Intent in=getIntent();
    path = in.getStringExtra("path");
/*    Toast.makeText(Main3Activity.this,"存储路径为:"+path,Toast.LENGTH_SHORT).show();
    // 通过路径获取第一帧的缩略图并显示
    Bitmap bitmap = Utils.createVideoThumbnail(path);
    BitmapDrawable drawable = new BitmapDrawable(bitmap);
    drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT);
    drawable.setDither(true);
    btnPlay.setBackgroundDrawable(drawable);*/

    // 显示播放页面
    VideoFragment bigPic = VideoFragment.newInstance(path);
    android.app.FragmentManager mFragmentManager = getFragmentManager();
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(R.id.main_menu, bigPic);
    transaction.commit();

   /* btnPlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });*/
  }
}
