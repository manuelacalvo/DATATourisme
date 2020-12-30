package bigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.servlet.http.HttpServlet;

@SpringBootApplication
public class App extends HttpServlet {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}