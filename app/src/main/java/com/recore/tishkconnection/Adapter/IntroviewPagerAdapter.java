package com.recore.tishkconnection.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recore.tishkconnection.R;
import com.recore.tishkconnection.Model.IntroScreen;

import java.util.List;

public class IntroviewPagerAdapter extends PagerAdapter {

    Context mContext;
    List<IntroScreen> mScreenItems;

    public IntroviewPagerAdapter(Context context, List<IntroScreen> screenItems) {
        mContext = context;
        mScreenItems = screenItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen = inflater.inflate(R.layout.inro_layout, null);

        ImageView introImg = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

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
        container.removeView((View) object);

    }
}
