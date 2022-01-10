import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TomDog {
    public static void main(String[] args) {
        TomDog tomDog = new TomDog();
        String resource = tomDog.getClass().getResource("/").getPath();
        System.out.println(resource);
    }
    private int port = 8080;
    private ServerSocket server;
    // 保存路径与Servlet的映射关系
    private Map<String,DogServlet> servletMapping = new HashMap<>();
    private Properties webxml = new Properties();

    private void init(){

        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF+"web.properties");
            webxml.load(fis);
            for(Object k : webxml.keySet()){
                String key = k.toString();
                //servlet.one.url=/firstServlet.do
                // servlet.one.className=com.xia.servlet.FirstServlet
                if(key.endsWith(".url")){//servlet.one.url
                    String servletName = key.replaceAll("\\.url$","");//servlet.one
                    String url = webxml.getProperty(key);// /firstServlet.do
                    String className = webxml.getProperty(servletName + ".className");//servlet.one.className
                    DogServlet servlet = (DogServlet) Class.forName(className).newInstance();
                    servletMapping.put(url,servlet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
