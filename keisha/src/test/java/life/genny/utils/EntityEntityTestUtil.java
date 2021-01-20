package life.genny.utils;

import life.genny.models.entity.EntityEntity;

public class EntityEntityTestUtil {

  public final static EntityEntity entityEntityObject;
  
  static {
    entityEntityObject = new EntityEntity(
        BaseEntityTestUtil.baseEntityObject,
        BaseEntityTestUtil.baseEntityObject,
        AttributeTestUtil.attributeObject, 
        1.0
        );
  }
}
