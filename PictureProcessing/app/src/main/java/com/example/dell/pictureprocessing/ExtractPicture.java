package com.example.dell.pictureprocessing;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dell.pictureprocessing.detection.Detection;
import com.example.dell.pictureprocessing.detection.Sobel;



public class ExtractPicture extends AppCompatActivity implements IMain {

    private boolean dealing;
    private ImageView showImage;
    private Bitmap bitmap;

    private Detection detection;
    private EditText et_threshold;
    private EditText et_gray_threshold;
    private EditText et_pale_grey_threshold;

    private Button sureExtractBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_picture);
        init();
        initToolbar();
        initShowPicture();
        initExtratBtn();
    }

    private void init(){
        et_threshold = (EditText) findViewById(R.id.threshold1);
        et_gray_threshold = (EditText) findViewById(R.id.threshold2);
        et_pale_grey_threshold = (EditText) findViewById(R.id.threshold3);
        sureExtractBtn=(Button)findViewById(R.id.sure_extract);

        dealing=false;
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.extract_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_extract, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so test1
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.extract_action_save:
                MyImageProcessor.saveImageToGallery(this,MyImageProcessor.drawableToBitmap(showImage.getDrawable()));
                break;
            case R.id.extract_action_choose:
                choosePicture();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initShowPicture(){
        showImage=(ImageView)findViewById(R.id.show_extract_picture);
        if(MyImageProcessor.currentPicture!=null)
            showImage.setImageDrawable(MyImageProcessor.currentPicture);
        bitmap =MyImageProcessor.drawableToBitmap(showImage.getDrawable());
        detection= new Sobel(this);
    }

    private void initExtratBtn(){
        sureExtractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dealing) {
                    Toast.makeText(ExtractPicture.this, "图象处理中...", Toast.LENGTH_SHORT).show();
                } else {
                    dealing = true;
                    String sa = et_threshold.getText().toString();
                    sa = sa.equals("") ? "0" : sa;
                    String sb = et_gray_threshold.getText().toString();
                    sb = sb.equals("") ? "0" : sb;
                    String sc = et_pale_grey_threshold.getText().toString();
                    sc = sc.equals("") ? "0" : sc;

                    double a = Double.parseDouble(sa);
                    double b = Double.parseDouble(sb);
                    double c = Double.parseDouble(sc);
//                    Toast.makeText(this, a + "\n" + b + "\n" + c, Toast.LENGTH_SHORT).show();
                    deal(a, b, c);
                }
            }
        });
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        Message msg = new Message();
        msg.obj = bitmap;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            showBitmap(bitmap);
            dealing = false;
        }
    };

    private void deal(final double a, final double b, final double c){
        new Thread(){
            @Override
            public void run() {
                detection.detection(bitmap);
                ((Sobel) detection).getBitmap(a, b, c);
            }
        }.start();
    }

    private void showBitmap(Bitmap bitmap){

        if(bitmap != null) {
            showImage.setImageBitmap(bitmap);
            Log.i("MainActivity", "bitmap load end");
        } else {
            Log.i("MainActivity", "bitmap null");
        }
    }


    private void choosePicture(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//设置类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                showPictureFromFile(uri);
                bitmap =MyImageProcessor.drawableToBitmap(showImage.getDrawable());
//                String selectedPath=uri.toString();
//                String picturePath=getApplicationContext().getFilesDir().getAbsolutePath();
//                Toast.makeText(this, "文件路径："+selectedPath+"\r\n"+picturePath, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPictureFromFile(Uri mImageCaptureUri){
        Bitmap photoBmp =getBitmap(mImageCaptureUri);
        Drawable showDrawable=new BitmapDrawable(this.getResources(),photoBmp);
        showImage.setImageDrawable(showDrawable);
    }


    public Bitmap getBitmap(Uri url) {
        try{
            Bitmap photoBmp;
            if (url !=null) {
                ContentResolver mContentResolver = this.getContentResolver();
                photoBmp = MediaStore.Images.Media.getBitmap(mContentResolver, url);
                return photoBmp;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
