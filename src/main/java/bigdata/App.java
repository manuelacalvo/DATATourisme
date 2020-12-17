package bigdata;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

public class App extends HttpServlet {
    /*public static void main(String[] args) {
        System.out.println("Hello world");

      /*  System.setProperty("jxbrowser.license.key", "1BNDHFSC1FXIGGJYF9F4KBZ8OK8WGKX1L98J37OSSMY7BEZR99ZCOKPLV0W0OQTHMDQHYH");

        // Creating and running Chromium engine
        EngineOptions options =
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build();
        Engine engine = Engine.newInstance(options);
        Browser browser = engine.newBrowser();


        SwingUtilities.invokeLater(() -> {
            // Creating Swing component for rendering web content
            // loaded in the given Browser instance.
            BrowserView view = BrowserView.newInstance(browser);

            // Creating and displaying Swing app frame.
            JFrame frame = new JFrame("Hello World");
            // Close Engine and onClose app window
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JTextField addressBar = new JTextField("C:/Users/CALVO Manuela/Desktop/cours/ING5/big_data/projet/src/main/java/bigdata/map.html");
            addressBar.addActionListener(e ->
                    browser.navigation().loadUrl(addressBar.getText()));
            frame.add(addressBar, BorderLayout.NORTH);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            browser.navigation().loadUrl(addressBar.getText());
        });
    } */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().print("Hello from Java!\n");

    }

    public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new App()),"/*");
        server.start();
        server.join();

      /*  try (BufferedInputStream in = new BufferedInputStream(new URL("https://diffuseur.datatourisme.gouv.fr/webservice/d02edf6818f4f2348d9baf3dca344396/fad8f97d-fd0a-4cd5-8a4a-c00d968bc3f6").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("flux-8827-202012120213.csv")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        } */
       //  https://diffuseur.datatourisme.gouv.fr/webservice/d02edf6818f4f2348d9baf3dca344396/fad8f97d-fd0a-4cd5-8a4a-c00d968bc3f6
    }

}