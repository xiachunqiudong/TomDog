# TomDog
1. tomdog要负责接受http请求，所以需要一个 **DogRequest**
2. tomdog要负责返回响应，所以需要一个 **DogResponse**
3. tomdog要负责实例化Servlet，所以需要一个 **DogServlet** 规范（以及一个配置了映射关系的**配置文件**）

### DogRequest

Request 本质就是 InputStream，主要工作下：

1. 读取 InputStream 的内容，并保存
2. 对读取的结果进行解码，提取关键信息（URL，请求方式）

```java
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

```

### DogResponse

Response 本质就是 OutputStream，主要工作是：

1. 将 Servlet 处理结果编码成 http 协议格式
2. 通过 OutputStream 写出

```java
import java.io.IOException;
import java.io.OutputStream;

public class DogResponse {
    private OutputStream out;
    
    public DogResponse(OutputStream out){
        this.out = out;
    }
    
    public void write(String s) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 Ok\n")
                .append("Content-Type: text/html\n")
                .append("\r\n")
                .append(s);
        out.write(sb.toString().getBytes());
    }
}
```

### DogServlet

Servlet 就是一个应用于 web 的对象，具有规范作用。这里的 MYServlet 是一个抽象类，它的主要工作是：

1.提供Servlet规范，即每个处理业务逻辑的Servlet都要继承它，重写doPost和doGet这俩模板方法
		2.完成请求方式与对应方法的映射，对外提供统一方法 service，这里其实采用了模板方法模式

##### 模板方法模式

定义一个操作中的算法的框架，将一些步骤的实现延迟到子类中，这样子类可以在不改变算法的结构上，从而对一些特定的步骤进行实现。

```java
public abstact class Cook{
    public void cook(){
        System.out.println("start!");
        step1();
        step2();
        System.out.println("end!");
    }
    
    protected abstract void step1();
    protected abstract void step2();
}

class CookFood extends Cook{
    @override
    public void step1(){
        System.out.println("step1");
    }
    
    @override
    public void step2(){
        System.out.println("step2");
    }
}
```

```java
public abstract class DogServlet {

    // 抽象类实现一个整体的算法规划
    public void service(DogRequest request, DogResponse response) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    // 模板方法模式 具体的实现步骤交给子类去实现
    protected abstract void doGet(DogRequest request, DogResponse response);

    protected abstract void doPost(DogRequest request, DogResponse response);

}
```

### TomDog

**MYTomcat主要做了两件事：**

#### 1.初始化 tomcat：

- 加载web.properties文件，在这里其实相当于Tocmat中的web.xml
- 寻找url与servlet的映射关系，即对配置文件进行解析
- 将url与Servlet实例保存在Map中，到时可直接根据url获取到处理业务的servlet（单例模式）

#### 2.启动 tomcat：

- 调用init，目的是得到servletMapping的映射关系
- 通过BIO创建socket的服务端，在指定端口开始监听
- 用一个死循环持续等待并处理用户请求，处理用户请求的具体逻辑是：
- 获取IO流，并包装成Request与Response
- 获取请求URL，寻找相应Servlet进行处理。如果能找到就调用 servlet 的 service 方法进行处理；找不到就写出404。
- 等处理完之后关闭本次连接的相关资源
  
