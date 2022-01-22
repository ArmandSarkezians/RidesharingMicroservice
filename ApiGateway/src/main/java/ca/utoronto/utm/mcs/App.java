package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This class creates the proxy server which acts as the API Gateway, and creates the contexts for each endpoint
 * @version 1.0
 * @author Armand Sarkezians
 */
public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/location", new Endpoint("http://locationmicroservice:8000"));
      server.createContext("/user", new Endpoint("http://usermicroservice:8000"));
      server.createContext("/trip", new Endpoint("http://tripinfomicroservice:8000"));
      server.start();

      System.out.printf("Server started on port %d...\n", PORT);
   }
}
