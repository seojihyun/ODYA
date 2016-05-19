package seojihyun.odya.pineapple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.nearby.messages.Message;

public class SOSActivity extends AppCompatActivity {
    MessageDialog messageDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sos);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        String message = intent.getExtras().getString("message");
        messageDialog = new MessageDialog(this, message, new ClickListener());
        messageDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageDialog.dismiss();
    }
    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
