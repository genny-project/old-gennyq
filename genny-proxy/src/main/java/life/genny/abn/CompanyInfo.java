package life.genny.abn;

import javax.json.bind.annotation.JsonbProperty;

public class CompanyInfo {

  @JsonbProperty("Abn")
  String abn;
  @JsonbProperty("AbnStatus")
  String abnStatus;
  @JsonbProperty("IsCurrent")
  boolean isCurrent;
  @JsonbProperty("Name")
  String name;
  @JsonbProperty("NameType")
  String nameType;
  @JsonbProperty("Postcode")
  String postcode;
  @JsonbProperty("Score")
  String score;
  @JsonbProperty("State")
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
