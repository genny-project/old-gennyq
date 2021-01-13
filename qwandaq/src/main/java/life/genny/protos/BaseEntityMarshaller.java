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
    writer.writeCollection("baseEntityAttributes", b.baseEntityAttributes, EntityAttribute.class);
  }

  @Override
  public BaseEntity readFrom(ProtoStreamReader reader) throws IOException {
    String code = reader.readString("code");
    String name = reader.readString("name");
    String created = reader.readString("created");

    Set<EntityAttribute> baseEntityAttributes = reader.readCollection(
        "baseEntityAttributes",
        new HashSet<>(), 
        EntityAttribute.class);

    BaseEntity b = new BaseEntity(code, name);
    b.baseEntityAttributes = baseEntityAttributes;
    b.setCreated(LocalDateTime.parse(created));
    return b;
  }
}
