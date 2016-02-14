package com.lcc.uestc.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.WeatherBean;
import com.lcc.uestc.data.Weather;
import com.lcc.uestc.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by lcc_luffy on 2016/1/8.
 */
public class WeatherFragment extends BaseFragment implements Weather.WeatherListener{
    @Bind(R.id.today)
    TextView mTodayTV;

    @Bind(R.id.weatherImage)
    ImageView mTodayWeatherImage;

    @Bind(R.id.weatherTemp)
    TextView mTodayTemperatureTV;

    @Bind(R.id.wind)
    TextView mTodayWindTV;

    @Bind(R.id.weather)
    TextView mTodayWeatherTV;

    @Bind(R.id.city)
    TextView mCityTV;

    @Bind(R.id.progress)
    ProgressBar mProgressBar;

    @Bind(R.id.weather_layout)
    LinearLayout mWeatherLayout;

    @Bind(R.id.weather_content)
    LinearLayout mWeatherContentLayout;

    List<WeatherBean> weatherBeanList;
    @Override
    protected void initInOnCreate() {
        actionBarCommands = new ActionBarCommands() {
            @Override
            public void refresh(boolean force) {
                update();
            }

            @Override
            public void OnFragmentSelected() {

                if(weatherBeanList == null)
                {
                    CommonUtils.i("OnFragmentSelected");
                    update();
                }

            }
        };

    }

    public void update()
    {
        showProgress();
        Weather.loadLocation(getActivity(), new Weather.LocationListener() {
            @Override
            public void onSuccess(String location) {
                setCity(location);
                Weather.loadWeatherData(location,WeatherFragment.this);
            }

            @Override
            public void onFail(String msg) {
                hideProgress();
            }
        });
    }
    public void setCity(String city)
    {
        mCityTV.setText(city);
    }
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void showWeatherLayout() {
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
    @Override
    protected int getLayoutView() {
        return R.layout.fragment_weather;
    }

    @Override
    public void onSuccess(List<WeatherBean> weatherBeanList) {
        this.weatherBeanList = weatherBeanList;

        parse(weatherBeanList);
        List<View> adapterList = new ArrayList();
        mWeatherContentLayout.removeAllViews();
        for (WeatherBean weatherBean : weatherBeanList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_weather, null, false);
            TextView dateTV = (TextView) view.findViewById(R.id.date);
            ImageView todayWeatherImage = (ImageView) view.findViewById(R.id.weatherImage);
            TextView todayTemperatureTV = (TextView) view.findViewById(R.id.weatherTemp);
            TextView todayWindTV = (TextView) view.findViewById(R.id.wind);
            TextView todayWeatherTV = (TextView) view.findViewById(R.id.weather);

            dateTV.setText(weatherBean.getWeek());
            todayTemperatureTV.setText(weatherBean.getTemperature());
            todayWindTV.setText(weatherBean.getWind());
            todayWeatherTV.setText(weatherBean.getWeather());
            todayWeatherImage.setImageResource(weatherBean.getImageRes());
            mWeatherContentLayout.addView(view);
            adapterList.add(view);
        }
        hideProgress();
    }

    void parse(List<WeatherBean> list)
    {
        if(list != null && list.size() > 0) {
            WeatherBean todayWeather = list.remove(0);
            mTodayTV.setText(todayWeather.getDate());
            mTodayTemperatureTV.setText(todayWeather.getTemperature());
            mTodayWeatherTV.setText(todayWeather.getWeather());
            mTodayWindTV.setText(todayWeather.getWind());
            mTodayWeatherImage.setImageResource(todayWeather.getImageRes());
        }
        showWeatherLayout();
    }

    @Override
    public String toString() {
        return "天气";
    }

    @Override
    public void onFail(String msg) {
        showSnackBar(msg);
        hideProgress();
    }
}
