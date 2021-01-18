/*
 * (C) Copyright 2017 GADA Technology (http://www.outcome-hub.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors: Adam Crow Byron Aguirre
 */

package life.genny.qwanda;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.qwanda.datatype.LocalDateTimeAdapter;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

/**
 * CoreEntity represents a base level core set of class attributes. It is the
 * base parent for many Qwanda classes and serves to establish Hibernate
 * compatibility and datetime stamping. This attribute information includes:
 * <ul>
 * <li>The Human Readable name for this class (used for summary lists)
 * <li>The unique code for the class object
 * <li>The description of the class object
 * <li>The created date time
 * <li>The last modified date time for the object
 * </ul>
 *
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */

@MappedSuperclass
public abstract class CoreEntity extends PanacheEntity implements Serializable, Comparable<Object> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Stores logger object.
     */
    protected static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    public static final String REGEX_NAME = "[\\pL0-9/: _.,?><%$&!*" + "" + "\\[\\]'\\-@()]+.?";
    public static final String REGEX_REALM = "[a-zA-Z0-9]+";
    public static final String DEFAULT_REALM = "genny";

    /**
     * Stores the Created UMT DateTime that this object was created
     */
    @Expose
    @Column(name = "created")
    private LocalDateTime created;

    /**
     * Stores the Last Modified UMT DateTime that this object was last updated
     */
    // @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @Column(name = "updated")
    @Expose
    private LocalDateTime updated;

    /**
     * A field that stores the human readable summary name of the attribute.
     * <p>
     * Note that this field is in English.
     */
    @NotNull
    @Size(max = 128)
    @Pattern(regexp = REGEX_NAME, message = "Must contain valid characters for name")
    @Column(name = "name")
    @Expose
    private String name;

    /**
     * A field that stores the human readable realm of this entity.
     * <p>
     * Note that this field is in English.
     */
    @NotNull
    @Size(max = 48)
    @Pattern(regexp = REGEX_REALM, message = "Must contain valid characters for realm")
    @Column(name = "realm", nullable = false)
    @Expose
    private String realm = DEFAULT_REALM;

    /**
     * Constructor.
     */
    protected CoreEntity() {
        // dummy
    }

    /**
     * Constructor.
     *
     * @param aName the summary name of the core entity
     */
    protected CoreEntity(final String aName) {
        super();
        this.realm = DEFAULT_REALM;
        this.name = aName;
        autocreateCreated();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param aName human readable text representing the question
     */
    public void setName(final String aName) {
        this.name = aName;
    }

    /**
     * @return the created
     */
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    /**
     * @return the updated
     */
    public LocalDateTime getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(final LocalDateTime updated) {
        this.updated = updated;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(final String realm) {
        this.realm = realm;
    }

    @PreUpdate
    public void autocreateUpdate() {
        setUpdated(LocalDateTime.now(ZoneId.of("Z")));
    }

    @PrePersist
    public void autocreateCreated() {
        if (getCreated() == null)
            setCreated(LocalDateTime.now(ZoneId.of("Z")));
        autocreateUpdate();
    }

    @Transient
    @JsonIgnore
    public Date getCreatedDate() {
        return Date.from(created.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Transient
    @JsonIgnore
    public Date getUpdatedDate() {
        if (updated != null) {
            return Date.from(updated.atZone(ZoneId.systemDefault()).toInstant());
        } else
            return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[id=" + id + ", created=" + created + ", updated=" + updated + ", name=" + name + "]";
    }
}
