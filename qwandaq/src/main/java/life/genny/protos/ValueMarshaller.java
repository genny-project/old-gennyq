package life.genny.protos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.infinispan.protostream.MessageMarshaller;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

import life.genny.models.Value;
import life.genny.models.datatype.DataType;


public class ValueMarshaller implements MessageMarshaller<Value> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.Value";
  }

  @Override
  public Class<? extends Value> getJavaClass() {
    return Value.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,Value v) throws IOException {
    writer.writeObject("dataType", v.dataType, DataType.class);
    writer.writeDouble("valueDouble", v.valueDouble);
    writer.writeInt("valueInteger", v.valueInteger);
    writer.writeLong("valueLong", v.valueLong);

    writer.writeString("valueDateTime",Optional.ofNullable(v.valueDateTime)
        .map(String::valueOf)
        .orElse(null));

    writer.writeString("valueDate",Optional.ofNullable(v.valueDate)
        .map(String::valueOf)
        .orElse(null));

    writer.writeString("valueTime",Optional.ofNullable(v.valueTime)
        .map(String::valueOf)
        .orElse(null));

    writer.writeBoolean("valueBoolean",v.valueBoolean);
    writer.writeString("valueString",v.valueString);
    writer.writeBoolean("expired",v.expired);
    writer.writeBoolean("refused",v.refused);
    writer.writeDouble("weight", v.weight);
  }

  @Override
  public Value readFrom(ProtoStreamReader reader) throws IOException {
    Value v = new Value();
    v.dataType = reader.readObject("dataType", DataType.class);
    v.valueDouble = reader.readDouble("valueDouble");
    v.valueInteger = reader.readInt("valueInteger");
    v.valueLong = reader.readLong("valueLong");
    v.valueBoolean = reader.readBoolean("valueBoolean");
    v.valueString = reader.readString("valueString");
    v.expired = reader.readBoolean("expired");
    v.refused = reader.readBoolean("refused");
    v.weight = reader.readDouble("weight");

    Optional.ofNullable(reader.readString("valueDateTime"))
      .map(LocalDateTime::parse)
      .ifPresent(d ->v.valueDateTime=d);

    Optional.ofNullable(reader.readString("valueDate"))
      .map(LocalDate::parse)
      .ifPresent(d ->v.valueDate=d);

    Optional.ofNullable(reader.readString("valueTime"))
      .map(LocalTime::parse)
      .ifPresent(d ->v.valueTime=d);
    return v;
  }
}

