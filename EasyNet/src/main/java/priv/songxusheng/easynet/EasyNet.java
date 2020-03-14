package priv.songxusheng.easynet;

import android.os.Handler;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.base.Request;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Response;

public class EasyNet {
    private EasyNet(){}

    private static Class clsTarget = null;
    private static Method mtdEasyResponse = null;
    public static void init(final Class clazz){
        clsTarget = clazz;
        Set<Method> methods = new HashSet<>(new ArrayList(Arrays.asList(clsTarget.getMethods())){{addAll(Arrays.asList(clsTarget.getDeclaredMethods()));}});

        find_method:
            for(Method method :methods){
                Annotation[] annotations = method.getAnnotations();
                for(Annotation annotation : annotations){
                    if(annotation instanceof BindEasyResponseMethod){
                        mtdEasyResponse = method;
                        break find_method;
                    }
                }
            }
    }

    final static private Handler handler = new Handler();
    final static private String Caches[] = new String[]{"easynetget","easynetpost","easynetdelete","easynetput"};
    public final static void easyRequest(final EasyRequestConfig config, final EasyListener listener){
        new Thread() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                Response response = null;
                EasyNetApi api = config.api;
                if(api == null){
                    listener.onFailure(800,"{\"code\":\"800\",\"msg\":\"EasyNetApi was null!\"}");
                    sb.append("\n"+"EasyNetApi was null!");
                    return;
                }
                sb.append("\n"+config);
                try{
                    if(config.jsonParams!=null){
                        switch (config.api.Type){
                            case GET:
                                response= OkGo.<JSONObject>get(api.Url).headers(config.headers).params(config.params).execute();
                                break;
                            case POST:
                                response=OkGo.<JSONObject>post(api.Url).headers(config.headers).upJson(config.jsonParams).execute();
                                break;
                            case DELETE:
                                response=OkGo.<JSONObject>delete(api.Url).headers(config.headers).upJson(config.jsonParams).execute();
                                break;
                            case PUT:
                                response=OkGo.<JSONObject>put(api.Url).headers(config.headers).upJson(config.jsonParams).execute();
                                break;
                        }
                    }
                    else {
                        Request request=null;
                        switch (api.Type){
                            case GET:
                                request= OkGo.get(api.Url);
                                break;
                            case POST:
                                request=OkGo.post(api.Url);
                                break;
                            case DELETE:
                                request=OkGo.delete(api.Url);
                                break;
                            case PUT:
                                request=OkGo.put(api.Url);
                                break;
                        }

                        HttpHeaders headers = config.headers;
                        if(headers!=null&&!headers.headersMap.isEmpty()) {//请求头
                            request.headers(headers);
                        }
                        HttpParams params=config.params;
                        if(params!=null&&(!params.fileParamsMap.isEmpty()||!params.urlParamsMap.isEmpty())) {//请求参数
                            request.params(params);
                        }
                        response =  request.tag(this)       // 请求的 tag, 主要用于取消对应的请求
                                .cacheKey(Caches[api.Type.Code])   // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                                .cacheMode(CacheMode.DEFAULT)              // 缓存模式，详细请看缓存介绍
                                .execute();
                    }
                    mtdEasyResponse.invoke(null,api,response,listener);
                }
                catch (final Exception e){
                    sb.append("\n"+"exception->"+e.getMessage());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailure(800,e.getMessage());
                        }
                    });
                }
                Log.e("EasyNet","EasyRequest:"+sb);
            }
        }.start();
    }
}