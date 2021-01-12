package life.genny.utils;

import life.genny.models.validation.Validation;

public class ValidationTestUtil {

  public final static String CODE = "XYZ_TEST";
  public final static String NAME = "testxyz";
  public final static String REGEX = ".*";

  public final static Validation validationObject = new Validation(CODE,NAME,REGEX);
  
}
