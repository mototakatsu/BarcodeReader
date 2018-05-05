package jp.mototakatsu.barcodereader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private static final String KEY_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String scanResult = getIntent().getStringExtra(KEY_RESULT);
        TextView textValue = findViewById(R.id.textValue);
        textValue.setText(scanResult);
    }

    public static Intent createIntent(Context context, String scanResult) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(KEY_RESULT, scanResult);
        return intent;
    }
}
