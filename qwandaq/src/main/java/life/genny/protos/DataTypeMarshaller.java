package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.validation.Validation;
import life.genny.qwanda.validation.ValidationList;

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
    String dttCode = reader.readString("dttCode");
    String className = reader.readString("className");
    String typeName = reader.readString("typeName");
    ValidationList validationList = new ValidationList();

    validationList.setValidationList(
        reader.readCollection("validationList", new ArrayList<Validation>(), Validation.class));

    String inputmask = reader.readString("inputmask");
    DataType d = new DataType(className,validationList,typeName,inputmask);
    return d;
  }
}

