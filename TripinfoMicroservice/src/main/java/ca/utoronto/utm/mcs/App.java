package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This class creates the trip microservice
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/trip/request", new Request());
      server.createContext("/trip/confirm", new Confirm());
      server.createContext("/trip", new Confirm());
      server.createContext("/trip/passenger", new Passenger());
      server.createContext("/trip/driver", new Driver());
      server.createContext("/trip/driverTime", new DriverTime());
      server.start();

      System.out.printf("Server started on port %d...\n", PORT);
   }
}
