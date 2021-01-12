package life.genny.utils;

import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.exception.BadDataException;

public class BaseEntityTestUtil {

  public final static String CODE = "XYZ_TEST";
  public final static String NAME = "testxyz";

  public final static BaseEntity baseEntityObject; 

  static {
    baseEntityObject = new BaseEntity(CODE,NAME);
    try {
      baseEntityObject.addAttribute(EntityAttributeTestUtil.entityAttributeObject);
    } catch (BadDataException e) {
      e.printStackTrace();
    }
  }
  
}
