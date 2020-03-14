package priv.songxusheng.easynet;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import priv.songxusheng.easyjson.ESON;

public class EasyRequestConfig {
    JSONObject jsonParams;
    HttpHeaders headers;
    HttpParams params;
    EasyNetApi api;

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
    public EasyRequestConfig(EasyNetApi api, String...values){
        this.api = api;
        headers = new HttpHeaders();
        params = new HttpParams();
        if(api==null) return;
        String info[] = api.Params;
        if(values!=null&&values.length>1&&info!=null&&info.length>0){
            if(values[0]!=null&&info[0]!=null){
                headers.put(info[0],values[0]);
            }
            for(int i=2,j=1,ni=info.length,nj=values.length;i<ni&&j<nj;i+=2,++j){
                if(info[i]!=null){
                    params.put(info[i],values[j]);
                }
            }
        }
    }

    /**
     *
     * @param api
     * @param headers
     * @param params 该参数类型可为HttpParams或合法的JSONObject(String格式或JSONObject)
     */
    public EasyRequestConfig(EasyNetApi api, HttpHeaders headers, Object params){
        this.api = api;
        this.headers = headers;
        if(params instanceof HttpParams){
            this.params = (HttpParams) params;
        }
        else{
            jsonParams = ESON.getJSONObject(params);
            this.params = new HttpParams();
            try {
                Map<String, String> map = new LinkedHashMap<String, String>();
                Iterator<String> it = jsonParams.keys();
                while(it.hasNext()){
                    String key  = it.next();
                    map.put(key, ESON.getJSONValue(jsonParams,key,""));
                }
                this.params.put(map);
            } catch (Exception e) {}
        }
        if(headers==null) headers = new HttpHeaders();
        if(params == null) params = new HttpParams();
    }

    @Override
    public String toString() {
        return String.format("%s\nHeaders:%s\nParams:%s\nJson:%s",api,headers,params,jsonParams);
    }
}
