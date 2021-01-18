/*
 * (C) Copyright 2017 GADA Technology (http://www.outcome-hub.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Adam Crow
 *     Byron Aguirre
 */

package life.genny.models.attribute;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.datatype.DataType;
import life.genny.utils.LocalDateTimeAdapter;

/**
 * Attribute represents a distinct abstract Fact about a target entity
 * managed in the Qwanda library.
 * An attribute may be used directly in processing meaning for a target
 * entity. Such processing may be in relation to a comparison score against
 * another target entity, or to generate more attribute information via
 * inference and induction  This
 * attribute information includes:
 * <ul>
 * <li>The Human Readable name for this attibute (used for summary lists)
 * <li>The unique code for the attribute
 * <li>The description of the attribute
 * <li>The answerType that represents the format of the attribute
 * </ul>
 * <p>
 * Attributes represent facts about a target.
 * <p>
 *
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */


@Entity
@Table(name = "qattribute",
        indexes = {
                @Index(columnList = "code", name = "code_idx"),
                @Index(columnList = "realm", name = "code_idx")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"code", "realm"}))
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@RegisterForReflection
public class Attribute extends PanacheEntity {

    private static final Logger log = Logger.getLogger(Attribute.class);

    private static final String DEFAULT_CODE_PREFIX = "PRI_";
    private static final String REGEX_CODE = "[A-Z]{3}\\_[A-Z0-9\\.\\-\\@\\_]*";

    static public final String REGEX_NAME = "[\\pL0-9/\\:\\ \\_\\.\\,\\?\\>\\<\\%\\$\\&\\!\\*" + ""
            + "\\[\\]\\'\\-\\@\\(\\)]+.?";
    private static final String REGEX_REALM = "[a-zA-Z0-9]+";
    private static final String DEFAULT_REALM = "genny";

    /**
     *
     */

    @NotEmpty
    @JsonbTransient
    @Pattern(regexp = REGEX_REALM, message = "Must be valid Realm Format!")
    public String realm = DEFAULT_REALM;

    @NotNull
    @Size(max = 64)
    @Pattern(regexp = REGEX_CODE, message = "Must be valid Code!")
    @Column(name = "code", updatable = false, nullable = false, unique = true)
    public String code;

    @NotNull
    @Size(max = 128)
    @Pattern(regexp = REGEX_NAME, message = "Must contain valid characters for name")
    @Column(name = "name", updatable = true, nullable = true)
    public String name;


    @JsonbTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC"));

    @JsonbTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime updated;


    @Embedded
    @NotNull
    public DataType dataType;

    public Boolean defaultPrivacyFlag = false;

    public String description;

    public String help;

    public String placeholder;

    public String defaultValue;


    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    protected Attribute() {
    }


    public Attribute(String code, String name, DataType dataType) {
        this.code = code;
        this.name = name;
        this.dataType = dataType;
        this.placeholder = name;
    }


    /**
     * getDefaultCodePrefix This method is overrides the Base class
     *
     * @return the default Code prefix for this class.
     */
    static public String getDefaultCodePrefix() {
        return DEFAULT_CODE_PREFIX;
    }


    @Override
    public String toString() {
        return "Attribute [code=" + code + ", created=" + created + ", dataType=" + dataType + ", defaultPrivacyFlag="
                + defaultPrivacyFlag + ", defaultValue=" + defaultValue + ", description=" + description + ", help="
                + help + ", name=" + name + ", placeholder=" + placeholder + ", realm=" + realm + ", updated=" + updated
                + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((defaultPrivacyFlag == null) ? 0 : defaultPrivacyFlag.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((help == null) ? 0 : help.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((placeholder == null) ? 0 : placeholder.hashCode());
        result = prime * result + ((realm == null) ? 0 : realm.hashCode());
        result = prime * result + ((updated == null) ? 0 : updated.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Attribute other = (Attribute) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (defaultPrivacyFlag == null) {
            if (other.defaultPrivacyFlag != null)
                return false;
        } else if (!defaultPrivacyFlag.equals(other.defaultPrivacyFlag))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (help == null) {
            if (other.help != null)
                return false;
        } else if (!help.equals(other.help))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (placeholder == null) {
            if (other.placeholder != null)
                return false;
        } else if (!placeholder.equals(other.placeholder))
            return false;
        if (realm == null) {
            if (other.realm != null)
                return false;
        } else if (!realm.equals(other.realm))
            return false;
        if (updated == null) {
            if (other.updated != null)
                return false;
        } else if (!updated.equals(other.updated))
            return false;
        return true;
    }
}
