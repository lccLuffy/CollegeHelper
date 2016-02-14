package com.lcc.uestc.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lcc.uestc.R;
import com.lcc.uestc.bean.WeatherBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by lcc_luffy on 2016/1/8.
 */
public class Weather {
    public static final String IMAGES_URL = "http://api.laifudao.com/open/tupian.json";

    // 天气预报url
    public static final String WEATHER = "http://wthrcdn.etouch.cn/weather_mini?city=";

    //百度定位
    public static final String INTERFACE_LOCATION = "http://api.map.baidu.com/geocoder";

    public static void loadWeatherData(String cityName, final WeatherListener listener)
    {
        try {
            String url = WEATHER + URLEncoder.encode(cityName,"utf-8");
            OkHttpUtils.get().url(url).build().execute(new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    if(listener != null)
                        listener.onFail(e.toString());
                }

                @Override
                public void onResponse(String response) {
                    if(listener != null)
                    {
                        List<WeatherBean> beanList = getWeatherInfo(response);
                        listener.onSuccess(beanList);
                    }

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if(listener != null)
                listener.onFail(e.toString());
        }

    }
    public static void loadLocation(Context context, final LocationListener listener)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if(listener != null)
                    listener.onFail("location failure.");
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null)
        {
            if(listener != null)
                listener.onFail("location failure.");
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        OkHttpUtils.get().url(getLocationURL(latitude,longitude)).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if(listener != null)
                    listener.onFail(e.toString());
            }

            @Override
            public void onResponse(String response) {
                String city = getCity(response);
                if(TextUtils.isEmpty(city))
                {
                    if(listener != null)
                        listener.onFail("fail");
                    return;
                }
                if(listener != null)
                    listener.onSuccess(city);
            }
        });
    }
    private static String getLocationURL(double latitude, double longitude) {
        StringBuffer sb = new StringBuffer(INTERFACE_LOCATION);
        sb.append("?output=json").append("&referer=32D45CBEEC107315C553AD1131915D366EEF79B4");
        sb.append("&location=").append(latitude).append(",").append(longitude);
        return sb.toString();
    }
    private static String getCity(String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        JsonElement status = jsonObj.get("status");
        if(status != null && "OK".equals(status.getAsString())) {
            JsonObject result = jsonObj.getAsJsonObject("result");
            if(result != null) {
                JsonObject addressComponent = result.getAsJsonObject("addressComponent");
                if(addressComponent != null) {
                    JsonElement cityElement = addressComponent.get("city");
                    if(cityElement != null) {
                        return cityElement.getAsString().replace("市", "");
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取天气信息
     * @param json
     * @return
     */
    private static List<WeatherBean> getWeatherInfo(String json) {
        List<WeatherBean> list = new ArrayList<WeatherBean>();
        if (TextUtils.isEmpty(json)) {
            return list;
        }
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        String status = jsonObj.get("status").getAsString();
        if("1000".equals(status)) {
            JsonArray jsonArray = jsonObj.getAsJsonObject("data").getAsJsonArray("forecast");
            for (int i = 0; i < jsonArray.size(); i++) {
                WeatherBean weatherBean = getWeatherBeanFromJson(jsonArray.get(i).getAsJsonObject());
                list.add(weatherBean);
            }
        }
        return list;
    }
    private static WeatherBean getWeatherBeanFromJson(JsonObject jsonObject) {

        String temperature = jsonObject.get("high").getAsString() + " " + jsonObject.get("low").getAsString();
        String weather = jsonObject.get("type").getAsString();
        String wind = jsonObject.get("fengxiang").getAsString();
        String date = jsonObject.get("date").getAsString();

        WeatherBean weatherBean = new WeatherBean();

        weatherBean.setDate(date);
        weatherBean.setTemperature(temperature);
        weatherBean.setWeather(weather);
        weatherBean.setWeek(date.substring(date.length()-3));
        weatherBean.setWind(wind);
        weatherBean.setImageRes(getWeatherImage(weather));
        return weatherBean;
    }

    public static int getWeatherImage(String weather) {
        if (weather.equals("多云") || weather.equals("多云转阴") || weather.equals("多云转晴")) {
            return R.drawable.biz_plugin_weather_duoyun;
        } else if (weather.equals("中雨") || weather.equals("中到大雨")) {
            return R.drawable.biz_plugin_weather_zhongyu;
        } else if (weather.equals("雷阵雨")) {
            return R.drawable.biz_plugin_weather_leizhenyu;
        } else if (weather.equals("阵雨") || weather.equals("阵雨转多云")) {
            return R.drawable.biz_plugin_weather_zhenyu;
        } else if (weather.equals("暴雪")) {
            return R.drawable.biz_plugin_weather_baoxue;
        } else if (weather.equals("暴雨")) {
            return R.drawable.biz_plugin_weather_baoyu;
        } else if (weather.equals("大暴雨")) {
            return R.drawable.biz_plugin_weather_dabaoyu;
        } else if (weather.equals("大雪")) {
            return R.drawable.biz_plugin_weather_daxue;
        } else if (weather.equals("大雨") || weather.equals("大雨转中雨")) {
            return R.drawable.biz_plugin_weather_dayu;
        } else if (weather.equals("雷阵雨冰雹")) {
            return R.drawable.biz_plugin_weather_leizhenyubingbao;
        } else if (weather.equals("晴")) {
            return R.drawable.biz_plugin_weather_qing;
        } else if (weather.equals("沙尘暴")) {
            return R.drawable.biz_plugin_weather_shachenbao;
        } else if (weather.equals("特大暴雨")) {
            return R.drawable.biz_plugin_weather_tedabaoyu;
        } else if (weather.equals("雾") || weather.equals("雾霾")) {
            return R.drawable.biz_plugin_weather_wu;
        } else if (weather.equals("小雪")) {
            return R.drawable.biz_plugin_weather_xiaoxue;
        } else if (weather.equals("小雨")) {
            return R.drawable.biz_plugin_weather_xiaoyu;
        } else if (weather.equals("阴")) {
            return R.drawable.biz_plugin_weather_yin;
        } else if (weather.equals("雨夹雪")) {
            return R.drawable.biz_plugin_weather_yujiaxue;
        } else if (weather.equals("阵雪")) {
            return R.drawable.biz_plugin_weather_zhenxue;
        } else if (weather.equals("中雪")) {
            return R.drawable.biz_plugin_weather_zhongxue;
        } else {
            return R.drawable.biz_plugin_weather_duoyun;
        }
    }
    public interface WeatherListener
    {
        void onSuccess(List<WeatherBean> weatherBeanList);
        void onFail(String msg);
    }
    public interface LocationListener
    {
        void onSuccess(String location);
        void onFail(String msg);
    }
}
