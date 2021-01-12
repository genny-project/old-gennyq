package life.genny.utils;

import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.entity.BaseEntity;

public class EntityAttributeTestUtil {

  public final static EntityAttribute entityAttributeObject; 

  static {
    Attribute attribute = AttributeTestUtil.attributeObject;
    BaseEntity baseentity = BaseEntityTestUtil.baseEntityObject;
    entityAttributeObject = new EntityAttribute(baseentity,attribute,1.0,"just a name");
  }
  
}
