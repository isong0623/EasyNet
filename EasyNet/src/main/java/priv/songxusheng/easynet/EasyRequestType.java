package priv.songxusheng.easynet;

public enum EasyRequestType {
    GET(0),POST(1),DELETE(2),PUT(3);
    public int Code;
    EasyRequestType(int code){
        Code = code;
    }

    @Override
    public String toString() {
        return new String[]{"GET","POST","DELETE","PUT"}[Code];
    }
}