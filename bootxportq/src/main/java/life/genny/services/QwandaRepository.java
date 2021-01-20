package life.genny.services;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;
import life.genny.qwandautils.GennySettings;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QwandaRepository {
    String currentRealm = GennySettings.mainrealm; // permit temprorary override

    private QwandaRepository() {
    }

    public QwandaRepository(String realm) {
        this.currentRealm = realm;
    }

    public Validation upsert(Validation validation) {
        Validation existing = findValidationByCode(validation.code);
        if (existing == null) {
            validation.persist();
            return validation;
        }
        existing.getEntityManager().merge(validation);
        return existing;
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

    public BaseEntity upsert(BaseEntity baseEntity) {
        BaseEntity existing = findBaseEntityByCode(baseEntity.code);
        if (existing == null) {
            baseEntity.persist();
            return baseEntity;
        }
        existing.getEntityManager().merge(baseEntity);
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

    public void insert(Attribute attribute) {
        attribute.persist();
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
        return null;
    }

    public EntityEntity findEntityEntity(String sourceCode, String targetCode, String linkCode) {
        return null;
    }

    public Integer updateEntityEntity(EntityEntity ee) {
        return null;
    }

    public EntityEntity insertEntityEntity(EntityEntity ee) {
        return null;
    }

    public QuestionQuestion findQuestionQuestionByCode(String sourceCode, String targetCode) {
        return null;
    }


    public Question findQuestionByCode(@NotNull String code) {
        return null;
    }

    public QuestionQuestion upsert(QuestionQuestion qq) {
        return null;
    }

    public Question findQuestionByCode(@NotNull String code, @NotNull String realm) {
        return null;
    }

    public Long updateRealm(Question que) {
        return null;
    }

    public Long insert(Question question) {
        return null;
    }

    public QBaseMSGMessageTemplate findTemplateByCode(@NotNull String templateCode) {
        return null;
    }

    public QBaseMSGMessageTemplate findTemplateByCode(@NotNull String templateCode, @NotNull String realm) {
        return null;
    }

    public Long updateRealm(QBaseMSGMessageTemplate msg) {
        return null;
    }

    public Long insert(QBaseMSGMessageTemplate template) {
        return null;
    }

    public Long update(QBaseMSGMessageTemplate template) {
        return null;
    }

    public <T> List<T> queryTableByRealm(String tableName, String realm) {
        return null;
    }

    public void bulkUpdate(ArrayList<PanacheEntity> objectList, HashMap<String, PanacheEntity> mapping) {

    }

    public void bulkInsert(ArrayList<PanacheEntity> objectList) {

    }

    public void bulkInsertQuestionQuestion(ArrayList<QuestionQuestion> objectList) {

    }

    public void bulkUpdateQuestionQuestion(ArrayList<QuestionQuestion> objectList, HashMap<String, QuestionQuestion> mapping) {

    }

    public void cleanAsk(String realm) {

    }

    public void cleanFrameFromBaseentityAttribute(String realm) {

    }

//    protected String getRealm() {
//
//        String realm = null;
//        try {
//            realm = securityService.getRealm();
//        } catch (Exception e) {
//            return currentRealm;
//        }
//        if (realm == null)
//            return currentRealm;
//        else
//            return realm;
//
//    }


/*
    public Long insert(final Attribute attribute) {
        // always check if baseentity exists through check for unique code
        try {
            Attribute existing = findAttributeByCode(attribute.code);
            if (existing == null) {

                attribute.realm = getRealm();

                getEntityManager().persist(attribute);
            }

            this.pushAttributes();
        } catch (final ConstraintViolationException e) {
            Attribute existing = findAttributeByCode(attribute.code);

            existing.realm = getRealm();

            existing = getEntityManager().merge(existing);
            return existing.id;
        } catch (final PersistenceException e) {
            Attribute existing = findAttributeByCode(attribute.code);

            existing.realm = getRealm();

            existing = getEntityManager().merge(existing);
            return existing.id;
        } catch (final IllegalStateException e) {
            Attribute existing = findAttributeByCode(attribute.code);

            existing.realm = getRealm();

            existing = getEntityManager().merge(existing);
            return existing.id;
        }
        return attribute.id;
    }
    */
}
