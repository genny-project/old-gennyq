package life.genny.abn;

import java.util.List;
import javax.json.bind.annotation.JsonbProperty;

public class AbnSearchResult {

  @JsonbProperty("Message")
  String message;

  @JsonbProperty("Names")
  List<CompanyInfo> names;

  public AbnSearchResult() {}

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setNames(List<CompanyInfo> names) {
    this.names = names;
  }

  public List<CompanyInfo> getNames() {
    return names;
  }

  @Override
  public String toString() {
    return "AbnSearchResult [message=" + message + ", names=" + names + "]";
  }

}


