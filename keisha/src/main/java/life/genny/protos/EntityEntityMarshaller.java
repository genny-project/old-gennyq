package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.SerializationUtils;
import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.Link;
import life.genny.models.Value;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;


public class EntityEntityMarshaller implements MessageMarshaller<EntityEntity> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.EntityEntity";
  }

  @Override
  public Class<? extends EntityEntity> getJavaClass() {
    return EntityEntity.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,EntityEntity ee) throws IOException {
    writer.writeString("realm", ee.realm);
    writer.writeString("created", ee.created.toString());
    writer.writeObject("attribute", ee.attribute, Attribute.class);
    writer.writeString("attributeCode",ee.attributeCode);
    writer.writeString("sourceCode", ee.sourceCode);
    writer.writeString("targetCode", ee.targetCode);
    writer.writeObject("value", ee.value, Value.class);
    writer.writeObject("link", ee.link, Link.class);
  }

  @Override
  public EntityEntity readFrom(ProtoStreamReader reader) throws IOException {
    EntityEntity ee = new EntityEntity();
    ee.realm = reader.readString("realm");
    ee.created = LocalDateTime.parse(reader.readString("created"));
    ee.attribute = reader.readObject("attribute", Attribute.class);
    ee.attributeCode = reader.readString("attributeCode");
    ee.sourceCode = reader.readString("sourceCode");
    ee.targetCode = reader.readString("targetCode");
    ee.value = reader.readObject("value", Value.class);
    ee.link = reader.readObject("link", Link.class);
    return ee;
  }
}
