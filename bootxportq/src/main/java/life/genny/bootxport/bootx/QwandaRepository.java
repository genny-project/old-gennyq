package life.genny.bootxport.bootx;

import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.qwanda.*;
import life.genny.models.attribute.Attribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface QwandaRepository {

    void setRealm(String realm);

    <T> void delete(T entity);

    Validation upsert(Validation validation);

    Attribute upsert(Attribute attribute);

    BaseEntity upsert(BaseEntity baseEntity);

    Question upsert(Question q);

    Long insert(final Ask ask);

    void insert(Attribute attribute);

    Validation findValidationByCode(@NotNull final String code);

    Attribute findAttributeByCode(@NotNull final String code);

    BaseEntity findBaseEntityByCode(@NotNull final String baseEntityCode);

    Long updateWithAttributes(BaseEntity entity);

    EntityEntity findEntityEntity(final String sourceCode, final String targetCode, final String linkCode);

    Integer updateEntityEntity(final EntityEntity ee);

    EntityEntity insertEntityEntity(final EntityEntity ee);

    QuestionQuestion findQuestionQuestionByCode(
            final String sourceCode, final String targetCode);

    Question findQuestionByCode(@NotNull final String code);

    QuestionQuestion upsert(QuestionQuestion qq);

    Question findQuestionByCode(@NotNull final String code, @NotNull final String realm);

    Long updateRealm(Question que);

    Long insert(final Question question);

    QBaseMSGMessageTemplate findTemplateByCode(@NotNull final String templateCode);

    QBaseMSGMessageTemplate findTemplateByCode(@NotNull final String templateCode, @NotNull final String realm);

    Long updateRealm(QBaseMSGMessageTemplate msg);

    Long insert(final QBaseMSGMessageTemplate template);

    Long update(final QBaseMSGMessageTemplate template);

    <T> List<T> queryTableByRealm(String tableName, String realm);

    // For Validation, Attribute, AttributeLink, QuestionQuestion, QBaseMSGMessageTemplate
    void bulkUpdate(ArrayList<PanacheEntity> objectList, HashMap<String, PanacheEntity> mapping);

    void bulkInsert(ArrayList<PanacheEntity> objectList);

    void bulkInsertQuestionQuestion(ArrayList<QuestionQuestion> objectList);

    void bulkUpdateQuestionQuestion(ArrayList<QuestionQuestion> objectList, HashMap<String, QuestionQuestion> mapping);

    void cleanAsk(String realm);

    void cleanFrameFromBaseentityAttribute(String realm);
}
