package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author: Kimler Jin
 * Date: 2021/07/17 19:15
 * Content: DEMO
 */
@Component
public class WeatherService {


    private Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final String PROVINCE_URL = "http://www.weather.com.cn/data/city3jdata/china.html";

    private final String CITY_URL_PREFIX = "http://www.weather.com.cn/data/city3jdata/provshi/";

    private final String WEATHER_URL_PREFIX = "http://www.weather.com.cn/data/sk/";

    private final List<String> specific = Arrays.asList("澳门", "香港");

    /**
     * 获取城市天气
     *
     * @param province 省
     * @param city     市
     * @param county   国家，题目中除了中国没用其他。不传也可以
     * @return
     */
    public Optional<Integer> getTemperature(String province, String city, String county) {
        if (!StringUtils.hasLength(city) || !StringUtils.hasLength(province)) {
            throw new RuntimeException("省/城市 不能为空");
        }
        JSONObject jsonObjectProvince = JSONObject.parseObject(getUrlInfo(PROVINCE_URL));
        if (jsonObjectProvince != null && jsonObjectProvince.values().contains(province)) {
            String provinceCode = getKeyByValue(province, jsonObjectProvince);
            StringBuilder sbProvinceUrl = new StringBuilder(CITY_URL_PREFIX);
            sbProvinceUrl.append(provinceCode).append(".html");
            JSONObject jsonObjectCity = JSONObject.parseObject(getUrlInfo(sbProvinceUrl.toString()));
            if (jsonObjectCity != null && jsonObjectCity.values().contains(city)) {
                String cityCode = provinceCode + getKeyByValue(city, jsonObjectCity);
                ;
                StringBuilder sbCityWeather = new StringBuilder(WEATHER_URL_PREFIX);
                if (jsonObjectCity.values().size() == 1 && !specific.contains(city)) {
                    sbCityWeather.append("10").append(cityCode).append(".html");
                } else {
                    sbCityWeather.append(cityCode).append("01.html");
                }
                JSONObject jsonObjectWeather = JSONObject.parseObject(getUrlInfo(sbCityWeather.toString()));
                if (jsonObjectWeather.getJSONObject("weatherinfo") != null && jsonObjectWeather.getJSONObject("weatherinfo").get("temp") != null) {
                    try {
                        //获取温度，有可能不为数值
                        BigDecimal temperature = jsonObjectWeather.getJSONObject("weatherinfo").getBigDecimal("temp");
                        if (temperature != null) {
                            return Optional.of(temperature.intValue());
                        }
                    } catch (Exception e) {
                        //不为数值则把它的返回直接抛出
                        logger.error(e.getMessage(), e);
                        throw new WeatherException(jsonObjectWeather.getJSONObject("weatherinfo").getString("temp"));
                    }
                }
                throw new WeatherException(province + city + "天气不存在");


            } else {
                throw new WeatherException(province + city + "不存在");
            }
        } else {
            throw new WeatherException(province + "不存在");
        }
    }


    /**
     * 根据value获取KEY
     *
     * @param value      值
     * @param jsonObject json对象
     * @return 返回key
     */
    private String getKeyByValue(String value, JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException(value + "不存在！");
    }


    /**
     * 根据URL获取内容
     *
     * @param url
     * @return
     */
    public String getUrlInfo(String url) {
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            return convertStreamToString((InputStream) conn.getContent());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 乱码处理，流转成可读的UTF-8
     *
     * @param is
     * @return
     */
    private String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return sb1.toString();
    }


}
