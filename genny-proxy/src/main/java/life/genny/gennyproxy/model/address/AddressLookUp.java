package life.genny.gennyproxy.model.address;

public class AddressLookUp {

    private String fullAddress;
    private String city;
    private String state;
    private String postCode;
    private String country;

    private AddressLookUp(){

    }

    public static class Builder{

        private String fullAddress;
        private String city;
        private String state;
        private String postCode;
        private String country;

        public Builder(){
        }

        public Builder withFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }
        public Builder withCity(String city) {
            this.city = city;
            return this;
        }
        public Builder withState(String state) {
            this.state = state;
            return this;
        }
        public Builder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public AddressLookUp build(){
            AddressLookUp addressLookUp = new AddressLookUp();
            addressLookUp.fullAddress = this.fullAddress ;
            addressLookUp.city = this.city ;
            addressLookUp.state = this.state ;
            addressLookUp.postCode = this.postCode ;
            addressLookUp.country = this.country ;
            return addressLookUp;
        }
    }

    public String getFullAddress() {
        return fullAddress;
    }


    public String getCity() {
        return city;
    }


    public String getState() {
        return state;
    }


    public String getPostCode() {
        return postCode;
    }


    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "AddressLookUp{" +
                "fullAddress='" + fullAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
