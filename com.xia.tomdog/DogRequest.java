import java.io.IOException;
import java.io.InputStream;

public class DogRequest {

    private String method;
    private String url;

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    //request本质上是一个输入流
    public DogRequest(InputStream in){
        // 获取http中的请求信息
        String content = "";
        byte[] buffer = new byte[1024];
        int len;
        try {
            if((len = in.read(buffer)) > 0){
                content = new String(buffer);
            }
            //处理请求信息
            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");
            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
