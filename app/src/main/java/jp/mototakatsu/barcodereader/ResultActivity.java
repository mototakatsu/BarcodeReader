package jp.mototakatsu.barcodereader;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private static final String KEY_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final String scanResult = getIntent().getStringExtra(KEY_RESULT);
        TextView textValue = findViewById(R.id.textValue);
        textValue.setText(scanResult);

        findViewById(R.id.buttonCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData.Item item = new ClipData.Item(scanResult);
                String[] mimeType = new String[1];
                mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
                ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(cd);
                Toast.makeText(ResultActivity.this, R.string.copied_it, Toast.LENGTH_SHORT).show();
                view.setEnabled(false);
            }
        });
    }

    public static Intent createIntent(Context context, String scanResult) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(KEY_RESULT, scanResult);
        return intent;
    }
}
