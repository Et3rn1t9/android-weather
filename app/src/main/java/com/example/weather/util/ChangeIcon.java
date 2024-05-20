package com.example.weather.util;

import android.widget.ImageView;

import com.example.weather.R;

public class ChangeIcon {
    public static void changeIcon(ImageView weatherStateIcon, int code) {
        switch (code) {
            case 100://晴
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_100);
                break;
            case 101://多云
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_101);
                break;
            case 102://少云
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_102);
                break;
            case 103://晴间多云
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_103);
                break;
            case 104:
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_102);
                break;
            case 150://夜晴
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_150);
                break;
            case 151://夜多云
            case 152:
            case 153:
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_150);
                break;
            case 200://有风
            case 202:
            case 203:
            case 204:
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_200d);//因为这几个状态的图标是一样的
                break;
            case 201://平静
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_200d);
                break;
            case 205://强风/劲风
            case 206://疾风
            case 207://大风
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_200d);//因为这几个状态的图标是一样的
                break;
            case 208://烈风
            case 209://风暴
            case 210://狂爆风
            case 211://飓风
            case 212://龙卷风
            case 213://热带风暴
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_208);//因为这几个状态的图标是一样的
                break;
            case 300://阵雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_300);
                break;
            case 301://强阵雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_301);
                break;
            case 302://雷阵雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_302);
                break;
            case 303://强雷阵雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_303);
                break;
            case 304://雷阵雨伴有冰雹
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_304);
                break;
            case 305://小雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_305);
                break;
            case 306://中雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_306);
                break;
            case 307://大雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_307);
                break;
            case 308://极端降雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_312);
                break;
            case 309://毛毛雨/细雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_309);
                break;
            case 310://暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_310);
                break;
            case 311://大暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_311);
                break;
            case 312://特大暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_312);
                break;
            case 313://冻雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_313);
                break;
            case 314://小到中雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_306);
                break;
            case 315://中到大雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_307);
                break;
            case 316://大到暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_310);
                break;
            case 317://大暴雨到特大暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_312);
                break;
            case 350://大暴雨到特大暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_350);
                break;
            case 351://大暴雨到特大暴雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_351);
                break;
            case 399://雨
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_399);
                break;
            case 400://小雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_400);
                break;
            case 401://中雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_401);
                break;
            case 402://大雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_402);
                break;
            case 403://暴雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_403);
                break;
            case 404://雨夹雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_404);
                break;
            case 405://雨雪天气
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_405);
                break;
            case 406://阵雨夹雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_406);
                break;
            case 407://阵雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_407);
                break;
            case 408://小到中雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_408);
                break;
            case 409://中到大雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_409);
                break;
            case 410://大到暴雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_410);
                break;
            case 456://大到暴雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_456);
                break;
            case 457://大到暴雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_457);
                break;
            case 499://雪
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_499);
                break;
            case 500://薄雾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_500);
                break;
            case 501://雾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_501);
                break;
            case 502://霾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_502);
                break;
            case 503://扬沙
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_503);
                break;
            case 504://扬沙
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_504);
                break;
            case 507://沙尘暴
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_507);
                break;
            case 508://强沙尘暴
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_508);
                break;
            case 509://浓雾
            case 510://强浓雾
            case 514://大雾
            case 515://特强浓雾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_509);
                break;
            case 511://中度霾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_511);
                break;
            case 512://重度霾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_512);
                break;
            case 513://严重霾
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_513);
                break;
            case 900://热
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_900);
                break;
            case 901://冷
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_901);
                break;
            case 999://未知
                weatherStateIcon.setBackgroundResource(R.mipmap.icon_999);
                break;
        }
    }
}
