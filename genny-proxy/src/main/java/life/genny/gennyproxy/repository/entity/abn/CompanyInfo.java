package life.genny.gennyproxy.repository.entity.abn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CompanyInfo implements Serializable {

  @JsonProperty("Abn")
  @SerializedName("Abn")
  @Expose
  String abn;
  @JsonProperty("AbnStatus")
  @SerializedName("AbnStatus")
  @Expose
  String abnStatus;
  @JsonProperty("IsCurrent")
  @SerializedName("IsCurrent")
  @Expose
  boolean isCurrent;
  @JsonProperty("Name")
  @SerializedName("Name")
  @Expose
  String name;
  @JsonProperty("NameType")
  @SerializedName("NameType")
  @Expose
  String nameType;
  @JsonProperty("Postcode")
  @SerializedName("Postcode")
  @Expose
  String postcode;
  @JsonProperty("Score")
  @SerializedName("Score")
  @Expose
  String score;
  @JsonProperty("State")
  @SerializedName("State")
  @Expose
  String state;

  public CompanyInfo() {}

  public void setAbn(String abn) {
    this.abn = abn;
  }

  public String getAbn() {
    return abn;
  }

  public void setAbnStatus(String abnStatus) {
    this.abnStatus = abnStatus;
  }

  public String getAbnStatus() {
    return abnStatus;
  }

  public void setIsCurrent(boolean isCurrent) {
    this.isCurrent = isCurrent;
  }

  public boolean isCurrent() {
    return isCurrent;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  public String getNameType() {
    return nameType;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public String getScore() {
    return score;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  @Override
  public String toString() {
    return "CompanyInfo [abn=" + abn + ", abnStatus=" + abnStatus + ", isCurrent=" + isCurrent + ", name=" + name + ", nameType=" + nameType
        + ", postcode=" + postcode + ", score=" + score + ", state=" + state + "]";
  }

}
