package yangxixi.processsuccessviewdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import yangxixi.processsuccessviewdemo.ui.views.ProcessSuccessView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textview = (TextView) findViewById(R.id.textview);
        final ProcessSuccessView processview = (ProcessSuccessView) findViewById(R.id.processview);
        processview.setListener(new ProcessSuccessView.IAnimotionListener() {
            @Override
            public void onAnimotionFinished() {
                textview.setVisibility(View.VISIBLE);
            }
        });
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview.setVisibility(View.GONE);
                processview.starRun();
            }
        });
    }

}
