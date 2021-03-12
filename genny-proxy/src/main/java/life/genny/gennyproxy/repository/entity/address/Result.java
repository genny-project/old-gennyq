
package life.genny.gennyproxy.repository.entity.address;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result implements Serializable {

    @SerializedName("address_components")
    @Expose
    @JsonProperty("address_components")
    private List<AddressComponent> addressComponents = null;

    @JsonProperty("formatted_address")
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @JsonProperty("place_id")
    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("plus_code")
    @Expose
    @JsonProperty("plus_code")
    private PlusCode plusCode;

    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("addressComponents", addressComponents).append("formattedAddress", formattedAddress).append("geometry", geometry).append("placeId", placeId).append("plusCode", plusCode).append("types", types).toString();
    }

}
