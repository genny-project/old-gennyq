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

package life.genny.qwanda;


import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.VisualControlType;
import life.genny.models.entity.BaseEntity;

/**
 * Context is the class for all entity contexts managed in the Qwanda library. A
 * Context object is used as a means of supplying information about a question
 * that assists in understanding what answer is required. This context
 * information includes:
 * <ul>
 * <li>The name of the context class
 * <li>The context unique code
 * <li>The key String for this context e.g. "employee" or "footballer" this is
 * saved in name field
 * </ul>
 * <p>
 * Contexts represent the major way of supplying info about a question that
 * permits a source to make a full decision. Contexts are also used in message
 * merging.
 * <p>
 *
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */

@Entity
@Cacheable
@Table(name = "context",indexes = { @Index(name = "IDX_MYIDX1", columnList = "BASEENTITY_ID") })
@RegisterForReflection
public class Context extends PanacheEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

	@JsonbTransient
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BASEENTITY_ID", nullable = false)
	public BaseEntity baseentity;

	// For compatibility initially
	public String baseEntityCode;


    public Double weight = 1.0;

    public String contextCode;  // ALIAS


    public String dataType;


    public String dttCode;


    public VisualControlType visualControlType;


    /**
     * @return the visualControlType
     */
    public VisualControlType getVisualControlType() {
        return visualControlType;
    }

    /**
     * @param visualControlType the visualControlType to set
     */
    public void setVisualControlType(VisualControlType visualControlType) {
        this.visualControlType = visualControlType;
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    public Context() {
        // dummy for hibernate
    }

    /**
     * Constructor.
     */
    public Context(ContextType key, BaseEntity aEntity) {
        this(key, aEntity, VisualControlType.VCL_DEFAULT);
    }

    public Context(ContextType key, BaseEntity aEntity, VisualControlType visualControlType) {
        this(key, aEntity, visualControlType, 1.0);
    }

    public Context(ContextType key, BaseEntity aEntity, VisualControlType visualControlType, Double weight) {
        this.baseentity = aEntity;
        this.baseEntityCode = aEntity.code;
        this.contextCode = key.toString();
        this.visualControlType = visualControlType;
        this.weight = weight;
    }

 


    /**
     * @param aEntity entity to set
     */
    public void setEntity(BaseEntity aEntity) {
        this.baseentity = aEntity;
        this.baseEntityCode = aEntity.code;
        this.contextCode = aEntity.code;
    }

    /**
     * @return the weight
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(final Double weight) {
        this.weight = weight;
    }

    /**
     * @return the contextCode
     */
    public String getContextCode() {
        return contextCode;
    }


    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dttCode
     */
    public String getDttCode() {
        return dttCode;
    }

    /**
     * @param dttCode the dttCode to set
     */
    public void setDttCode(String dttCode) {
        this.dttCode = dttCode;
    }

	@Override
	public int hashCode() {
		return Objects.hash(baseEntityCode, contextCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Context))
			return false;
		Context other = (Context) obj;
		return Objects.equals(baseEntityCode, other.baseEntityCode) && Objects.equals(contextCode, other.contextCode);
	}

  
}