package life.genny.protos;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.protostream.MessageMarshaller;


@ApplicationScoped
public class MarshallerConfiguration {
  @Produces
  MessageMarshaller baseEntityMarshaller() {
    return new BaseEntityMarshaller();
  }
  @Produces
  MessageMarshaller validationMarshaller() {
    return new ValidationMarshaller();
  }
  @Produces
  MessageMarshaller dataTypeMarshaller() {
    return new DataTypeMarshaller();
  }
  @Produces
  MessageMarshaller attributeMarshaller() {
    return new AttributeMarshaller();
  }
  @Produces
  MessageMarshaller entityAttributeMarshaller() {
    return new EntityAttributeMarshaller();
  }
  @Produces
  MessageMarshaller valueMarshaller() {
    return new ValueMarshaller();
  }
  @Produces
  MessageMarshaller linkMarshaller() {
    return new LinkMarshaller();
  }
  @Produces
  MessageMarshaller entityEntityMarshaller() {
    return new EntityEntityMarshaller();
  }
}
