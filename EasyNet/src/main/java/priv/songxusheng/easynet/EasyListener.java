package priv.songxusheng.easynet;

public interface EasyListener {
    void onSuccess(int code,Object msg);
    void onFailure(int code,Object msg);
}
