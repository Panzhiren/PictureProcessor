package com.example.dell.pictureprocessing;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //floating action
    private FloatingActionButton fab;

    private SQLiteOp sqLiteOp;

    private ImageView showImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sqLiteOp=new SQLiteOp(this);
        initToolbar();
        initShowImage();
//        initViewPager();
        initFloatingActionBtn();

    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


//    private void initViewPager(){
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//    }


    private void initShowImage(){
        showImage=(ImageView)findViewById(R.id.ShowImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so test1
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_save:
                savePictureIntoDB();
                break;
            case R.id.action_hReverse:
                pictureReverse(0);
                break;
            case R.id.action_vReverse:
                pictureReverse(1);
                break;
            case R.id.action_relief:
                pictureRelief();
                break;
            case R.id.action_sharpen:
                pictureSharpen();
                break;
            case R.id.action_extract:
                pictureExtract();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void savePictureIntoDB(){
        Drawable picture=showImage.getDrawable();
        Bitmap bmp = (((BitmapDrawable)picture).getBitmap());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        addPictrueIntoSQLite(os);
    }



//    public Drawable getPictrue(int position){
//        String pictureName="saber"+String.valueOf(position);
//        int picID=getPictureID(pictureName);
//        if (picID!=0){
//            Drawable showPicture=ContextCompat.getDrawable(this,picID);
//            return showPicture;
//        }
//        return null;
//    }
//
//
//
//    public int getPictureID(String pictureName){
//        Class drawable = R.drawable.class;
//        Field field ;
//        try {
//            field = drawable.getField(pictureName);
//            int res_ID = field.getInt(field.getName());
//            return res_ID;
//        } catch (Exception e) {
//            return 0;
//        }
//
//    }

    private void addPictrueIntoSQLite(ByteArrayOutputStream os){
        SQLiteDatabase db=this.sqLiteOp.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("picture",os.toByteArray());
        db.insert("picture",null,values);
        db.close();
        successTip();
    }


    private void successTip(){
        new AlertDialog.Builder(this).setTitle("提示").setMessage("图片保存成功！")
                .setPositiveButton("确定",null)
                .show();
    }



    private void getPictrueFromDB() {
        SQLiteDatabase db = this.sqLiteOp.getReadableDatabase();
        String[] columns = {"id", "picture"};
        Cursor cursor = db.query("picture", columns, null, null, null, null, null);
        //判断游标是否为空
        if (cursor.moveToFirst())
        {
            do{
                int id = cursor.getInt(0);
                byte[] blob = cursor.getBlob(1);
                Drawable picture=exchangeByteToDrawble(blob);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    private Drawable exchangeByteToDrawble(byte[] blob){
        Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        BitmapDrawable bd = new BitmapDrawable(this.getResources(),bmp);
        return bd;
    }




    private void initFloatingActionBtn(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//设置类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                showPictureFromFile(uri);
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


    private void pictureReverse(int flag){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap reversedBitmap=MyImageProcessor.reversePicture(curImageDrawable,flag);
        Drawable reversePicture=new BitmapDrawable(this.getResources(),reversedBitmap);
        showImage.setImageDrawable(reversePicture);
    }


    private void pictureRelief(){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap reliefBitmap=MyImageProcessor.reliefPicture(curImageDrawable);
        Drawable reliefPicture=new BitmapDrawable(this.getResources(),reliefBitmap);
        showImage.setImageDrawable(reliefPicture);
    }


    private void pictureSharpen(){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap sharpenBitmap=MyImageProcessor.sharpenPicture(curImageDrawable);
        Drawable sharpenPicture=new BitmapDrawable(this.getResources(),sharpenBitmap);
        showImage.setImageDrawable(sharpenPicture);
    }

    private void pictureExtract(){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap extractBitmap=MyImageProcessor.extractPicture(curImageDrawable);
        Drawable extractPicture=new BitmapDrawable(this.getResources(),extractBitmap);
        showImage.setImageDrawable(extractPicture);
    }

}
