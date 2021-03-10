package life.genny.gennyproxy.mapper;

import io.netty.util.internal.StringUtil;
import life.genny.gennyproxy.model.address.AddressLookUp;
import life.genny.gennyproxy.model.address.AddressResp;
import life.genny.gennyproxy.model.address.Gps;
import life.genny.gennyproxy.repository.entity.address.Addresses;
import life.genny.gennyproxy.repository.entity.address.Result;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AddressRespMapper {

    public List<AddressResp> map(Addresses addresses) {

       return addresses
                .getResults()
                .stream()
                .map(result -> {

                    String fullAddress = result.getFormattedAddress();
                    Gps gps = buildGps(result);
                    AddressLookUp addressLookUp = buildAddressLookUp(result);

                    return new AddressResp
                            .Builder()
                            .withFullAddress(fullAddress)
                            .withAddressLookUp(addressLookUp)
                            .withGps(gps)
                            .build();

                }).collect(Collectors.toList());
    }

    private AddressLookUp buildAddressLookUp(Result result) {

          Map<String, String> valueMap = result
                  .getAddressComponents()
                  .stream()
                  .filter(addressComponent ->
                      addressComponent
                              .getTypes()
                              .stream()
                              .filter(t -> ComponentsMapping.hasProperty(t))
                          .count() > 0
                   )
                  .collect(Collectors.toMap(
                          addressComponent -> addressComponent
                                  .getTypes()
                                  .stream()
                                  .filter(t -> ComponentsMapping.hasProperty(t))
                                  .findFirst()
                                  .orElse(StringUtil.EMPTY_STRING),
                          addressComponent -> addressComponent.getLongName()
                  ));

        return new AddressLookUp
                .Builder()
                .withFullAddress(result.getFormattedAddress())
                .withCity(valueMap.get(ComponentsMapping.CITY.toString()))
                .withCountry(valueMap.get(ComponentsMapping.COUNTRY.toString()))
                .withPostCode(valueMap.get(ComponentsMapping.POSTCODE.toString()))
                .withState(valueMap.get(ComponentsMapping.STATE.toString()))
                .build();
    }

    enum ComponentsMapping{

        CITY("locality"),
        COUNTRY("country"),
        POSTCODE("postal_code"),
        STATE("administrative_area_level_1");

        private String googleResp;

        private ComponentsMapping(String googleResp) {
            this.googleResp = googleResp;
        }

        public static boolean hasProperty(String element){
           return  Arrays.asList(ComponentsMapping.values())
                   .stream()
                   .filter(e -> e.googleResp.equalsIgnoreCase(element))
                   .count() > 0;
        }

        @Override
        public String toString(){
            return this.googleResp;
        }
    }

    private Gps buildGps(Result result) {
        return new Gps
                .Builder()
                .withLat(result.getGeometry().getLocation().getLat())
                .withLng(result.getGeometry().getLocation().getLng())
                .build();
    }

}
