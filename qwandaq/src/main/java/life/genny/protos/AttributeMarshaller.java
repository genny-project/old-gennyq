package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.datatype.DataType;


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
    String realm = reader.readString("realm");
    String code = reader.readString("code");
    String created = reader.readString("created");
    String name = reader.readString("name");
    DataType dataType = reader.readObject("dataType", DataType.class);
    Boolean defaultPrivacyFlag = reader.readBoolean("defaultPrivacyFlag");
    String description = reader.readString("description");

    String help = reader.readString("help");
    String placeholder = reader.readString("placeholder");
    String defaultValue = reader.readString("defaultValue");
    Attribute a = new Attribute(code, name, dataType);
    a.realm = realm;
    a.defaultPrivacyFlag = defaultPrivacyFlag;
    a.description = description;
    a.help = help;
    a.placeholder = placeholder;
    a.defaultValue = defaultValue;
    a.created = LocalDateTime.parse(created);
    return a;
  }
}
