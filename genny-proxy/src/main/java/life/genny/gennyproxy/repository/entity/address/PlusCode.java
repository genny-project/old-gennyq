
package life.genny.gennyproxy.repository.entity.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class PlusCode implements Serializable {

    @JsonProperty("compound_code")
    @SerializedName("compound_code")
    @Expose
    private String compoundCode;

    @JsonProperty("global_code")
    @SerializedName("global_code")
    @Expose
    private String globalCode;

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public String getGlobalCode() {
        return globalCode;
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("compoundCode", compoundCode).append("globalCode", globalCode).toString();
    }

}
