package bigdata;

import com.jcraft.jsch.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin
@RestController
public class POIController {

    @GetMapping("")
    public String hello() {
        return "hello from back";
    }


    public static void listFolderStructure(String username, String password,
                                           String host, int port, String command) throws Exception {

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private static Session createSession(String user, String host, int port, String keyFilePath, String keyPassword) {
        try {
            JSch jsch = new JSch();

            if (keyFilePath != null) {
                if (keyPassword != null) {
                    jsch.addIdentity(keyFilePath, keyPassword);
                } else {
                    jsch.addIdentity(keyFilePath);
                }
            }

            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);
            session.setPassword(keyPassword);
            session.connect();

            return session;
        } catch (JSchException e) {
            System.out.println(e);
            return null;
        }


    }

    private static void copyRemoteToLocal(Session session, String from, String to, String fileName) throws JSchException, IOException {
        from = from + File.separator + fileName;
        String prefix = null;

        if (new File(to).isDirectory()) {
            prefix = to + File.separator;
        }

        // exec 'scp -f rfile' remotely
        String command = "scp -f " + from;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkAck(in);
            if (c != 'C') {
                break;
            }

            // read '0644 '
            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') break;
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }

            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            System.out.println("file-size=" + filesize + ", file=" + file);

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            FileOutputStream fos = new FileOutputStream(prefix == null ? to : prefix + file);
            int foo;
            while (true) {
                if (buf.length < filesize) foo = buf.length;
                else foo = (int) filesize;
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // error
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L) break;
            }

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            try {
                if (fos != null) fos.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        channel.disconnect();
        session.disconnect();
    }

    public static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    @GetMapping("/allType")
    public static List<String> getAllCategories() throws Exception {

        String request = ("'use ece_2020_fall_app_1; select type from calvo_monnier_projet_data_orc limit 20;'");
        String command = "hive --outputformat=csv2 -e " + request + " > categories.csv";
        listFolderStructure("g.monnier-ece", "ysyt2sk8Ph", "edge-1.au.adaltas.cloud", 22, command);
        System.out.println("Fin de connexion");
        String host = "edge-1.au.adaltas.cloud";
        String user = "g.monnier-ece";
        String keyPassword = "ysyt2sk8Ph";

        String remote = "";
        String local = "./src/data";
        String fileName = "categories.csv";

        int port = 22;

        String keyFilePath = null;

        Session session = createSession(user, host, port, keyFilePath, keyPassword);

        copyRemoteToLocal(session, remote, local, fileName);

        List<String> categories = new ArrayList<String>();


        File file = new File("./src/data/categories.csv");

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String tempArray[];

        int count = 0;

        while ((line = br.readLine()) != null) {
            if (count > 0) {

                //Obtention de la liste de categories
                int firstBracket = line.indexOf('[');
                int lastBracket = line.indexOf(']');
                String cat = line.substring(firstBracket, lastBracket);
                cat = cat.replace("\"", "");
                cat = cat.replace("[", "");
                cat = cat.replace("]", "");
                tempArray = cat.split(",");

                for (int i = 0; i < tempArray.length; i++) {
                    if (!categories.contains(tempArray[i])) {
                        categories.add(tempArray[i]);
                    }
                }

            }
            count++;
        }
        br.close();
        fr.close();

        return categories;
    }

    @GetMapping("/allName")
    public static List<String> getAllNames() throws Exception {

        String request = ("'use ece_2020_fall_app_1; select name from calvo_monnier_projet_data_orc;'");
        String command = "hive --outputformat=csv2 -e " + request + " > names.csv";
        listFolderStructure("g.monnier-ece", "ysyt2sk8Ph", "edge-1.au.adaltas.cloud", 22, command);
        System.out.println("Fin de connexion");
        String host = "edge-1.au.adaltas.cloud";
        String user = "g.monnier-ece";
        String keyPassword = "ysyt2sk8Ph";

        String remote = "";
        String local = "./src/data";
        String fileName = "names.csv";

        int port = 22;

        String keyFilePath = null;

        Session session = createSession(user, host, port, keyFilePath, keyPassword);

        copyRemoteToLocal(session, remote, local, fileName);

        List<String> names = new ArrayList<String>();


        File file = new File("./src/data/names.csv");

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        int count = 0;

        while ((line = br.readLine()) != null) {
            if (count > 0) {

                names.add(line);

            }
            count++;
        }
        br.close();
        fr.close();
        return names;
    }

    @GetMapping("/poiList")
    private static List<POI> getAllPoi() throws Exception {
        listFolderStructure("g.monnier-ece", "ysyt2sk8Ph", "edge-1.au.adaltas.cloud", 22, "hive --outputformat=csv2 -f projectHiveRequest.hql > requestOutputJava.csv");
        System.out.println("Fin de connexion");
        String host = "edge-1.au.adaltas.cloud";
        String user = "g.monnier-ece";
        String keyPassword = "ysyt2sk8Ph";

        String remote = "";
        String local = "./src/data";
        String fileName = "requestOutputJava.csv";

        int port = 22;

        String keyFilePath = null;

        Session session = createSession(user, host, port, keyFilePath, keyPassword);

        copyRemoteToLocal(session, remote, local, fileName);

        File file = new File("./src/data/requestOutputJava.csv");

        List<POI> pois = CsvToPoi(file);

        return pois;
    }

    private static List<POI> CsvToPoi(File file) throws Exception {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String tempArray[];

        String[] splitLine;
        List<POI> pois = new ArrayList<>();
        int count = 0;

        while ((line = br.readLine()) != null) {
            if (count > 0) {

                //Obtention de la liste de categories
                int firstBracket = line.indexOf('[');
                int lastBracket = line.indexOf(']');
                String categories = line.substring(firstBracket, lastBracket);
                categories = categories.replace("\"", "");
                categories = categories.replace("[", "");
                categories = categories.replace("]", "");
                tempArray = categories.split(",");
                List<String> cat = new ArrayList<String>();
                for (int i = 0; i < tempArray.length; i++) {
                    cat.add(tempArray[i]);
                }


                //obtention de l'id
                int firstComma = line.indexOf(",");
                String id = line.substring(0, firstComma);

                //Obtention du nom
                int secondComma = line.indexOf(",", firstComma + 1);
                String nom = line.substring(firstComma + 1, secondComma);

                line = line.substring(lastBracket + 2);
                splitLine = line.split(",");

                String comment = splitLine[0];
                String lastUpdate = splitLine[1];
                String reduceMobilityAccess = splitLine[2];
                String locality = splitLine[3];
                String postalCode = splitLine[4];
                String adresse = splitLine[5];
                String latitude = splitLine[6];
                String longitude = splitLine[7];

                Address address = new Address(locality, postalCode, adresse);

                POI poi = new POI(id, nom, cat, comment, lastUpdate, reduceMobilityAccess, address, latitude, longitude);


                pois.add(poi);

            }
            count++;
        }
        br.close();
        fr.close();

        return pois;
    }

    @GetMapping("/type")
    public static List<POI> requestByCategory(@RequestParam(value = "category", defaultValue = "") String category) throws Exception{

        String request = "'use ece_2020_fall_app_1; select * from calvo_monnier_projet_data_orc where array_contains(type, \"" + category + "\") limit 20;'";
        String command = "hive --outputformat=csv2 -e " + request + " > requestOutputJava.csv";
        listFolderStructure("g.monnier-ece", "ysyt2sk8Ph", "edge-1.au.adaltas.cloud", 22, command );
        System.out.println("Fin de connexion");
        String host = "edge-1.au.adaltas.cloud";
        String user = "g.monnier-ece";
        String keyPassword = "ysyt2sk8Ph";

        String remote = "";
        String local = "./src/data";
        String fileName = "requestOutputJava.csv";

        int port = 22;

        String keyFilePath = null;

        Session session = createSession(user, host, port, keyFilePath, keyPassword);

        copyRemoteToLocal(session, remote, local, fileName);

        File file = new File("./src/data/requestOutputJava.csv");

        List<POI> pois = CsvToPoi(file);

        return pois;

    }
}
