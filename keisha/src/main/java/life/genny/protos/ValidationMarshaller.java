package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.validation.Validation;

public class ValidationMarshaller implements MessageMarshaller<Validation> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.Validation";
  }

  @Override
  public Class<? extends Validation> getJavaClass() {
    return Validation.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,Validation v) throws IOException {
    writer.writeString("name", v.name);
    writer.writeString("code", v.code);
    writer.writeString("regex", v.regex);
    writer.writeString("created", v.created.toString());
  }

  @Override
  public Validation readFrom(ProtoStreamReader reader) throws IOException {
    Validation v = new Validation(
        reader.readString("code"), 
        reader.readString("name"),
        reader.readString("regex"));

    v.created  = LocalDateTime.parse(reader.readString("created"));
    return v;
  }
}

