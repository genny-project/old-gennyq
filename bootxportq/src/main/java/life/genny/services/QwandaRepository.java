package life.genny.services;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.bootxport.bootx.BeanNotNullFields;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.models.validation.Validation;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.qwanda.message.QEventLinkChangeMessage;
import life.genny.qwandautils.GennySettings;
import life.genny.qwandautils.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class QwandaRepository {
    @Inject
    EntityManager em;
    private static final int BATCHSIZE = 500;

    protected static final Logger log = LogManager.getLogger(
            MethodHandles.lookup().lookupClass().getCanonicalName());
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

    protected EntityManager getEntityManager() {
        return em;
    }

    public Long updateWithAttributes(BaseEntity entity) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        entity.realm = currentRealm;
        try {
            entity = getEntityManager().merge(entity);
        } catch (final Exception e) {
            getEntityManager().persist(entity);
        }
        String json = JsonUtils.toJson(entity);
//        writeToDDT(entity.getCode(), json);
        transaction.commit();
        return entity.id;
    }


    public Integer updateEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        int result = 0;
        try {
            String sql =
                    "update EntityEntity ee set ee.weight=:weight, ee.valueString=:valueString, ee.link.weight=:weight, ee.link.linkValue=:valueString where ee.pk.targetCode=:targetCode and ee.link.attributeCode=:linkAttributeCode and ee.link.sourceCode=:sourceCode";
            result = getEntityManager().createQuery(sql)
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

    //TODO
    public void sendQEventLinkChangeMessage(final QEventLinkChangeMessage event) {
        log.info("Send Link Change:" + event);
    }

    // TODO
    protected String getCurrentToken() {
        return "DUMMY_TOKEN";
    }

    public EntityEntity insertEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        getEntityManager().persist(ee);
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
        Question existing = Question.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }


    public Long updateRealm(Question que) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        long result = getEntityManager().createQuery(
                "update Question que set que.realm =:realm where que.code=:code")
                .setParameter("code", que.getCode())
                .setParameter("realm", que.getRealm()).executeUpdate();
        transaction.commit();
        return result;
    }

    public Long insert(Question question) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        try {
            question.setRealm(currentRealm);
            getEntityManager().persist(question);
            log.info(String.format("Saved question:%s. ", question.getCode()));
            transaction.commit();
        } catch (PersistenceException | IllegalStateException e) {
            Question existing = findQuestionByCode(question.getCode());
            existing.setRealm(currentRealm);
            existing = getEntityManager().merge(existing);
            transaction.commit();
            return existing.id;
        }
        return question.id;
    }


    public <T> List<T> queryTableByRealm(String tableName, String realm) {
        List<T> result = Collections.emptyList();
        try {
            Query query = getEntityManager().createQuery(String.format("SELECT temp FROM %s temp where temp.realm=:realmStr", tableName));
            query.setParameter("realmStr", realm);
            result = query.getResultList();
        } catch (Exception e) {
            log.error(String.format("Query table %s Error:%s".format(realm, e.getMessage())));
        }
        return result;
    }

    public void bulkUpdate(List<PanacheEntity> objectList, Map<String, PanacheEntity> mapping) {
        if (objectList.isEmpty()) return;
        BeanNotNullFields copyFields = new BeanNotNullFields();
        for (PanacheEntity panacheEntity : objectList) {
            if (panacheEntity instanceof QBaseMSGMessageTemplate) {
                QBaseMSGMessageTemplate obj = ((QBaseMSGMessageTemplate) panacheEntity);
                QBaseMSGMessageTemplate msg = (QBaseMSGMessageTemplate) mapping.get(obj.code);
                msg.name = obj.name;
                msg.setDescription(obj.getDescription());
                msg.setEmail_templateId(obj.getEmail_templateId());
                msg.setSms_template(obj.getSms_template());
                msg.setSubject(obj.getSubject());
                msg.setToast_template(obj.getToast_template());
                getEntityManager().merge(msg);
            } else {
                if (panacheEntity instanceof BaseEntity) {
                    BaseEntity obj = (BaseEntity) panacheEntity;
                    BaseEntity val = (BaseEntity) (mapping.get(obj.getCode()));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.getCode()));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.getCode()));
                    }

                    val.realm = currentRealm;
                    getEntityManager().merge(val);
                } else if (panacheEntity instanceof Validation) {
                    Validation obj = (Validation) panacheEntity;
                    Validation val = (Validation) (mapping.get(obj.code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.code));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    getEntityManager().merge(val);
                } else if (panacheEntity instanceof Attribute) {
                    Attribute obj = (Attribute) panacheEntity;
                    Attribute val = (Attribute) (mapping.get(obj.code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.code));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    getEntityManager().merge(val);
                }
            }
        }
    }

    public void bulkInsert(List<PanacheEntity> objectList) {
        if (objectList.isEmpty()) return;

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

    public void bulkInsertQuestionQuestion(List<QuestionQuestion> objectList) {
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

    public void bulkUpdateQuestionQuestion(List<QuestionQuestion> objectList, Map<String, QuestionQuestion> mapping) {
        for (QuestionQuestion qq : objectList) {
            String uniqCode = qq.getSourceCode() + "-" + qq.getTarketCode();
            QuestionQuestion existing = mapping.get(uniqCode.toUpperCase());
            existing.setMandatory(qq.getMandatory());
            existing.setWeight(qq.getWeight());
            existing.setReadonly(qq.getReadonly());
            existing.setDependency(qq.getDependency());
            getEntityManager().merge(existing);
        }

    }

    public void cleanAsk(String realm) {
        String qlString = String.format("delete from ask where realm = '%s'", realm);
        EntityManager em1 = getEntityManager();
        Query query = em1.createNativeQuery(qlString);
        int number = query.executeUpdate();
        em1.flush();
        log.info(String.format("Clean up ask, realm:%s, %d ask deleted", realm, number));
    }

    public void cleanFrameFromBaseentityAttribute(String realm) {
        String qlString = "delete from baseentity_attribute " +
                "where baseEntityCode like \'RUL_FRM%_GRP\' " +
                "and attributeCode = \'PRI_ASKS\' " +
                "and realm = \'" + realm + "\'";
        EntityManager em1 = getEntityManager();
        Query query = em1.createNativeQuery(qlString);
        int number = query.executeUpdate();
        em1.flush();
        log.info(String.format("Clean up BaseentityAttribute, realm:%s, %d BaseentityAttribute deleted", realm, number));
    }
}
