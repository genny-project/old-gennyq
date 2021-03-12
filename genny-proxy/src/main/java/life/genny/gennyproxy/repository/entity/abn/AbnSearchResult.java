package life.genny.gennyproxy.repository.entity.abn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AbnSearchResult implements Serializable {

  @JsonProperty("Message")
  @SerializedName("Message")
  @Expose
  private String message;

  @JsonProperty("Names")
  @SerializedName("Names")
  @Expose
  private List<CompanyInfo> names;

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


