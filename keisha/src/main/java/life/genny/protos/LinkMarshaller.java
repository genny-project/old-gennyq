package life.genny.protos;

import java.io.IOException;
import java.time.LocalDateTime;

import org.infinispan.protostream.MessageMarshaller;

import life.genny.models.Link;


public class LinkMarshaller implements MessageMarshaller<Link> {

  @Override
  public String getTypeName() {
    return "baseentity_schema.Link";
  }

  @Override
  public Class<? extends Link> getJavaClass() {
    return Link.class;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer,Link l) throws IOException {
    writer.writeString("attributeCode", l.attributeCode);
    writer.writeString("targetCode", l.targetCode);
    writer.writeString("sourceCode", l.sourceCode);
    writer.writeString("linkValue", l.linkValue);
    writer.writeDouble("weight", l.weight);
    writer.writeString("childColor", l.childColor);
    writer.writeString("parentColor", l.parentColor);
    writer.writeString("rule", l.rule);
  }

  @Override
  public Link readFrom(ProtoStreamReader reader) throws IOException {
    Link l = new Link();
    l.attributeCode = reader.readString("attributeCode");
    l.targetCode = reader.readString("targetCode");
    l.sourceCode = reader.readString("sourceCode");
    l.linkValue = reader.readString("linkValue");
    l.weight = reader.readDouble("weight");
    l.childColor = reader.readString("childColor");
    l.parentColor = reader.readString("parentColor");
    l.rule = reader.readString("rule");
    return l;
  }
}

