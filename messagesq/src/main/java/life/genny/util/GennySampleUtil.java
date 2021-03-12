package life.genny.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import life.genny.models.entity.BaseEntity;
import life.genny.models.message.QBaseMSGMessageType;
import life.genny.models.message.QMessageGennyMSG;
import life.genny.qwanda.Answer;
import life.genny.qwandautils.JsonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GennySampleUtil {

    public static BaseEntity createSuperHeroBE(){
        BaseEntity superHero = new BaseEntity("PER_SUPER_HERO","James Bond");
        return superHero;
    }

    public static BaseEntity createEvilBE(){
        BaseEntity evil = new BaseEntity("PER_BAD_GUY","Dr Evil");
        return evil;
    }

    public static BaseEntity createTemplateBE(){
        String templateCode = "MSG_GENNY_INVITE";
        BaseEntity msgTemplate = new BaseEntity(templateCode,"Test message");
        return msgTemplate;
    }

    public static String createSamplePayload(String token, BaseEntity baseEntity){
        String msg_type = "MSG_MESSAGE";
        Map<String, String> contextMap = new HashMap<String, String>();

        contextMap.put("SUPER_HERO", "PER_SUPER_HERO");
        contextMap.put("BAD_GUY", "PER_BAD_GUY");
        contextMap.put("USER", "PER_SUPER_HERO");
        contextMap.put("PROJECT", "PRJ_INTERNMATCH");
        String templateCode = "MSG_GENNY_INVITE";


        String[] recipientArr = new String[] { baseEntity.getCode() };
        QBaseMSGMessageType messageType=QBaseMSGMessageType.SMS;
        QBaseMSGMessageType[] messageTypes= new QBaseMSGMessageType[]{QBaseMSGMessageType.SMS, QBaseMSGMessageType.EMAIL};
        QMessageGennyMSG qMessageGennyMSG = new QMessageGennyMSG(msg_type, messageType, templateCode, contextMap, recipientArr, messageTypes);
        qMessageGennyMSG.setToken(token);
        System.out.println(JsonUtils.toJson(qMessageGennyMSG));
        return JsonUtils.toJson(qMessageGennyMSG);
    }

    public  static List<Answer> createAnswers(BaseEntity evil, BaseEntity superHero, BaseEntity msgTemplate){

        List<Answer> answers = new ArrayList<Answer>();

        answers.add(new Answer(evil.getCode(),evil.getCode(),"PRI_NAME", "Dr Evil"));
        answers.add(new Answer(evil.getCode(),evil.getCode(),"PRI_PHONE", "61414222333"));


        answers.add(new Answer(superHero.getCode(),superHero.getCode(),"PRI_NAME", "James Bond"));
        answers.add(new Answer(superHero.getCode(),superHero.getCode(),"PRI_FIRSTNAME", "James"));

        String mobile = System.getenv("TEST_RECEIVE_MOBILE");
        answers.add(new Answer(superHero.getCode(),superHero.getCode(),"PRI_MOBILE", mobile));

        answers.add(new Answer(superHero.getCode(),superHero.getCode(),"PRI_PHONE", "61400000000"));

        String email = System.getenv("USER_EMAIL");
        answers.add(new Answer(superHero.getCode(),superHero.getCode(),"PRI_EMAIL", email));

        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_NAME", "Test Message"));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_SUBJECT", "This is a test message subject."));// This is should appear on email subject
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_TITLE", "GADA test message")); // This is the title would appear on message
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_BODY", "This is the main body type of the message from {{SUPER_HERO.PRI_NAME}}")); // This is  the body
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_HTML", "<html><body><p>This is the <b>html</b> body type of the message from {{BAD_GUY.PRI_NAME}} and {{SUPER_HERO.PRI_NAME}}.</p></body></html>")); // This is  the body
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_SHORT_BODY", "This is a short version of the message regarding {{BAD_GUY.PRI_NAME}}")); // This is  the body
        //answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_SHORT_BODY", "This is a short version of the message")); // This is  the body
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_CONTEXT_CODES", "[\"SUPER_HERO\",  \"BAD_GUY\" ]"));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_OPTIONAL_CONTEXT_CODES", "[\"LOVE_INTEREST\"]"));

        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_SEND_DATETIME", ""));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_SEND_MODE", "IMMEDIATELY"));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_MEDIA", "[\"EMAIL\", \"SMS\"]"));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_RECEIVED_CHECK", "TRUE"));
        answers.add(new Answer(msgTemplate.getCode(),msgTemplate.getCode(),"PRI_UNSUBSCRIBE", "TEST")); // The value represents the tag

        return answers;
        //beUtils.saveAnswers(answers);
    }

}
