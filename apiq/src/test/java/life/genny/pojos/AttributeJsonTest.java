package life.genny.pojos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import life.genny.qwanda.DateTimeDeserializer;
import org.jboss.logging.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.quarkus.test.junit.QuarkusTest;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.AttributeText;
import life.genny.qwanda.datatype.LocalDateConverter;
import life.genny.qwanda.message.QDataAttributeMessage;
import life.genny.qwanda.validation.Validation;

@QuarkusTest
public class AttributeJsonTest {
	
	private static final Logger log = Logger.getLogger(AttributeJsonTest.class);
	
	static GsonBuilder gsonBuilder = new GsonBuilder();       

	static public Gson gson = gsonBuilder
			.registerTypeAdapter(LocalDateTime.class, new DateTimeDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateConverter())
		//	.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
		//	.excludeFieldsWithoutExposeAnnotation()
		//    .disableHtmlEscaping()
		    .setPrettyPrinting()
			.create();
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
		
		String vjson = String.join("\n", "{",
	            "\"regex\": \".*\",",
	            "\"selectionBaseEntityGroupList\": [",
	              "\"\"",
	            "],",
	            "\"recursiveGroup\": false,",
	            "\"multiAllowed\": false,",
	            "\"code\": \"VLD_ANYTHING\",",
	            "\"index\": 0,",
	            "\"created\": \"2020-08-25T09:34:56.979\",",
	            "\"name\": \"Anything\",",
	            "\"realm\": \"genny\"",
	          "}");
		
		 try {
			 Validation validation = gson.fromJson(vjson, Validation.class);
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
		            "\"created\": \"2020-08-25T09:34:56.979\",",
		            "\"name\": \"Anything\",",
		            "\"realm\": \"genny\"",
		          "}",
		        "]",
		      "}");
		
		
		Attribute attribute = jsonb.fromJson(json, Attribute.class);
		log.info(attribute);
	}
}
