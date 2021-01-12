package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.SerializationUtils;
import org.infinispan.protostream.MessageMarshaller;

import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;


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
    writer.writeObject("baseentity", ba.baseentity, BaseEntity.class);
    writer.writeDouble("weight", ba.getWeight());
    writer.writeBytes("value", SerializationUtils.serialize(ba.getValue()));
    writer.writeBoolean("privacyFlag", ba.privacyFlag);
  }

  @Override
  public EntityAttribute readFrom(ProtoStreamReader reader) throws IOException {
    String realm = reader.readString("realm");
    String created = reader.readString("created");
    Attribute attribute = reader.readObject("attribute", Attribute.class);
    BaseEntity baseentity = reader.readObject("baseentity",BaseEntity.class);
    Double weight = reader.readDouble("weight");
    Object value = SerializationUtils.deserialize(reader.readBytes("value"));
    Boolean privacyFlag = reader.readBoolean("privacyFlag");
    EntityAttribute ba = new EntityAttribute(baseentity, attribute, weight, value);
    ba.privacyFlag = privacyFlag;
    ba.realm = realm;
    ba.created = LocalDateTime.parse(created);
    return ba;
  }
}
