package life.genny.utils;

import java.util.Arrays;

import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.validation.ValidationList;

public class DataTypeTestUtil {

  public final static String CLASS_NAME = String.class.toString();

  public final static DataType dataTypeObject = new DataType(
      CLASS_NAME,
      new ValidationList(Arrays.asList(ValidationTestUtil.validationObject)),
      CLASS_NAME
      );
  
}
