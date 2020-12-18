package life.genny.qwanda.endpoints;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import life.genny.qwanda.GennyToken;
import life.genny.qwanda.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.identity.SecurityIdentity;
import life.genny.notes.utils.LocalDateTimeAdapter;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.datatype.DataType;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.EntityEntity;
import life.genny.qwanda.entity.EntityQuestion;
import life.genny.qwanda.message.QDataAttributeMessage;
import life.genny.qwanda.message.QDataBaseEntityMessage;
import life.genny.qwanda.validation.Validation;
import life.genny.qwandautils.JsonUtils;
import life.genny.qwandautils.QwandaUtils;

@Path("/import")
@RegisterForReflection
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImportResource {

	private static final Logger log = Logger.getLogger(ImportResource.class);

	@ConfigProperty(name = "default.realm", defaultValue = "genny")
	String defaultRealm;

	@Inject
	SecurityIdentity securityIdentity;

	@Inject
	JsonWebToken accessToken;

	@Inject
	UserTransaction userTransaction;

	@OPTIONS
	public Response opt() {
		return Response.ok().build();
	}

	@GET
	@Path("/attributes")
//	@Transactional
	public Response importAttributes(@Context UriInfo uriInfo, @QueryParam("url") String externalGennyUrl) {
		GennyToken userToken = new GennyToken(accessToken.getRawToken());
		if (userToken == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being created", Status.FORBIDDEN);
		}

		log.info("External Genny Url = " + externalGennyUrl);
		try {
			userTransaction.setTransactionTimeout(3600);
			userTransaction.begin();

			String jsonString;

			jsonString = QwandaUtils.apiGet(externalGennyUrl + "/qwanda/attributes", accessToken.getRawToken());
			if (!StringUtils.isBlank(jsonString)) {

				QDataAttributeMessage attributesMsg = JsonUtils.fromJson(jsonString, QDataAttributeMessage.class);
				Attribute[] attributeArray = attributesMsg.getItems();

				for (Attribute attribute : attributeArray) {
					log.info(attribute);
					Attribute existing = Attribute.find("code", attribute.code).firstResult();

					DataType dt = attribute.dataType;
					List<Validation> vl = dt.getValidationList();
					List<Validation> goodList = new ArrayList<Validation>();
					for (Validation v : vl) {
						Validation existingValidation = Validation.find("code", v.code).firstResult();
						if (existingValidation == null) {
							v.persist();
							goodList.add(v);
						} else {
							goodList.add(existingValidation);
						}
					}

					if (existing == null) {
						attribute.id = null;
						attribute.dataType.setValidationList(goodList);
						attribute.persist();
					}
				}
			}
			userTransaction.commit();
			log.info("Finished import");
			return Response.status(Status.OK).build();

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.BAD_REQUEST).build();

	}

	@GET
	@Path("/baseentitys")
	// @Transactional
	// @TransactionConfiguration()
	public Response importBaseentitys(@Context UriInfo uriInfo, @QueryParam("url") String externalGennyUrl) {
		GennyToken userToken = new GennyToken(accessToken.getRawToken());
		if (userToken == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!userToken.hasRole("dev") && !userToken.hasRole("superadmin")) {
			throw new WebApplicationException("User not recognised. Entity not being created", Status.FORBIDDEN);
		}

		log.info("External Genny Url = " + externalGennyUrl);
		String jsonString;
		try {
			String hql = "select ea from EntityAttribute ea";
			hql = Base64.getUrlEncoder().encodeToString(hql.getBytes());
			jsonString = QwandaUtils.apiGet(externalGennyUrl + "/qwanda/baseentitys/search23/" + hql + "/0/10000000",
					accessToken.getRawToken());

			if (jsonString.contains("404 Not Found") || jsonString.startsWith("<html>")) {
				return Response.status(Status.BAD_REQUEST).build();
			}

			if (!StringUtils.isBlank(jsonString)) {
				JsonObject jsonMsg = new Gson().fromJson(jsonString, JsonObject.class);
				JsonArray items = jsonMsg.getAsJsonArray("items");

//				JsonParser parser = new JsonParser();
//				JsonElement tradeElement = parser.parse(jsonString);
//				JsonArray attributeValues = tradeElement.getAsJsonArray();
//

				List<BaseEntity> baseentitys = new ArrayList<>();

				/* we loop through the attribute values */
				int size = items.size();
				for (int i = 0; i < items.size(); i++) {

					BaseEntity be = new BaseEntity();

					JsonObject beJson = items.get(i).getAsJsonObject();
					String code = beJson.get("code").getAsString();
					LocalDateTime created = Value.getDateTime(beJson.get("created"));
					LocalDateTime updated = Value.getDateTime(beJson.get("updated"));
					String name = beJson.get("name").getAsString();
					String realm = beJson.get("realm").getAsString();

					be.realm = realm;
					be.active = true;
					be.code = code;
					be.name = name;
					be.created = created;
					be.updated = updated;
					// be.persist();

					JsonArray bes = beJson.getAsJsonArray("baseEntityAttributes");

					for (int e = 0; e < bes.size(); e++) {
						JsonObject ea = bes.get(e).getAsJsonObject();

						String attributeCode = ea.get("attributeCode").getAsString();
						String attributeName = ea.get("attributeName").getAsString();
						Boolean readonly = ea.get("readonly").getAsBoolean();
						Boolean inferred = ea.get("inferred").getAsBoolean();
						Boolean privacyFlag = ea.get("privacyFlag").getAsBoolean();
						Double weight = ea.get("weight").getAsDouble();

						LocalDateTime createdEA = Value.getDateTime(ea.get("created"));
						LocalDateTime updatedEA = Value.getDateTime(ea.get("updated"));

						Value value = Value.getValueFromJsonObject(ea);

						// String valueString =
						EntityAttribute eat = new EntityAttribute();
						eat.realm = be.realm;
						eat.attributeCode = attributeCode;
						eat.attributeName = attributeName;
						Attribute attribute = Attribute.find("code", attributeCode).firstResult();
						eat.attribute = attribute;
						eat.readonly = readonly;
						eat.inferred = inferred;
						eat.privacyFlag = privacyFlag;
						eat.setWeight(weight);
						eat.created = createdEA;
						eat.updated = updatedEA;
						eat.value = value;
						eat.baseentity = be;
						eat.baseEntityCode = be.code;
						// eat.persist();

						be.baseEntityAttributes.add(eat);
					}

					JsonArray links = beJson.getAsJsonArray("links");
					for (int link = 0; link < links.size(); link++) {
						JsonObject l = links.get(link).getAsJsonObject();

						JsonObject lnk = l.getAsJsonObject("link");
						String attributeCode = lnk.get("attributeCode").getAsString();
						String targetCode = lnk.get("targetCode").getAsString();
						String sourceCode = lnk.get("sourceCode").getAsString();
						JsonElement lnkJE = lnk.get("linkValue");
						String linkValue = null;
						if (lnkJE != null) {
							linkValue = lnkJE.getAsString();
						}

						LocalDateTime createdEE = Value.getDateTime(l.get("created"));
						Value value = Value.getValueFromJsonObject(l);

						EntityEntity ee = new EntityEntity();
						Attribute attribute = Attribute.find("code", attributeCode).firstResult();
						if (attribute == null) {
							log.error("NO ATTRIBUTE FOUND FOR [" + attributeCode + "] with " + Attribute.count()
									+ " attributes");
							DataType dtt = new DataType(BaseEntity.class);
							attribute = new Attribute(attributeCode, attributeCode, dtt);
							// attribute.persist();
						}
						ee.realm = be.realm;
						ee.attribute = attribute;
						ee.attributeCode = attributeCode;
						ee.sourceCode = sourceCode;
						ee.targetCode = targetCode;
						ee.created = createdEE;
						ee.value.dataType = attribute.dataType;
						ee.value.valueString = linkValue;
						ee.value.weight = value.weight;

						ee.link.attributeCode = attributeCode;
						ee.link.linkValue = linkValue;
						ee.link.sourceCode = sourceCode;
						ee.link.targetCode = targetCode;
						ee.link.weight = value.weight;

						// ee.persist();
						be.links.add(ee);
					}

					JsonArray questions = beJson.getAsJsonArray("questions");

					for (int q = 0; q < questions.size(); q++) {
						JsonObject questionQ = questions.get(q).getAsJsonObject();
						String valueStringQ = questionQ.get("valueString").getAsString();
						Double weightQ = questionQ.get("weight").getAsDouble();
						JsonObject lnkQ = questionQ.getAsJsonObject("link");
						String attributeCodeQ = lnkQ.get("attributeCode").getAsString();
						String targetCodeQ = lnkQ.get("targetCode").getAsString();
						String sourceCodeQ = lnkQ.get("sourceCode").getAsString();
						Value value = Value.getValueFromJsonObject(lnkQ);

						EntityQuestion eq = new EntityQuestion();
						eq.link.attributeCode = attributeCodeQ;
						JsonElement je = lnkQ.get("linkValue");
						if (je != null) {
							eq.link.linkValue = je.getAsString();
						}
						eq.link.sourceCode = sourceCodeQ;
						eq.link.targetCode = targetCodeQ;
						eq.link.weight = value.weight;
						eq.value.valueString = valueStringQ;
						eq.value.weight = weightQ;
						eq.persist();
						be.questions.add(eq);
					}
					// be.persist();
					log.info(i + " of " + size + " -> " + code);
					baseentitys.add(be);
				}

				log.info("SAVING!");

				try {
					userTransaction.setTransactionTimeout(10000);
					// userTransaction.setBatchSize(100);
					// Don't bother getting generated keys
					// userTransaction.setBatchGetGeneratedKeys(false);
					// Skip cascading persist
					// userTransaction.setPersistCascade(false);
					userTransaction.begin();
					int i = 0;
					for (BaseEntity b : baseentitys) {

						if (i % 20 == 0) { // 20, same as the JDBC batch size
							// flush a batch of inserts and release memory:
							b.persistAndFlush();
						} else {
							b.persist();
						}
						log.info("SAVING " + (i++) + " of " + size + " -> " + b.code);
					}
					userTransaction.commit();
					log.info("Finished saving " + baseentitys.size() + " bes");
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicMixedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicRollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				QDataBaseEntityMessage beMsg = JsonUtils.fromJson(jsonString, QDataBaseEntityMessage.class);
//				BaseEntity[] beArray = beMsg.getItems();

//				jsonString = fixJson(jsonString);
//				JsonArray jsonBEs = new Gson().fromJson(jsonString, JsonArray.class);

			}

			return Response.status(Status.OK).build();
		} catch (IOException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	private String fixJson(String resultStr) {
		String resultStr2 = resultStr.replaceAll(Pattern.quote("\\\""), Matcher.quoteReplacement("\""));
		String resultStr3 = resultStr2.replaceAll(Pattern.quote("\\n"), Matcher.quoteReplacement("\n"));
		String resultStr4 = resultStr3.replaceAll(Pattern.quote("\\\n"), Matcher.quoteReplacement("\n"));
//		String resultStr5 = resultStr4.replaceAll(Pattern.quote("\"{"),
//				Matcher.quoteReplacement("{"));
//		String resultStr6 = resultStr5.replaceAll(Pattern.quote("\"["),
//				Matcher.quoteReplacement("["));
//		String resultStr7 = resultStr6.replaceAll(Pattern.quote("]\""),
//				Matcher.quoteReplacement("]"));
//		String resultStr8 = resultStr5.replaceAll(Pattern.quote("}\""), Matcher.quoteReplacement("}"));
		String ret = resultStr4.replaceAll(Pattern.quote("\\\"" + ""), Matcher.quoteReplacement("\""));
		return ret;

	}

	@Transactional
	void onStart(@Observes StartupEvent ev) {
		log.info("Import Endpoint starting");

	}

	@Transactional
	void onShutdown(@Observes ShutdownEvent ev) {
		log.info("Import Endpoint Shutting down");
	}

}