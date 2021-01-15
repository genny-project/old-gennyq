package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.SerializationUtils;
import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.Value;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.entity.BaseEntity;


public class EntityAttributeMarshaller implements MessageMarshaller<EntityAttribute> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.EntityAttribute";
  }

  @Override
  public Class<? extends EntityAttribute> getJavaClass() {
    return EntityAttribute.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,EntityAttribute ba) throws IOException {
    writer.writeString("realm", ba.realm);
    writer.writeString("created", ba.created.toString());
    writer.writeObject("attribute", ba.attribute, Attribute.class);
    //writer.writeObject("baseentity", ba.baseentity, BaseEntity.class);
    writer.writeDouble("weight", ba.getWeight());
    writer.writeObject("value", ba.value, Value.class);
    writer.writeBoolean("privacyFlag", ba.privacyFlag);
    writer.writeString("baseEntityCode", ba.baseEntityCode);
    writer.writeString("attributeCode", ba.attributeCode);
  }

  @Override
  public EntityAttribute readFrom(ProtoStreamReader reader) throws IOException {
    String realm = reader.readString("realm");
    String created = reader.readString("created");
    String attributeCode = reader.readString("attributeCode");
    String baseEntityCode = reader.readString("baseEntityCode");
    Attribute attribute = reader.readObject("attribute", Attribute.class);
    //BaseEntity baseentity = reader.readObject("baseentity",BaseEntity.class);
    Double weight = reader.readDouble("weight");
    Value value = reader.readObject("value",Value.class);
    Boolean privacyFlag = reader.readBoolean("privacyFlag");
    EntityAttribute ba = new EntityAttribute();
    ba.attribute = attribute;
    ba.baseEntityCode = baseEntityCode;
    ba.setWeight(weight);
    ba.value = value;
    ba.privacyFlag = privacyFlag;
    ba.realm = realm;
    ba.created = LocalDateTime.parse(created);
    return ba;
  }
}
