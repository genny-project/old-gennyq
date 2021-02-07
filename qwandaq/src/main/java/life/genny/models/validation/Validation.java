/*
 * (C) Copyright 2017,2020 GADA Technology (http://www.gada.io/) and others.
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

package life.genny.models.validation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.PatternSyntaxException;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.converter.StringListConverter;
import life.genny.utils.LocalDateTimeAdapter;

/**
 * Validation represents a distinct abstract Validation Representation in the
 * Qwanda library. The validations are applied to values. In addition to the
 * extended CoreEntity this information includes:
 * <ul>
 * <li>Regex
 * </ul>
 * <p>
 * 
 * <p>
 * 
 * 
 * @author Adam Crow
 * @author Byron Aguirre
 * @version %I%, %G%
 * @since 1.0
 */

@Entity
@Cacheable
@Table(name = "qvalidation")
@RegisterForReflection
public class Validation extends PanacheEntity {

	private static final Logger log = Logger.getLogger(Validation.class);
	
	private static final String DEFAULT_CODE_PREFIX = "VLD_";
	private static final String REGEX_CODE = "[A-Z]{3}\\_[A-Z0-9\\.\\-\\@\\_]*";

	private static final String REGEX_NAME = "[\\pL0-9/\\:\\ \\_\\.\\,\\?\\>\\<\\%\\$\\&\\!\\*" + ""
			+ "\\[\\]\\'\\-\\@\\(\\)]+.?";
	private static final String REGEX_REALM = "[a-zA-Z0-9]+";
	private static final String DEFAULT_REALM = "genny";
	
	
	private static final String DEFAULT_REGEX = ".*";

	@NotEmpty
	@JsonbTransient
	@Pattern(regexp = REGEX_REALM, message = "Must be valid Realm Format!")
	public String realm=DEFAULT_REALM;

	@NotNull
	@Size(max = 64)
	@Pattern(regexp = REGEX_CODE, message = "Must be valid Code!")
	@Column(name = "code", updatable = false, nullable = false)
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


	/**
	 * A field that stores the validation regex.
	 * <p>
	 * Note that this regex needs to be applied to the complete value (Not partial).
	 */
	@NotNull
	@Column(name = "regex", length = 512, updatable = true, nullable = false)
	public String regex;

	@Column(name = "selection_grp", length = 512, updatable = true, nullable = true)
	@Convert(converter = StringListConverter.class)
	public List<String> selectionBaseEntityGroupList;

	public Boolean recursiveGroup = false;

	public Boolean multiAllowed = false;

	@Column(name = "options", length = 2048, updatable = true, nullable = true)
	public String options;

	/**
	 * Constructor.
	 * 
	 */
	@SuppressWarnings("unused")
	public Validation() {
	}

	public Validation(String code, String name, String aRegex) throws PatternSyntaxException {
		this.code = code;
		this.name = name;
		setRegex(aRegex);
	}

	public Validation(String code, String name, String aRegex, String aOptions) throws PatternSyntaxException {
		this.code = code;
		this.name = name;
		setRegex(aRegex);
		setOptions(aOptions);
	}

	public Validation(String code, String name, String aSelectionBaseEntityGroup, Boolean recursive,
			Boolean multiAllowed) throws PatternSyntaxException {
		this.code = code;
		this.name = name;
		setRegex(DEFAULT_REGEX);
		List<String> aSelectionBaseEntityGroupList = new CopyOnWriteArrayList<String>();
		aSelectionBaseEntityGroupList.add(aSelectionBaseEntityGroup);
		setSelectionBaseEntityGroupList(aSelectionBaseEntityGroupList);
		setMultiAllowed(multiAllowed);
	}

	public Validation(String aCode, String aName, String aSelectionBaseEntityGroup, Boolean recursive, Boolean multiAllowed,String aOptions) throws PatternSyntaxException
	{
		this.code = aCode;
		this.name = aName;
		this.recursiveGroup = recursive;
		setRegex(DEFAULT_REGEX);
		List<String> aSelectionBaseEntityGroupList = new CopyOnWriteArrayList<String>();
		aSelectionBaseEntityGroupList.add(aSelectionBaseEntityGroup);
		setSelectionBaseEntityGroupList(aSelectionBaseEntityGroupList);
		setMultiAllowed(multiAllowed);
		setOptions(aOptions);
	}

	public Validation(String code, String name, List<String> aSelectionBaseEntityGroupList, Boolean recursive,
			Boolean multiAllowed) throws PatternSyntaxException {
		this(code,name,aSelectionBaseEntityGroupList,recursive,multiAllowed,null);
	}

	public Validation(String code, String name, List<String> aSelectionBaseEntityGroupList, Boolean recursive,
			Boolean multiAllowed, String aOptions) throws PatternSyntaxException {
		this.code = code;
		this.name = name;

		setRegex(DEFAULT_REGEX);
		setSelectionBaseEntityGroupList(aSelectionBaseEntityGroupList);
		setMultiAllowed(multiAllowed);
		setOptions(aOptions);
	}

	/**
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(String regex) throws PatternSyntaxException {
		if (regex != null) {
			validateRegex(regex); // confirm the regex is valid, if invalid throws PatternSyntaxException
		} else {
			regex = ".*";
		}
		this.regex = regex;
	}

	/**
	 * @return the selectionBaseEntityGroup
	 */
	public List<String> getSelectionBaseEntityGroupList() {
		return selectionBaseEntityGroupList;
	}

	/**
	 * @param selectionBaseEntityGroup the selectionBaseEntityGroup to set
	 */
	public void setSelectionBaseEntityGroupList(List<String> selectionBaseEntityGroup) {
		this.selectionBaseEntityGroupList = selectionBaseEntityGroup;
	}

	/**
	 * @return the recursiveGroup
	 */
	public Boolean getRecursiveGroup() {
		return recursiveGroup;
	}

	/**
	 * @param recursiveGroup the recursiveGroup to set
	 */
	public void setRecursiveGroup(Boolean recursiveGroup) {
		this.recursiveGroup = recursiveGroup;
	}

	/**
	 * @return the multiAllowed
	 */
	public Boolean getMultiAllowed() {
		return multiAllowed;
	}

	/**
	 * @param multiAllowed the multiAllowed to set
	 */
	public void setMultiAllowed(Boolean multiAllowed) {
		this.multiAllowed = multiAllowed;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getRealm() {
		return this.realm;
	}

	/**
	 * @param regex
	 */
	static public void validateRegex(String regex) {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(regex);
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
    return "Validation [code=" + code + ", created=" + created + ", multiAllowed=" + multiAllowed + ", name=" + name
        + ", options=" + options + ", realm=" + realm + ", recursiveGroup=" + recursiveGroup + ", regex="
        + regex + ", selectionBaseEntityGroupList=" + selectionBaseEntityGroupList + ", updated=" + updated
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((multiAllowed == null) ? 0 : multiAllowed.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((options == null) ? 0 : options.hashCode());
    result = prime * result + ((realm == null) ? 0 : realm.hashCode());
    result = prime * result + ((recursiveGroup == null) ? 0 : recursiveGroup.hashCode());
    result = prime * result + ((regex == null) ? 0 : regex.hashCode());
    result = prime * result
        + ((selectionBaseEntityGroupList == null) ? 0 : selectionBaseEntityGroupList.hashCode());
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
    Validation other = (Validation) obj;
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
    if (multiAllowed == null) {
      if (other.multiAllowed != null)
        return false;
    } else if (!multiAllowed.equals(other.multiAllowed))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (options == null) {
      if (other.options != null)
        return false;
    } else if (!options.equals(other.options))
      return false;
    if (realm == null) {
      if (other.realm != null)
        return false;
    } else if (!realm.equals(other.realm))
      return false;
    if (recursiveGroup == null) {
      if (other.recursiveGroup != null)
        return false;
    } else if (!recursiveGroup.equals(other.recursiveGroup))
      return false;
    if (regex == null) {
      if (other.regex != null)
        return false;
    } else if (!regex.equals(other.regex))
      return false;
    if (selectionBaseEntityGroupList == null) {
      if (other.selectionBaseEntityGroupList != null)
        return false;
    } else if (!selectionBaseEntityGroupList.equals(other.selectionBaseEntityGroupList))
      return false;
    if (updated == null) {
      if (other.updated != null)
        return false;
    } else if (!updated.equals(other.updated))
      return false;
    return true;
  }
}
