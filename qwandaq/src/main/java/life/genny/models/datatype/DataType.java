/*
 * (C) Copyright 2017,2020 GADA Technology (http://www.outcome-hub.com/) and others.
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

package life.genny.models.datatype;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import life.genny.models.converter.ValidationListConverter;
import life.genny.models.validation.Validation;
import life.genny.models.validation.ValidationList;

/**
 * DataType represents a distinct abstract Data Representation in the Qwanda
 * library. The data types express the format and the validations required for
 * values collected. In addition to the extended CoreEntity this information
 * includes:
 * <ul>
 * <li>The code type of the base data e.g. Text, Integer, etc.
 * <li>The List of default Validation items
 * <li>The default mask used for data entry
 * </ul>
 * <p>
 *
 * <p>
 *
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */

@Embeddable
public class DataType implements Serializable {
	public static final String DTT_LINK = "LNK_ATTRIBUTE"; // This datatype classname indicates the datatype belongs to
															// the BaseEntity set with parent
	@NotNull
	@Size(max = 120)
	private String dttCode; // e.g. java.util.String

    @NotNull
    @Size(max = 120)
    private String className; // e.g. java.util.String

    @NotNull
    @Size(max = 120)
    private String typeName; // e.g. TEXT

    private String inputmask;

	public String component;

    /**
     * A fieldlist that stores the validations for this object.
     * <p>
     * Note that this is stored into a single object
     */

    @Column(name = "validation_list", length = 512)
    @Convert(converter = ValidationListConverter.class)
	private List<Validation> validationList = new CopyOnWriteArrayList<>();


    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    protected DataType() {
        super();
        // dummy for hibernate
    }

    public DataType(final Class clazz) {
        this(clazz, new ValidationList());
    }

    public DataType(final String className) {
        this(className, new ValidationList());
    }

    public DataType(final String className, final ValidationList aValidationList, final String name,
                    final String inputmask) {
        setDttCodeFromClassName(className);
		setClassName(className);
		setValidationList(aValidationList.getValidationList());
		setTypeName(name);
		setInputmask(inputmask);
	}

	public DataType(final String className, final ValidationList aValidationList, final String name,
					final String inputmask, final  String component) {
		setDttCodeFromClassName(className);
		setClassName(className);
		setValidationList(aValidationList.getValidationList());
		setTypeName(name);
		setInputmask(inputmask);
		setComponent(component);
	}

	public DataType(final String className, final ValidationList aValidationList, final String name) {
		this(className, aValidationList, name, "");
	}

    public void setDttCodeFromClassName(String str){
		String[] strs = str.split("\\.");
		String type;

		if (strs.length > 1){
			type = strs[strs.length-1];
		} else {
			type = strs[0];
		}
		if (str.contains("DTT")) {
			setDttCode(str);
		}else {
			setDttCode("DTT_" + type.toUpperCase());
		}
	}

	public DataType(final String className, final ValidationList aValidationList) {
		this(className, aValidationList, className);
	}

	public DataType(final Class clazz, final ValidationList aValidationList) {
		this(clazz.getCanonicalName(), aValidationList);
	}

	/**
	 * @return the validationList
	 */
	public List<Validation> getValidationList() {
		return validationList;
	}

	/**
	 * @param validationList
	 *            the validationList to set
	 */
	public void setValidationList(final List<Validation> validationList) {
		this.validationList = validationList;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * @return the name
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setTypeName(String name) {
		this.typeName = name;
	}

	/**
	 * @return the name
	 */
	public String getDttCode() {
		return this.dttCode;
	}

	/**
	 * @param code
	 *            the name to set
	 */
	public void setDttCode(String code) {
		this.dttCode = code;
	}
	/**
	 * @return the inputmask
	 */
	public String getInputmask() {
		return inputmask;
	}

	/**
	 * @param inputmask
	 *            the inputmask to set
	 */
	public void setInputmask(String inputmask) {
		this.inputmask = inputmask;
	}


	public void setClass(final Class clazz) {
		final String simpleClassName = clazz.getCanonicalName();
		setClassName(simpleClassName);
	}


	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	@Override
  public String toString() {
    return "DataType [className=" + className + ", dttCode=" + dttCode + ", inputmask=" + inputmask + ", typeName="
        + typeName + ", validationList=" + validationList + "]";
  }

	public static DataType getInstance(final String className) {
		final List<Validation> validationList = new CopyOnWriteArrayList<Validation>();
		ValidationList vlist = new ValidationList(validationList);
		return new DataType(className, vlist);
	}

	public static Object add(DataType dtype, Object v1, Object v2) {
		switch (dtype.getClassName()) {
		case "java.lang.Integer":
		case "Integer":
			return ((Integer)v1) + ((Integer)v2);
		case "java.lang.Long":
		case "Long":
			return ((Long)v1) + ((Long)v2);
		case "java.lang.Double":
		case "Double":
			return ((Double)v1) + ((Double)v2);
		case "org.javamoney.moneta.Money":
		default:
			return null;
		}
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result + ((dttCode == null) ? 0 : dttCode.hashCode());
    result = prime * result + ((inputmask == null) ? 0 : inputmask.hashCode());
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    result = prime * result + ((validationList == null) ? 0 : validationList.hashCode());
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
    DataType other = (DataType) obj;
    if (className == null) {
      if (other.className != null)
        return false;
    } else if (!className.equals(other.className))
      return false;
    if (dttCode == null) {
      if (other.dttCode != null)
        return false;
    } else if (!dttCode.equals(other.dttCode))
      return false;
    if (inputmask == null) {
      if (other.inputmask != null)
        return false;
    } else if (!inputmask.equals(other.inputmask))
      return false;
    if (typeName == null) {
      if (other.typeName != null)
        return false;
    } else if (!typeName.equals(other.typeName))
      return false;
    if (validationList == null) {
      if (other.validationList != null)
        return false;
    } else if (!validationList.equals(other.validationList))
      return false;
    return true;
  }

}
