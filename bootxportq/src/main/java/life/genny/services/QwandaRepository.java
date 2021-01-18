package life.genny.services;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.bootxport.bootx.BeanNotNullFields;
import life.genny.models.attribute.AttributeLink;
import life.genny.models.attribute.EntityAttribute;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;
import life.genny.qwanda.message.QEventLinkChangeMessage;
import life.genny.qwandautils.GennySettings;
import life.genny.qwandautils.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xmlschema.BaseAttribute;
import org.w3c.dom.Attr;

import javax.inject.Inject;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class QwandaRepository {
    private static final int BATCHSIZE = 500;

    @Inject
    EntityManager em;
    protected static final Logger log = LogManager.getLogger(QwandaRepository.class);
    String currentRealm = GennySettings.mainrealm; // permit temprorary override

    private QwandaRepository() {
    }

    public QwandaRepository(String realm) {
        this.currentRealm = realm;
    }


    public Attribute upsert(Attribute attribute) {
        Attribute existing = findAttributeByCode(attribute.code);
        if (existing == null) {
            attribute.persist();
            return attribute;
        }
        existing.getEntityManager().merge(attribute);
        return existing;
    }


    public Question upsert(Question q) {
        Question existing = findQuestionByCode(q.getCode());
        if (existing == null) {
            q.persist();
            return q;
        }
        existing.getEntityManager().merge(q);
        return existing;
    }

    public void insert(Ask ask) {
        ask.persist();
    }

    public Long insert(final Question question) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        try {
            question.setRealm(currentRealm);
            em.persist(question);
            log.info(String.format("Saved question:%s. ", question.getCode()));
            transaction.commit();
        } catch (PersistenceException | IllegalStateException e) {
            Question existing = findQuestionByCode(question.getCode());
            existing.setRealm(currentRealm);
            existing = em.merge(existing);
            transaction.commit();
            return existing.id;
        }
        return question.id;
    }


    public Validation findValidationByCode(@NotNull final String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        Validation existing = Validation.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }

    public Attribute findAttributeByCode(@NotNull String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        return Attribute.find("code = :code and realm= :realm", params).firstResult();
    }

    public BaseEntity findBaseEntityByCode(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        BaseEntity existing = BaseEntity.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }

    public Long updateWithAttributes(BaseEntity entity) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        entity.realm = currentRealm;
        try {
            entity = em.merge(entity);
        } catch (final Exception e) {
            em.persist(entity);
        }
        String json = JsonUtils.toJson(entity);
        //TODO
//        writeToDDT(entity.getCode(), json);
        transaction.commit();
        return entity.id;
    }


    public Integer updateEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Integer result = 0;
        try {
            String sql = "update EntityEntity ee " +
                    "set ee.weight=:weight, ee.valueString=:valueString, ee.link.weight=:weight, ee.link.linkValue=:valueString " +
                    "where ee.pk.targetCode=:targetCode and ee.link.attributeCode=:linkAttributeCode and ee.link.sourceCode=:sourceCode";
            result = em.createQuery(sql)
                    .setParameter("sourceCode",
                            ee.getPk().getSource().getCode())
                    .setParameter("linkAttributeCode",
                            ee.getLink().getAttributeCode())
                    .setParameter("targetCode", ee.getPk().getTargetCode())
                    .setParameter("weight", ee.getWeight())
                    .setParameter("valueString", ee.valueString)
                    .executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        transaction.commit();
        return result;
    }

    protected String getCurrentToken() {
        return "DUMMY_TOKEN";
    }

    public void sendQEventLinkChangeMessage(final QEventLinkChangeMessage event) {
        log.info("Send Link Change:" + event);
    }

    public EntityEntity insertEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(ee);
        QEventLinkChangeMessage msg = new QEventLinkChangeMessage(ee.getLink(), null, getCurrentToken());
        sendQEventLinkChangeMessage(msg);
        transaction.commit();
        log.info(String.format("Sent Event Link Change Msg:%s.", msg));
        return ee;
    }


    public Question findQuestionByCode(@NotNull String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        return Question.find("code = :code and realm= :realm", params).firstResult();
    }


    public Long updateRealm(Question que) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        long result = em.createQuery(
                "update Question que set que.realm =:realm where que.code=:code")
                .setParameter("code", que.getCode())
                .setParameter("realm", que.getRealm()).executeUpdate();
        transaction.commit();
        return result;
    }


    public <T> List<T> queryTableByRealm(String tableName, String realm) {
        List<T> result = Collections.emptyList();
        try {
            Query query = em.createQuery(String.format("SELECT temp FROM %s temp where temp.realm=:realmStr", tableName));
            query.setParameter("realmStr", realm);
            result = query.getResultList();
        } catch (Exception e) {
            log.error(String.format("Query table %s Error:%s".format(realm, e.getMessage())));
        }
        return result;
    }


    private void merge() {

    }

    public void bulkUpdate(List<PanacheEntity> objectList, Map<String, PanacheEntity> mapping) {
        if (objectList.isEmpty()) return;
        BeanNotNullFields copyFields = new BeanNotNullFields();
        for (PanacheEntity panacheEntity : objectList) {
            if (panacheEntity instanceof QBaseMSGMessageTemplate) {
                QBaseMSGMessageTemplate newObj = (QBaseMSGMessageTemplate) panacheEntity;
                QBaseMSGMessageTemplate msg = (QBaseMSGMessageTemplate) mapping.get(newObj.code);
                msg.name = newObj.name;
                msg.setDescription(newObj.getDescription());
                msg.setEmail_templateId(newObj.getEmail_templateId());
                msg.setSms_template(newObj.getSms_template());
                msg.setSubject(newObj.getSubject());
                msg.setToast_template(newObj.getToast_template());
                em.merge(msg);
            } else {
                if (panacheEntity instanceof Attribute) {
                    String code = ((Attribute) panacheEntity).code;
                    Attribute val = (Attribute)(mapping.get(code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", code));
                    }
                    try {
                        copyFields.copyProperties(val, panacheEntity);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    em.merge(val);
                } else if (panacheEntity instanceof Validation) {
                    String code = ((Validation) panacheEntity).code;
                    Validation val = (Validation)(mapping.get(code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", code));
                    }
                    try {
                        copyFields.copyProperties(val, panacheEntity);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    em.merge(val);

                } else if (panacheEntity instanceof BaseEntity) {
                    String code = ((BaseEntity) panacheEntity).getCode();
                    BaseEntity val = (BaseEntity)(mapping.get(code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", code));
                    }
                    try {
                        copyFields.copyProperties(val, panacheEntity);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    em.merge(val);
                }
            }
        }
    }


    public void bulkInsert(List<PanacheEntity> objectList) {
        if (objectList.isEmpty())
            return;

        int index = 1;
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();

        for (PanacheEntity panacheEntity : objectList) {
            em.persist(panacheEntity);
            if (index % BATCHSIZE == 0) {
                //flush a batch of inserts and release memory:
                log.debug("Batch is full, flush to database.");
                em.flush();
            }
            index += 1;
        }
        transaction.commit();
    }

    public void bulkInsertQuestionQuestion(ArrayList<QuestionQuestion> objectList) {
        if (objectList.isEmpty()) return;

        int index = 1;
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();

        for (QuestionQuestion qq : objectList) {
            em.persist(qq);
            if (index % BATCHSIZE == 0) {
                //flush a batch of inserts and release memory:
                log.debug("BaseEntity Batch is full, flush to database.");
                em.flush();
            }
            index += 1;
        }
        transaction.commit();
    }

    public void bulkUpdateQuestionQuestion(ArrayList<QuestionQuestion> objectList, HashMap<String, QuestionQuestion> mapping) {
        for (QuestionQuestion qq : objectList) {
            String uniqCode = qq.getSourceCode() + "-" + qq.getTarketCode();
            QuestionQuestion existing = mapping.get(uniqCode.toUpperCase());
            existing.setMandatory(qq.getMandatory());
            existing.setWeight(qq.getWeight());
            existing.setReadonly(qq.getReadonly());
            existing.setDependency(qq.getDependency());
            em.merge(existing);
        }
    }

    public void cleanAsk(String realm) {
        long number = Ask.delete("realm", realm);
        log.info(String.format("Clean up ask, realm:%s, %d ask deleted", realm, number));
    }

    public void cleanFrameFromBaseentityAttribute(String realm) {
        Map<String, Object> params = new HashMap<>();
        params.put("baseEntityCode", "RUL_FRM%_GRP");
        params.put("attributeCode", "PRI_ASKS");
        params.put("realm", currentRealm);
        long number = EntityAttribute.delete("baseEntityCode like :baseEntityCode and attributeCode = :attributeCode and realm = :realm", params);
        log.info(String.format("Clean up BaseentityAttribute, realm:%s, %d BaseentityAttribute deleted", realm, number));
    }
}
