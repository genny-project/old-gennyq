package life.genny.models;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.AttributeText;
import life.genny.models.message.QDataAttributeMessage;
import life.genny.models.validation.Validation;



public class AttributeJsonTest {
	
	private static final Logger log = Logger.getLogger(AttributeJsonTest.class);
	
	@Test
	public void attributeJsonTest()
	{
		Jsonb jsonb = JsonbBuilder.create();
		
		Attribute a = new AttributeText("PRI_TEXT","Text");
		Attribute b = new AttributeText("PRI_TEXT2","Text2");
		
		Attribute[] aa = new Attribute[2];
		aa[0]= a;
		aa[1] = b;
		
		QDataAttributeMessage msg = new QDataAttributeMessage(aa);
		
		String ja = jsonb.toJson(msg);
		
		Validation vv = new Validation("VLD_TEST", "Test Validation", ".*");
		String validationJson = jsonb.toJson(vv);
		System.out.println("Validation json = "+validationJson);
		
		try {
			 Validation validation2 = jsonb.fromJson(validationJson, Validation.class);
			 System.out.println(validation2);

      } catch (Exception e) {
//      	     log.error("The JSON file received is  :::  "+json);;
           log.error("Bad Deserialisation ");
      }
		
		
		String vjson = String.join("\n", "{",
	            "\"regex\": \".*\",",
	            "\"selectionBaseEntityGroupList\": [",
	              "\"\"",
	            "],",
	            "\"recursiveGroup\": false,",
	            "\"multiAllowed\": false,",
	            "\"code\": \"VLD_ANYTHING\",",
	            "\"index\": 0,",
	            "\"created\": \"2020-08-25T09:34:56.970Z\",",
	            "\"name\": \"Anything\",",
	            "\"realm\": \"genny\"",
	          "}");
		
		 try {
			 Validation validation = jsonb.fromJson(vjson, Validation.class);
			 log.info(validation);
 
       } catch (Exception e) {
//       	     log.error("The JSON file received is  :::  "+json);;
            log.error("Bad Deserialisation ");
       }
		Validation validation = jsonb.fromJson(vjson, Validation.class);
		log.info(validation);

		
		String json = String.join("\n",
		      "{ \"dataType\": {",
		        "\"dttCode\": \"DTT_NAME\",",
		        "\"className\": \"text\",",
		        "\"typeName\": \"text\",",
		        "\"validationList\": [",
		          "{",
		            "\"regex\": \".*\",",
		            "\"selectionBaseEntityGroupList\": [",
		              "\"\"",
		            "],",
		            "\"recursiveGroup\": false,",
		            "\"multiAllowed\": false,",
		            "\"code\": \"VLD_ANYTHING\",",
		            "\"index\": 0,",
		            "\"created\": \"2020-08-25T09:34:56.979Z\",",
		            "\"name\": \"Anything\",",
		            "\"realm\": \"genny\"",
		          "}",
		        "]",
		      "}",
		      "}");
		
		
		Attribute attribute = jsonb.fromJson(json, Attribute.class);
		log.info(attribute);
	}
}
