package life.genny.abn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class AbnLookup {

  static final String UTF_8 = "UTF-8";
  static final String callbackName = "c";

  static final String ABN_KEY_FILE_PATH = "/tmp/abn-key";
  static Predicate<String> isNotEmpty = a -> !a.isEmpty();

  static Supplier<String> readKeyFromFileSystem = () -> {
    String data = "";
    File myObj = new File(ABN_KEY_FILE_PATH);
    try (Scanner myReader = new Scanner(myObj)){
      data = myReader.nextLine();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return data;
  };

  static final String abnKey = Optional.ofNullable(System.getenv("ABN_KEY"))
      .filter(Objects::nonNull)
      .filter(isNotEmpty)
      .orElseGet(readKeyFromFileSystem);
  
  static String getCallbackJson(String callbackResult) {
    String jsonRes = callbackResult.substring(callbackName.length() + 1, callbackResult.length() - 1);
    return jsonRes;
  }

  public static AbnSearchResult searchByName(String searchedName, int pageSize)
      throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
    AbnSearchResult results = null;

    String params = "";

    params += "callback=" + URLEncoder.encode(callbackName, UTF_8);

    params += "&name=" + URLEncoder.encode(searchedName, UTF_8);
    params += "&maxResults=" + pageSize;

    params += "&guid=" + URLEncoder.encode(abnKey, UTF_8);

    results = doRequest("ABRSearchByNameAdvancedSimpleProtocol", params);

    return results;
  }

  static AbnSearchResult doRequest(String service, String parameters)
      throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
    AbnSearchResult res = null;

    URL url = new URL("https://abr.business.gov.au/json/MatchingNames.aspx?" + parameters);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    System.out.println(url);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "text/javascript; charset-utf-8");
    connection.connect();
    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
      BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line = streamReader.readLine();
      String jsonRes = getCallbackJson(line);
      Jsonb jsonb = JsonbBuilder.create();
      res = jsonb.fromJson(jsonRes, AbnSearchResult.class);
      System.out.println(res);
    }

    connection.disconnect();

    return res;
  }

}
