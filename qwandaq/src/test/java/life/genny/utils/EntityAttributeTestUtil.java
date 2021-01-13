package life.genny.utils;

import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.exception.BadDataException;

public class EntityAttributeTestUtil {

  public final static EntityAttribute entityAttributeObject; 

  static {
    Attribute attribute = AttributeTestUtil.attributeObject;
    BaseEntity baseentity = BaseEntityTestUtil.baseEntityObject;
    entityAttributeObject = new EntityAttribute();
    entityAttributeObject.baseEntityCode = baseentity.code;
    entityAttributeObject.attribute = attribute;
    entityAttributeObject.setWeight(1.0);
    entityAttributeObject.setValue("just a name");
    try {
      //BaseEntityTestUtil.baseEntityObject.addAttribute(attribute);
      BaseEntityTestUtil.baseEntityObject.addAttribute(entityAttributeObject);
    } catch (BadDataException e) {
      e.printStackTrace();
    }
  }

}
