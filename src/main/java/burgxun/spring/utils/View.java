package burgxun.spring.utils;

/**
 * @ClassName View
 * @Auther burgxun
 * @Description:
 * @Date 2020/6/1 15:06
 **/
public class View {

    private String url;
    private String dispatchAction = "forward";

    public View(String url) {
        this.url = url;
    }

    public String getDispatchAction() {
        return dispatchAction;
    }

    public void setDispatchAction(String dispatchAction) {
        this.dispatchAction = dispatchAction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

