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
