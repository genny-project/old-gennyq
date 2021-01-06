package life.genny.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.qwanda.message.QMSGMessage;
import life.genny.qwandautils.JsonUtils;
import life.genny.qwandautils.QwandaUtils;

public class MergeHelper {
	
	final public static String PDF_GEN_SERVICE_API_URL = System.getenv("PDF_GEN_SERVICE_API_URL") == null ? "http://localhost:7331"
			: System.getenv("PDF_GEN_SERVICE_API_URL");

	public static Map<String, String> getKeyEntityAttrMap(QMSGMessage message) {

		String[] messageDataArr = message.getMsgMessageData();
		Map<String, String> keyEntityMap = new HashMap<>();
		if (messageDataArr != null && messageDataArr.length > 0) {

			for (String data : messageDataArr) {

				String[] dataEntity = StringUtils.split(data, ":");
				if (dataEntity.length > 0) {

					keyEntityMap.put(dataEntity[0], dataEntity[1]);

				}

			}

		}
		System.out.println("key entity map ::" + keyEntityMap);
		return keyEntityMap;
	}

	public static QBaseMSGMessageTemplate getTemplate(String templateCode, String token) {

		QBaseMSGMessageTemplate template = QwandaUtils.getTemplate(templateCode, token);
		return template;
	}

	public static byte[] getUrlContentInBytes(String urlString) {

		byte[] bytes = null;
		try {
			URL url = new URL(urlString);
			// BufferedInputStream bis = new
			// BufferedInputStream(url.openConnection().getInputStream());
			InputStream is = url.openStream();
			bytes = IOUtils.toByteArray(is);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytes;

	}



	/* Converts HTML String into PDF and into BASE64 */
	public static String getHtmlStringToPdfInByte(String htmlString) {

		JSONObject postObj = new JSONObject();
		postObj.put("html", htmlString);
		Gson gson = new Gson();
		String resp = null;
		String path = null;
		try {

			/* Camelot htmlToPdfConverter service */ 
			resp = QwandaUtils.apiPostEntity(PDF_GEN_SERVICE_API_URL + "/raw", gson.toJson(postObj), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("response for attachment ::" + resp);

		if(resp != null) {
			JSONObject respObj = JsonUtils.fromJson(resp, JSONObject.class);
			path = (String) respObj.get("path");
		}
		
		return path;
	}

}
