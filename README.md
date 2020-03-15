# EasyNet
A more focused framework for network requests and parsing.

https://blog.csdn.net/best335/article/details/104859525

还在使用各种Bean么？还在声明各种接口请求方法吗？
快尝试一下EasyNet吧，一个接口使用一个EasyNetApi对象，只需要定义好你需要请求的EasyNetApi对象，并在回调函数中完成回调就可以了。
**从此解放你的双手，走上人生巅峰，迎娶白富美，暗示了这么多，还不快快尝试一下？**
# [查看Easy全家桶](https://blog.csdn.net/best335/article/details/104842939)
# 使用示例
#### 添加依赖
```bash
//build.gradle
//项目刚上传仓库（2020年3月14日16:51） 预计2020年3月14日18:00左右可添加依赖
dependencies {
	...
	api "com.github.isong0623:EasyNet:1.0-core"
	...
}
```
#### 新建网络类

```java
import org.json.JSONObject;

import okhttp3.Response;
import priv.songxusheng.easyjson.ESON;
import priv.songxusheng.easynet.BindEasyRequestItem;
import priv.songxusheng.easynet.BindEasyRequestMethod;
import priv.songxusheng.easynet.BindEasyResponseMethod;
import priv.songxusheng.easynet.EasyListener;
import priv.songxusheng.easynet.EasyNet;
import priv.songxusheng.easynet.EasyNetApi;
import priv.songxusheng.easynet.EasyRequestConfig;
import priv.songxusheng.easynet.EasyRequestType;

public class Api {
    private final static String baseUrl1 = "http://wthrcdn.etouch.cn/weather_mini";
    static{ EasyNet.init(Api.class);}//这步及其重要
    @BindEasyRequestItem//这个注解不作用于反射，只是增强可读性
    final static EasyNetApi GET_WHEATHER = new EasyNetApi(
    		0,//API id
            baseUrl1+"",//URL
            "获取青岛天气",//请求描述
             EasyRequestType.GET,//请求类型为Get 其他还有Post,pull,delete
             null,null,"city","城市名称");//这个String params[]结构如下: "请求头键名","请求头描述","请求参数名1","请求参数值1描述","请求参数名2","请求参数值2描述"... 哪项为空哪项填null就可以

    @BindEasyRequestMethod//这个注解不作用于反射，只是增强可读性,方法实现也不是必要的
    final public static void easyRequest(EasyRequestConfig config, EasyListener listener){
        EasyNet.easyRequest(config,listener);
    }

//这个接口通用回调处理方法必须为public static 并且参数一定要和下方实现一致
    @BindEasyResponseMethod
    final public static void easyResponse(EasyNetApi requestItem, Response responseData, EasyListener listener){
        try {
            String response = responseData.body().string();
            switch (requestItem.Id){
                case 0:
                    JSONObject jsonObject = ESON.getJSONObject(response);
                    listener.onSuccess(200,jsonObject);
                    return;
            }
            listener.onSuccess(200,response);
        } catch (Exception e) {
            listener.onFailure(400,e);
        }
    }
}
```
#### 调用测试Api.class中的接口

```java
  Api.easyRequest(new EasyRequestConfig(Api.GET_WHEATHER,new String[]{null,"青岛"}), new EasyListener() {
            @Override
            public void onSuccess(int code, Object msg) {
                Log.e("Api",msg+"");
            }
            @Override
            public void onFailure(int code, Object msg) {
                Log.e("Api",msg+"");
            }
        });
```
###### 接口返回结果
```java
E/Api: {"data":{"yesterday":{"date":"13日星期五","high":"高温 8℃","fx":"东北风","low":"低温 3℃","fl":"<![CDATA[3-4级]]>","type":"多云"},"city":"青岛","forecast":[{"date":"14日星期六","high":"高温 13℃","fengli":"<![CDATA[4-5级]]>","low":"低温 5℃","fengxiang":"西北风","type":"晴"},{"date":"15日星期天","high":"高温 13℃","fengli":"<![CDATA[4-5级]]>","low":"低温 1℃","fengxiang":"西北风","type":"晴"},{"date":"16日星期一","high":"高温 8℃","fengli":"<![CDATA[4-5级]]>","low":"低温 4℃","fengxiang":"西南风","type":"多云"},{"date":"17日星期二","high":"高温 15℃","fengli":"<![CDATA[4-5级]]>","low":"低温 9℃","fengxiang":"西北风","type":"多云"},{"date":"18日星期三","high":"高温 14℃","fengli":"<![CDATA[4-5级]]>","low":"低温 10℃","fengxiang":"南风","type":"晴"}],"ganmao":"昼夜温差大，风力较强，易发生感冒，请注意适当增减衣服，加强自我防护避免感冒。","wendu":"14"},"status":1000,"desc":"OK"}
E/EasyNet: EasyRequest:
    获取青岛天气->
    Id:0
    Url:http://wthrcdn.etouch.cn/weather_mini
    Type:GET
    Headers:HttpHeaders{headersMap={}}
    Params:city=[青岛]
    Json:null
```
# 重要步骤
#### 配置回调函数
```java
@BindEasyResponseMethod//一定要加上这个注解 函数名称可以不同，其他部分一定要保证一致
final public static void easyResponse(EasyNetApi requestItem, Response responseData, EasyListener listener){
	//在这里边通过requestItem.Id判断你请求的类型
	//Response中的body就是此次请求返回的数据
	//处理Response后通过调用EasyListener接口的onSuccess或onFailure返回处理数据
}
```
#### 初始化回调类
```java
//在接口调用前的任意位置
EasyNet.init(Api.class);//这个类一定要有BindEasyResponseMethod标注的回调函数，只能有一个
```

# Api详解
#### EasyNetApi包含请求描述，请求方式，请求URL的请求封装类

```java
/**
     *
     * @param Id API唯一ID
     * @param url 请求的目标URL
     * @param desc API说明文档
     * @param type 请求类型 POST GET PULL DELETE
     * @param params 请求头和请求参数描述信息
     * @param params	格式[[请求头部分],[请求参数部分]]
     * @param params	请求头部分："请求头key',"对这个key的说明信息" //只有一组，因为大多数只需要传一个Authorization
     * @param params    请求参数部分："请求参数key","对这个key的说明信息"//有0到若干组
     * params示例："Authorization","鉴权Token","username","手机号","password","密码"
     * params示例："Authorization","鉴权Token"
     * params示例：null,null,"username","手机号","password","密码"
     * params示例：什么也不写
     */
    public EasyNetApi(int Id, String url , String desc, EasyRequestType type, String...params);
```
#### EasyRequestConfig请求实例类

```java

	/**
     *
     * @param api 请求目标
     * @param values 请求参数对应api.Params
     * @param values 格式：[[请求头部分][请求参数部分]]
     * @param values [请求头部分]说明：请求头值//最多只有一组
     * @param values [请求参数部分]说明："请求key对应的value"//有0到若干组
     * @param values 示例："Bearer JF7890_fdskFDMNA_fd234F4fg_","18888888888","asd123"
     * @param values 示例："Bearer JF7890_fdskFDMNA_fd234F4fg_"
     * @param values 示例：null,"18888888888","asd123"
     * @param values 示例：什么也不写
     * 以上示例对应EasyNetApi
     */
    public EasyRequestConfig(EasyNetApi api, String...values)

	/**
     *
     * @param api
     * @param headers
     * @param params 该参数类型可为HttpParams或合法的JSONObject(String格式或JSONObject)
     */
    public EasyRequestConfig(EasyNetApi api, HttpHeaders headers, Object params)
```
