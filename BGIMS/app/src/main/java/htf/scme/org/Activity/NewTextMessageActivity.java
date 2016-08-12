package htf.scme.org.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import htf.scme.org.bgims.Constants;
import htf.scme.org.bgims.R;

public class NewTextMessageActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.new_text_message);
   Button b=(Button) findViewById(R.id.button);
    b.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("REPLY_ID","20");
        intent.putExtra("REPLY_NAME","安二个桌");
        setResult(Constants.MESSAGE_REPLY,intent);
        finish();
      }
    });
  }
}
