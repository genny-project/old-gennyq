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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import life.genny.qwanda.GennyInterface;
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
public class Validation extends PanacheEntity implements GennyInterface {

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
	return Objects.hash(code, realm);
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (!(obj instanceof Validation))
		return false;
	Validation other = (Validation) obj;
	return Objects.equals(code, other.code) && Objects.equals(realm, other.realm);
}

public static Validation findByCode(String code) {
	Validation item = null;
	
	try {
		item = find("code", code.toUpperCase().trim()).firstResult();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return item;
}

@Override
public Long getId() {
	return id;
}

@Override
public String getCode() {
	return code;
}

@Override
public boolean isChanged(GennyInterface obj)
{
	boolean ret = false;
	Validation other = (Validation) obj;
	
	

   Set union = Stream.concat(selectionBaseEntityGroupList.stream(),other.selectionBaseEntityGroupList.stream()).collect(Collectors.toSet()); 
	if ((union.size()!=selectionBaseEntityGroupList.size())||(union.size()!=other.selectionBaseEntityGroupList.size())) {
		return true;
	}
	return !Objects.equals(code, other.code) && Objects.equals(realm, other.realm)  && Objects.equals(options, other.options)  && Objects.equals(multiAllowed, other.multiAllowed)
			 && Objects.equals(recursiveGroup, other.recursiveGroup)  && Objects.equals(regex, other.regex)  && Objects.equals(name, other.name);


}

	String convertToSQLStr(String value) {
		String resultStr = null;
		if (value != null) {
			resultStr = singleQuoteSeparator + value + singleQuoteSeparator;
		}
		return resultStr;
	}

	@Override
	public void updateById(long id) {
		StringBuilder selectionBaseEntityGroupListStr = new StringBuilder();
		Optional.ofNullable(this.selectionBaseEntityGroupList).ifPresent(element->
				selectionBaseEntityGroupListStr.append(String.join(",", element)).append(","));

		if (selectionBaseEntityGroupListStr.length() == 0)
			selectionBaseEntityGroupListStr.append(",");

		String updateStatement = "update from Validation" + " " +
				"set multiAllowed=" + this.multiAllowed + ", " +
				"name=" + convertToSQLStr(this.name) + ", " +
				"options=" + convertToSQLStr(this.options)+ ", " +
				"recursiveGroup=" + this.recursiveGroup + ", " +
				"regex=" + convertToSQLStr(this.regex) + ", " +
				"selection_grp=" + singleQuoteSeparator + selectionBaseEntityGroupListStr + singleQuoteSeparator + ", " +
				"updated=" + singleQuoteSeparator + this.created + singleQuoteSeparator + " " +
				"where id=?1";
		Validation.update(updateStatement,id);
	}
}
