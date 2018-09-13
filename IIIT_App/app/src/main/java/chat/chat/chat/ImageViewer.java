package chat.chat.chat;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import chat.chat.R;

public class ImageViewer extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        imageView=(ImageView)findViewById(R.id.imageView);
        textView=(TextView)findViewById(R.id.textView);
        File file= (File) getIntent().getExtras().get("file");
        String text=getIntent().getStringExtra("text");
        String context=getIntent().getStringExtra("context");

        imageView.setImageURI(Uri.fromFile(file));
        textView.setText(text);
    }
}
