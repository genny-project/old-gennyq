package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.attribute.Attribute;
import life.genny.models.datatype.DataType;


public class AttributeMarshaller implements MessageMarshaller<Attribute> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.Attribute";
  }

  @Override
  public Class<? extends Attribute> getJavaClass() {
    return Attribute.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,Attribute a) throws IOException {
    writer.writeString("realm", a.realm);
    writer.writeString("code", a.code);
    writer.writeString("created", a.created.toString());
    writer.writeString("name", a.name);
    writer.writeObject("dataType",a.dataType,DataType.class);
    writer.writeBoolean("defaultPrivacyFlag",a.defaultPrivacyFlag);
    writer.writeString("description",a.description);
    writer.writeString("help",a.help);
    writer.writeString("placeholder",a.placeholder);
    writer.writeString("defaultValue",a.defaultValue);
  }

  @Override
  public Attribute readFrom(ProtoStreamReader reader) throws IOException {

    Attribute a = new Attribute(
        reader.readString("code"), 
        reader.readString("name"), 
        reader.readObject("dataType", DataType.class));

    a.realm = reader.readString("realm");
    a.created = LocalDateTime.parse(reader.readString("created"));
    a.defaultPrivacyFlag = reader.readBoolean("defaultPrivacyFlag");
    a.description = reader.readString("description");
    a.help = reader.readString("help");
    a.placeholder = reader.readString("placeholder");
    a.defaultValue = reader.readString("defaultValue");
    return a;
  }
}
