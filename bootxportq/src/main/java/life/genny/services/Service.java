package life.genny.services;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.bootxport.bootx.QwandaRepository;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;
import life.genny.qwandautils.GennySettings;
import org.hibernate.exception.ConstraintViolationException;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.quarkus.hibernate.orm.panache.Panache.getEntityManager;

public class Service implements QwandaRepository {
    String currentRealm = GennySettings.mainrealm; // permit temprorary override

    /**
     * @param currentRealm the currentRealm to set
     */
    public void setCurrentRealm(String currentRealm) {
        this.currentRealm = currentRealm;
    }

    @Override
    public void setRealm(String realm) {

    }

    @Override
    public <T> void delete(T entity) {

    }

    @Override
    public Validation upsert(Validation validation) {
        return null;
    }

    @Override
    public Attribute upsert(Attribute attribute) {
        return null;
    }

    @Override
    public BaseEntity upsert(BaseEntity baseEntity) {
        BaseEntity existing = findBaseEntity(baseEntity);
        if (existing == null) {
            baseEntity.persist();
            return baseEntity;
        } else {
            existing.getEntityManager().merge(baseEntity);
        }
        return null;
    }

    @Override
    public Question upsert(Question q) {
        return null;
    }

    @Override
    public Long insert(Ask ask) {
        return null;
    }

    @Override
    public void insert(Attribute attribute) {

    }

    @Override
    public Validation findValidationByCode(@NotNull String code) {
        return null;
    }

    @Override
    public Attribute findAttributeByCode(@NotNull String code) {
        return null;
    }

    @Override
    public BaseEntity findBaseEntity(@NotNull BaseEntity baseEntity) {
        return findBaseEntityByCode(baseEntity.code, baseEntity.realm);
    }

    @Override
    public BaseEntity findBaseEntityByCode(String code, String realm) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", realm);
        BaseEntity existing = BaseEntity.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }

    @Override
    public Long updateWithAttributes(BaseEntity entity) {
        return null;
    }

    @Override
    public EntityEntity findEntityEntity(String sourceCode, String targetCode, String linkCode) {
        return null;
    }

    @Override
    public Integer updateEntityEntity(EntityEntity ee) {
        return null;
    }

    @Override
    public EntityEntity insertEntityEntity(EntityEntity ee) {
        return null;
    }

    @Override
    public QuestionQuestion findQuestionQuestionByCode(String sourceCode, String targetCode) {
        return null;
    }

    @Override
    public Question findQuestionByCode(@NotNull String code) {
        return null;
    }

    @Override
    public QuestionQuestion upsert(QuestionQuestion qq) {
        return null;
    }

    @Override
    public Question findQuestionByCode(@NotNull String code, @NotNull String realm) {
        return null;
    }

    @Override
    public Long updateRealm(Question que) {
        return null;
    }

    @Override
    public Long insert(Question question) {
        return null;
    }

    @Override
    public QBaseMSGMessageTemplate findTemplateByCode(@NotNull String templateCode) {
        return null;
    }

    @Override
    public QBaseMSGMessageTemplate findTemplateByCode(@NotNull String templateCode, @NotNull String realm) {
        return null;
    }

    @Override
    public Long updateRealm(QBaseMSGMessageTemplate msg) {
        return null;
    }

    @Override
    public Long insert(QBaseMSGMessageTemplate template) {
        return null;
    }

    @Override
    public Long update(QBaseMSGMessageTemplate template) {
        return null;
    }

    @Override
    public <T> List<T> queryTableByRealm(String tableName, String realm) {
        return null;
    }

    @Override
    public void bulkUpdate(ArrayList<PanacheEntity> objectList, HashMap<String, PanacheEntity> mapping) {

    }

    @Override
    public void bulkInsert(ArrayList<PanacheEntity> objectList) {

    }

    @Override
    public void bulkInsertQuestionQuestion(ArrayList<QuestionQuestion> objectList) {

    }

    @Override
    public void bulkUpdateQuestionQuestion(ArrayList<QuestionQuestion> objectList, HashMap<String, QuestionQuestion> mapping) {

    }

    @Override
    public void cleanAsk(String realm) {

    }

    @Override
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
