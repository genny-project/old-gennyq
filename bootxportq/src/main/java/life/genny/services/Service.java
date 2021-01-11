package life.genny.services;

import life.genny.bootxport.bootx.QwandaRepository;
import life.genny.qwanda.Ask;
import life.genny.qwanda.CodedEntity;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.EntityEntity;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.qwanda.validation.Validation;
import life.genny.qwandautils.GennySettings;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public Validation findValidationByCode(@NotNull String code) {
        return null;
    }

    @Override
    public Attribute findAttributeByCode(@NotNull String code) {
        return null;
    }

    @Override
    public BaseEntity findBaseEntityByCode(@NotNull String baseEntityCode) {
        return null;
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
    public void bulkUpdate(ArrayList<CodedEntity> objectList, HashMap<String, CodedEntity> mapping) {

    }

    @Override
    public void bulkInsert(ArrayList<CodedEntity> objectList) {

    }

    @Override
    public void bulkInsertAsk(ArrayList<Ask> objectList) {

    }

    @Override
    public void bulkUpdateAsk(ArrayList<Ask> objectList, HashMap<String, Ask> mapping) {

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

    @Override
    protected String getRealm() {

        String realm = null;
        try {
            realm = securityService.getRealm();
        } catch (Exception e) {
            return currentRealm;
        }
        if (realm == null)
            return currentRealm;
        else
            return realm;

    }


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
            Attribute existing = findAttributeByCode(attribute.getCode());

            existing.setRealm(getRealm());

            existing = getEntityManager().merge(existing);
            return existing.getId();
        } catch (final PersistenceException e) {
            Attribute existing = findAttributeByCode(attribute.getCode());

            existing.setRealm(getRealm());

            existing = getEntityManager().merge(existing);
            return existing.getId();
        } catch (final IllegalStateException e) {
            Attribute existing = findAttributeByCode(attribute.getCode());

            existing.setRealm(getRealm());

            existing = getEntityManager().merge(existing);
            return existing.getId();
        }
        return attribute.getId();
    }
}
