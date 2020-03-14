package priv.songxusheng.easynet;

public class EasyNetApi {
    public int Id;//常量类型值 用于区别请求的功能
    public EasyRequestType Type;//请求类型 GET,POST,PUT,DELETE;
    public String Url;//请求的网页链接
    public String Desc;//请求描述
    public String[] Params = null;//参数 每两个一组 格式：参数名，参数描述

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
    public EasyNetApi(int Id, String url , String desc, EasyRequestType type, String...params){
        this.Id = Id;
        this.Url = url;
        this.Desc = desc;
        this.Type = type;
        this.Params = params;
    }

    @Override
    public String toString() {
        return String.format("%s->\nId:%d\nUrl:%s\nType:%s",Desc,Id,Url,Type.toString());
    }
}