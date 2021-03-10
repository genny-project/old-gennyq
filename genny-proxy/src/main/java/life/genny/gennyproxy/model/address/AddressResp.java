package life.genny.gennyproxy.model.address;

import java.io.Serializable;

public class AddressResp implements Serializable {

    private AddressLookUp addressLookUp;

    private String fullAddress;

    private Gps gps;

    private AddressResp(){

    }

    public static class Builder{

        private AddressLookUp addressLookUp;

        private String fullAddress;

        private Gps gps;

        public Builder(){
        }

        public Builder withAddressLookUp(AddressLookUp addressLookUp){
            this.addressLookUp = addressLookUp;
            return this;
        }

        public Builder withFullAddress(String fullAddress){
            this.fullAddress = fullAddress;
            return this;
        }

        public Builder withGps(Gps gps){
            this.gps = gps;
            return this;
        }

        public AddressResp build(){
            AddressResp addressResp = new AddressResp();
            addressResp.addressLookUp =  this.addressLookUp;
            addressResp.gps =  this.gps;
            addressResp.fullAddress =  this.fullAddress;
            return addressResp;
        }
    }

    public AddressLookUp getAddressLookUp() {
        return addressLookUp;
    }

    public String getFullAddress() {
        return fullAddress;
    }


    public Gps getGps() {
        return gps;
    }

    @Override
    public String toString() {
        return "AddressResp{" +
                "addressLookUp=" + addressLookUp +
                ", fullAddress='" + fullAddress + '\'' +
                ", gps=" + gps +
                '}';
    }
}
