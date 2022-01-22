package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

/**
 * This is a utility class
 *
 * @version 1.0
 * @author Armand Sarkezians
 */
public class Utils {
   /**
    * This function converts an inputStream to a string
    *
    * @param inputStream the input stream being converted
    * @return the converted string
    * @throws IOException in case of error
    */
   public static String convert(InputStream inputStream) throws IOException {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
         return br.lines().collect(Collectors.joining(System.lineSeparator()));
      }
   }

   /**
    * This function gets the body of an HTTPExchange
    *
    * @param r The HTTPExchange
    * @return the body
    * @throws IOException in case of error
    * @throws JSONException in case of error
    */
   public static JSONObject getBody (HttpExchange r) throws IOException, JSONException {
      String reqBody = Utils.convert(r.getRequestBody());
      if (reqBody.length() > 0){
         return new JSONObject(reqBody);
      }
      return new JSONObject("{}");
   }

   /**
    * This function sends a request to a microservice
    *
    * @param url the microservice which will get a request
    * @param method the method of request (GET, PATCH, etc)
    * @param body the body of the request
    * @return the response of the request
    * @throws IOException in case of error
    * @throws InterruptedException in case of error
    */
   public static HttpResponse<String> sendRequest(String url, String method, String body) throws IOException, InterruptedException{
      HttpClient client = HttpClient.newHttpClient();
      System.out.println(url);
      HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("accept", "application/json")
              .method(method, HttpRequest.BodyPublishers.ofString(body)).build();
      return client.send(req, HttpResponse.BodyHandlers.ofString());
   }
}
