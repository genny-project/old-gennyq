package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.datatype.DataType;
import life.genny.models.validation.Validation;
import life.genny.models.validation.ValidationList;

public class DataTypeMarshaller implements MessageMarshaller<DataType> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.DataType";
  }

  @Override
  public Class<? extends DataType> getJavaClass() {
    return DataType.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,DataType d) throws IOException {
    writer.writeString("dttCode", d.getDttCode());
    writer.writeString("className", d.getClassName());
    writer.writeString("typeName", d.getTypeName());
    writer.writeString("inputmask", d.getInputmask());
    writer.writeCollection("validationList", d.getValidationList(), Validation.class);
  }

  @Override
  public DataType readFrom(ProtoStreamReader reader) throws IOException {
    ValidationList validationList = new ValidationList();
    validationList.setValidationList(
        reader.readCollection("validationList", new ArrayList<Validation>(), Validation.class));

    DataType d = new DataType(
        reader.readString("className"),
        validationList,
        reader.readString("typeName"),
        reader.readString("inputmask"));

    d.setDttCode(reader.readString("dttCode"));
    return d;
  }
}

