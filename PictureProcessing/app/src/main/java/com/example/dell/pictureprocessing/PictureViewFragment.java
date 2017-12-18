package com.example.dell.pictureprocessing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by dell on 2017/12/10.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class PictureViewFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PictureViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PictureViewFragment newInstance(int sectionNumber) {
        PictureViewFragment fragment = new PictureViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        int position=getArguments().getInt(ARG_SECTION_NUMBER);
        setText(rootView,position);
        setImageView(rootView,position);
        return rootView;
    }


    public void setText(View rootView,int position){
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, position));
    }



    public void setImageView(View rootView,int position){
        ImageView showImage=(ImageView) rootView.findViewById(R.id.imageView);
        String pictureName="saber"+String.valueOf(position);
        int picID=getPictureID(pictureName);
        if (picID!=0){
            Drawable showPicture=getResources().getDrawable(picID);
            showImage.setImageDrawable(showPicture);
        }
    }

    public int getPictureID(String pictureName){
        Class drawable = R.drawable.class;
        Field field = null;
        try {
            field = drawable.getField(pictureName);
            int res_ID = field.getInt(field.getName());
            return res_ID;
        } catch (Exception e) {
            return 0;
        }

    }
}
