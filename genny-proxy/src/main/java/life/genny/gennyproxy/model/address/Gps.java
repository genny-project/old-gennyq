package life.genny.gennyproxy.model.address;

import java.io.Serializable;

public class Gps implements Serializable {

    private double lat;

    private double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    private Gps(){

    }

    public static class Builder{

        private double lat;

        private double lng;

        public Builder(){
        }

        public Builder withLat(double lat){
            this.lat = lat;
            return this;
        }

        public Builder withLng(double lng){
            this.lng = lng;
            return this;
        }

        public Gps build(){
            Gps gps = new Gps();
            gps.lng = this.lng;
            gps.lat = this.lat;
            return gps;
        }
    }

    @Override
    public String toString(){
        return  lat+","+lng;
    }
}
