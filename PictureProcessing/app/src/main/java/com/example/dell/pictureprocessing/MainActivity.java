package com.example.dell.pictureprocessing;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {


    private SQLiteDatabase writeDB;
    private SQLiteDatabase readDB;

    private ImageView showImage;

    private Stack<Drawable> pictureForRevoke;
    private int maxRevokePictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRevokeValue();
//        initDB();
        initToolbar();
        initShowImage();
        initAddPictureFabBtn();
        initRevokeFabBtn();
    }

    private void initRevokeValue(){
        pictureForRevoke=new Stack<>();
        maxRevokePictures=10;
    }


//    private void initDB(){
//        SQLiteOp sqLiteOp=new SQLiteOp(this);
//        writeDB=sqLiteOp.getWritableDatabase();
//        readDB=sqLiteOp.getReadableDatabase();
//        clearDB();
//    }
//
//    private void clearDB(){
//        String deleteCmm="delete from picture";
//        writeDB.execSQL(deleteCmm);
//    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }





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
        if(id!=R.id.action_save)
            putPictureIntoRevokeStack();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_save:
                savePicture();
                break;
            case R.id.action_hReverse:
                reversePicture(0);
                break;
            case R.id.action_vReverse:
                reversePicture(1);
                break;
            case R.id.action_relief:
                reliefPicture();
                break;
            case R.id.action_sharpen:
                sharpenPicture();
                break;
            case R.id.action_extract:
                extractPicture();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void savePicture(){
        MyImageProcessor.saveImageToGallery(this,MyImageProcessor.drawableToBitmap(showImage.getDrawable()));
    }

    private void putPictureIntoRevokeStack(){
        if(pictureForRevoke.size()>=maxRevokePictures){
            pictureForRevoke.remove(0);
        }
        Drawable currentPicture=showImage.getDrawable();
        pictureForRevoke.push(currentPicture);
    }


    private void reversePicture(int flag){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap reversedBitmap=MyImageProcessor.reversePicture(curImageDrawable,flag);
        Drawable reversePicture=new BitmapDrawable(this.getResources(),reversedBitmap);
        showImage.setImageDrawable(reversePicture);
    }


    private void reliefPicture(){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap reliefBitmap=MyImageProcessor.reliefPicture(curImageDrawable);
        Drawable reliefPicture=new BitmapDrawable(this.getResources(),reliefBitmap);
        showImage.setImageDrawable(reliefPicture);
    }


    private void sharpenPicture(){
        Drawable curImageDrawable=showImage.getDrawable();
        Bitmap sharpenBitmap=MyImageProcessor.sharpenPicture(curImageDrawable);
        Drawable sharpenPicture=new BitmapDrawable(this.getResources(),sharpenBitmap);
        showImage.setImageDrawable(sharpenPicture);
    }


    private void extractPicture(){
        MyImageProcessor.currentPicture=showImage.getDrawable();
        Intent intent=new Intent(this,ExtractPicture.class);
        startActivity(intent);
    }


//    private void savePictureIntoDB(){
//        Drawable picture=showImage.getDrawable();
//        Bitmap bmp = (((BitmapDrawable)picture).getBitmap());
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
//        addPictureIntoSQLite(os);
//    }
//
//
//
//
//
//    private void addPictureIntoSQLite(ByteArrayOutputStream os){
//        ContentValues values=new ContentValues();
//        values.put("picture",os.toByteArray());
//        writeDB.insert("picture",null,values);
//        successTip();
//    }
//
//
//    private void successTip(){
//        new AlertDialog.Builder(this).setTitle("提示").setMessage("图片保存成功！")
//                .setPositiveButton("确定",null)
//                .show();
//    }


    //获取资源文件中的图片
//    public Drawable getPictrue(int position){
//        String pictureName="saber"+String.valueOf(position);
//        int picID=getPictureID(pictureName);
//        if (picID!=0){
//            Drawable showPicture= ContextCompat.getDrawable(this,picID);
//            return showPicture;
//        }
//        return null;
//    }
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


//    private void getPictrueFromDB() {
//        String[] columns = {"id", "picture"};
//        Cursor cursor = readDB.query("picture", columns, null, null, null, null, null);
//        //判断游标是否为空
//        if (cursor.moveToFirst())
//        {
//            do{
//                int id = cursor.getInt(0);
//                byte[] blob = cursor.getBlob(1);
//                Drawable picture=exchangeByteToDrawble(blob);
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
//    }
//
//
//    private Drawable exchangeByteToDrawble(byte[] blob){
//        Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
//        BitmapDrawable bd = new BitmapDrawable(this.getResources(),bmp);
//        return bd;
//    }




    private void initAddPictureFabBtn(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//设置类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
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


    private void initRevokeFabBtn(){
        FloatingActionButton revokeFab = (FloatingActionButton) findViewById(R.id.revoke_fab);
        revokeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokePicture();
            }
        });
    }

    private void revokePicture(){
        if(!pictureForRevoke.empty()){
            showImage.setImageDrawable(pictureForRevoke.peek());
            pictureForRevoke.pop();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeDB.close();
        readDB.close();
    }
}
