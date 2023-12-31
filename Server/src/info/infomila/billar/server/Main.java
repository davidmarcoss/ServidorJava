package info.infomila.billar.server;

import info.infomila.billar.ipersistence.BillarException;
import info.infomila.billar.ipersistence.BillarFactory;
import info.infomila.billar.ipersistence.IBillar;
import info.infomila.billar.models.Soci;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main
{
    public static Map<String, Soci> usersMap = new HashMap<>();

    public static void main(String[] args)
    {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("config.properties"));
        } catch (IOException ex) {
            System.out.println("Error en llegir el fitxer de propietats: " + ex);
            System.exit(0);
        }
        
        String className = p.getProperty("className");
        if (className == null || className.length() == 0) {
            System.out.println("Error en llegir la propietat className");
            System.exit(0);
        }
        
        String hibernateConfig = p.getProperty("hibernateConfig");
        if (hibernateConfig == null || hibernateConfig.length() == 0) {
            System.out.println("Error en llegir la propietat hibernateConfig");
            System.exit(0);
        }
        
        IBillar billar = null;
        try {
            billar = BillarFactory.getInstance(className, hibernateConfig);
        } catch (BillarException ex) {
            System.out.println("Error en crear objecte de capa de persistència");
            System.out.println("Més informació: " + ex.getMessage());
            ex.printStackTrace();
            if (ex.getCause() != null) {
                System.out.println("Causat per: " + ex.getCause().getMessage());
            }
            System.exit(1);
        }

        int portServer = 1237;
        try {
            InetAddress addr = InetAddress.getByName("192.168.1.150");
            ServerSocket sockServer = new ServerSocket(portServer, 50, addr);

            System.out.println("Server iniciat");

            while (true) {
                Socket newSock = sockServer.accept();
                System.out.println("Client Acceptat");
                Thread thread = new ThreadHandler(newSock, billar);
                thread.start();
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
