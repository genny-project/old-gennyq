package life.genny.utils;

import java.util.Optional;

import life.genny.models.Link;
import life.genny.models.Value;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.models.validation.Validation;

public class KeyConvertion {

  public static String convertObjectToKey(EntityAttribute ea){
    String attributeCode = Optional.ofNullable(ea.attributeCode).orElse(ea.attribute.code);
    return ea.baseEntityCode.concat(attributeCode);
  }
    
  public static String convertObjectToKey(EntityEntity ee){
    return ee.sourceCode.concat(ee.targetCode).concat(ee.attributeCode);
  }

  public static String convertObjectToKey(Link l){
    return l.sourceCode.concat(l.targetCode);
  }

  public static String convertObjectToKey(Attribute a){
    return a.code;
  }

  public static String convertObjectToKey(Validation v){
    return v.code;
  }

  public static String convertObjectToKey(Value v){
    return v.dataType.getDttCode();
  }

  public static String convertObjectToKey(BaseEntity b) {
    return b.code;
  }

  public static String convertObjectToKey(DataType d) {
    return d.getDttCode();
  }
}
