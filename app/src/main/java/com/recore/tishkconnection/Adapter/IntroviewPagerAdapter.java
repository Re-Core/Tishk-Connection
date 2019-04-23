package com.recore.tishkconnection.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recore.tishkconnection.R;
import com.recore.tishkconnection.Model.ScreenItem;

import java.util.List;

public class IntroviewPagerAdapter extends PagerAdapter {

    Context mContext;
    List<ScreenItem>mScreenItems;

    public IntroviewPagerAdapter(Context context, List<ScreenItem> screenItems) {
        mContext = context;
        mScreenItems = screenItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen = inflater.inflate(R.layout.inro_layout,null);

        ImageView introImg =layoutScreen.findViewById(R.id.intro_img);
        TextView title=layoutScreen.findViewById(R.id.intro_title);
        TextView description=layoutScreen.findViewById(R.id.intro_description);

        title.setText(mScreenItems.get(position).getTitle());
        description.setText(mScreenItems.get(position).getDescription());
        introImg.setImageResource(mScreenItems.get(position).getIntroImage());


        container.addView(layoutScreen);
        return layoutScreen;

    }

    @Override
    public int getCount() {
        return mScreenItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);

    }
}
