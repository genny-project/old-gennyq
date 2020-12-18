package life.genny.notes.utils;

import org.jboss.logging.Logger;

public class WriteToBridge {
	 private static final Logger log = Logger.getLogger(WriteToBridge.class);	

//	public static String writeMessage(String bridgeUrl, QDataNoteMessage msg, final GennyToken userToken) {
//
//		Jsonb jsonb = JsonbBuilder.create();
//
//		String entityString = jsonb.toJson(msg);
//
//		String responseString = null;
//		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
//		CloseableHttpResponse response = null;
//		try {
//
//			HttpPost post = new HttpPost(bridgeUrl + "?channel=webdata");
//
//			StringEntity postEntity = new StringEntity(entityString, "UTF-8");
//
//			post.setEntity(postEntity);
//			post.setHeader("Content-Type", "application/json; charset=UTF-8");
//			if (userToken != null) {
//				post.addHeader("Authorization", "Bearer " + userToken.getToken()); // Authorization": `Bearer
//			}
//
//			response = httpclient.execute(post);
//			HttpEntity entity = response.getEntity();
//			responseString = EntityUtils.toString(entity);
//			return responseString;
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		} finally {
//			if (response != null) {
//				try {
//					response.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} else {
//				log.error("postApi response was null");
//			}
//			try {
//				httpclient.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return responseString;
//	}
}
