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

