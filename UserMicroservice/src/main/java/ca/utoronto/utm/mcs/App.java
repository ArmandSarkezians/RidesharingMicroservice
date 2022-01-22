package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * This class creates the user microservice, accessed by the API Gateway
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/user/", new User());
      server.createContext("/user/register", new User());
      server.createContext("/user/login", new User());
      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
