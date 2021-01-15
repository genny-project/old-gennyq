package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.attribute.EntityAttribute;
import life.genny.models.entity.BaseEntity;

public class BaseEntityMarshaller implements MessageMarshaller<BaseEntity> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.BaseEntity";
  }

  @Override
  public Class<? extends BaseEntity> getJavaClass() {
    return BaseEntity.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,BaseEntity b) throws IOException {
    writer.writeString("name", b.getName());
    writer.writeString("code", b.getCode());
    writer.writeString("created", b.getCreated().toString());
    b.baseEntityAttributes.forEach(d -> d.baseEntityCode = b.code);
    writer.writeCollection("baseEntityAttributes", b.baseEntityAttributes, EntityAttribute.class);
  }

  @Override
  public BaseEntity readFrom(ProtoStreamReader reader) throws IOException {

    BaseEntity b = new BaseEntity(
        reader.readString("code"), 
        reader.readString("name"));

    b.setCreated(LocalDateTime.parse(reader.readString("created")));

    b.baseEntityAttributes = reader.readCollection(
        "baseEntityAttributes",
        new HashSet<>(), 
        EntityAttribute.class);

    return b;
  }
}
