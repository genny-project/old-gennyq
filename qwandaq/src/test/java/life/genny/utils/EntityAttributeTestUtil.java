package life.genny.utils;

import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;

public class EntityAttributeTestUtil {

  public final static EntityAttribute entityAttributeObject; 

  static {
    Attribute attribute = AttributeTestUtil.attributeObject;
    BaseEntity baseentity = BaseEntityTestUtil.baseEntityObject;
    entityAttributeObject = new EntityAttribute(baseentity,attribute,1.0,"just a name");
  }
  
}
