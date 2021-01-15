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
    writer.writeDouble("weight", ba.getWeight());
    writer.writeObject("value", ba.value, Value.class);
    writer.writeBoolean("privacyFlag", ba.privacyFlag);
    writer.writeString("baseEntityCode", ba.baseEntityCode);
    writer.writeString("attributeCode", ba.attributeCode);
  }

  @Override
  public EntityAttribute readFrom(ProtoStreamReader reader) throws IOException {
    EntityAttribute ba = new EntityAttribute();
    ba.realm = reader.readString("realm");
    ba.created = LocalDateTime.parse(reader.readString("created"));
    ba.attributeCode = reader.readString("attributeCode");
    ba.baseEntityCode = reader.readString("baseEntityCode");
    ba.attribute = reader.readObject("attribute", Attribute.class);
    ba.setWeight(reader.readDouble("weight"));
    ba.value = reader.readObject("value",Value.class);
    ba.privacyFlag = reader.readBoolean("privacyFlag");
    return ba;
  }
}
