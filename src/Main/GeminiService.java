package Main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A service to connect to Gemini.
 */
public class GeminiService {

    String API_KEY = System.getenv("GEMINI_API_KEY");
    HttpClient client;
    String url;

    public GeminiService() {
        client = HttpClient.newHttpClient();
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    }


        // URL for connection

        HttpRequest request;
        HttpResponse<String> response;

        public void generateRequest(String text) {
          request  = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(generateBody(text)))
                    .build();

        }

        public String sendResponse() throws IOException, InterruptedException {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            int start = responseBody.indexOf("\"text\": \"");

            // In JSON "text": " -> Our text is 9 characters from start so start+9.
            int end = responseBody.indexOf("\"", start+9);

            return responseBody.substring(start+9, end);
        }

        public String generateBody(String text) {
            return """
                {
                 "contents": [
                   {
                     "parts": [
                       {
                         "text": "%s"
                }
                     ]
                   }
                  ]
               } 
               """.formatted("Summarize the following text:\n"+text);
        }

    }



