package htf.scme.org.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import htf.scme.org.bgims.R;
import io.rong.imkit.tools.RongWebviewActivity;

public class Main2Activity extends RongWebviewActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);


   TextView t=(TextView) findViewById(R.id.videtext);
    t.setText(getIntent().getStringExtra("message"));
  }

}
