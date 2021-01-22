package life.genny.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.Type;
import org.jboss.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.models.datatype.DataType;

@Embeddable
@RegisterForReflection
public class Value implements Serializable,Comparable<Value> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(Value.class);

	public DataType dataType;

	public Double valueDouble;
	public Integer valueInteger;
	public Long valueLong;
	public LocalDateTime valueDateTime;
	public LocalDate valueDate;
	public LocalTime valueTime;
	public Boolean valueBoolean;

//	  @Column(name="valueString" , length = 65535, columnDefinition="varchar")
	  @Type(type="text")
	public String valueString;

	public Boolean expired = false;

	/**
	 * Store the Refused boolean value if the value was refused
	 */
	public Boolean refused = false;

	/**
	 * Store the relative credibility of the value
	 */
	public Double weight=0.0;

	public Value() {
		this.dataType = new DataType(String.class.getCanonicalName()); // default
	}

	public Value(Object value) {
		dataType = DataType.getInstance(value.getClass().getName());
		setValue(value);
	}

    public Value(Object value, DataType dataType) {
        this.dataType = dataType;
        setValue(dataType);
    }


	@SuppressWarnings("unchecked")
	@JsonbTransient
	@Transient
	public <T> T getValue() {

		switch (dataType.getClassName()) {
		case "java.lang.String":
		case "String":
			return (T) valueString;
		case "java.lang.Integer":
		case "Integer":
			return (T) valueInteger;
		case "java.time.LocalDateTime":
		case "LocalDateTime":
			return (T) valueDateTime;
		case "java.time.LocalDate":
		case "LocalDate":
			return (T) valueDate;
		case "java.time.LocalTime":
		case "LocalTime":
			return (T) valueTime;
		case "java.lang.Long":
		case "Long":
			return (T) valueLong;
		case "java.lang.Double":
		case "Double":
			return (T) valueDouble;
		case "java.lang.Boolean":
		case "Boolean":
			return (T) valueBoolean;

		default:
			return (T) valueString;
		}

	}

	
	
	
	@JsonbTransient
	@Transient
	public <T> void setValue(final Object value) {
		
		if (value == null) {
			return;
		}
		
		if (value instanceof String) {
			String result = (String) value;
			try {
				if (dataType.getClassName().equalsIgnoreCase(String.class.getCanonicalName())) {
					valueString =result;
				} else {
					setValue(convertFromString(result));
				}
			} catch (Exception e) {
				log.error("Conversion Error :" + value + " for dataType "+dataType + " and SourceCode:"						);
			}
		} else {
		
		switch (dataType.getClassName()) {
		case "java.lang.String":
		case "String":
			valueString = (String) value;
			break;
		case "java.lang.Integer":
		case "Integer":
			valueInteger = (Integer) value;
			break;
		case "java.time.LocalDateTime":
		case "LocalDateTime":
			valueDateTime = (LocalDateTime) value;
			break;
		case "java.time.LocalDate":
		case "LocalDate":
			valueDate = (LocalDate) value;
			break;
		case "java.lang.Long":
		case "Long":
			valueLong = (Long) value;
			break;
		case "java.lang.Double":
		case "Double":
			valueDouble = (Double) value;
			break;
		case "java.lang.Boolean":
		case "Boolean":
			valueBoolean = (Boolean) value;
			break;

		
		default:
			valueString = (String) value;
			break;
		}
		}

	}

	@JsonbTransient
	@Transient
	public <T> T convertFromString(final String value) throws ParseException
	{
		switch (dataType.getClassName()) {
		case "java.lang.String":
			valueString = value;
			return (T)valueString;
		case "java.lang.Integer":
		case "Integer":
			final Integer integer = Integer.parseInt(value);
			valueInteger = integer;
			return (T)valueInteger;
		case "java.time.LocalDateTime":
		case "LocalDateTime":
			List<String> formatStrings = Arrays.asList("yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss",
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			for (String formatString : formatStrings) {
				try {
					Date olddate = new SimpleDateFormat(formatString).parse(value);
					final LocalDateTime dateTime = olddate.toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();
					valueDateTime = dateTime;
					return (T)valueDateTime;
					
				} catch (ParseException e) {
				}

			}
			break;
		case "java.time.LocalDate":
		case "LocalDate":
			Date olddate = null;
			try {
				olddate = DateUtils.parseDate(value, "M/y", "yyyy-MM-dd", "yyyy/MM/dd",
						"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			} catch (java.text.ParseException e) {
				olddate = DateUtils.parseDate(value, "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss",
						"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			}
			final LocalDate date = olddate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			valueDate = date;
			return (T) valueDate;
		case "java.time.LocalTime":
		case "LocalTime":
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			final LocalTime time= LocalTime.parse(value, formatter);
			valueTime = time;
			return (T)valueTime;
		case "java.lang.Long":
		case "Long":
			final Long l = Long.parseLong(value);
			valueLong = l;
			return (T)valueLong;
		case "java.lang.Double":
		case "Double":
			final Double d = Double.parseDouble(value);
			valueDouble = d;
			return (T)valueDouble;
		case "java.lang.Boolean":
		case "Boolean":
			final Boolean b = Boolean.parseBoolean(value);
			valueBoolean = b;
			return (T)valueBoolean;

		
		default:
			valueString = (String) value;
			break;
		}
		return (T)valueString;
	}
	
	@Override
  public String toString() {
    return "Value [dataType=" + dataType + ", expired=" + expired + ", refused=" + refused + ", valueBoolean="
        + valueBoolean + ", valueDate=" + valueDate + ", valueDateTime=" + valueDateTime + ", valueDouble="
        + valueDouble + ", valueInteger=" + valueInteger + ", valueLong=" + valueLong + ", valueString="
        + valueString + ", valueTime=" + valueTime + ", weight=" + weight + "]";
  }

	@Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
    result = prime * result + ((expired == null) ? 0 : expired.hashCode());
    result = prime * result + ((refused == null) ? 0 : refused.hashCode());
    result = prime * result + ((valueBoolean == null) ? 0 : valueBoolean.hashCode());
    result = prime * result + ((valueDate == null) ? 0 : valueDate.hashCode());
    result = prime * result + ((valueDateTime == null) ? 0 : valueDateTime.hashCode());
    result = prime * result + ((valueDouble == null) ? 0 : valueDouble.hashCode());
    result = prime * result + ((valueInteger == null) ? 0 : valueInteger.hashCode());
    result = prime * result + ((valueLong == null) ? 0 : valueLong.hashCode());
    result = prime * result + ((valueString == null) ? 0 : valueString.hashCode());
    result = prime * result + ((valueTime == null) ? 0 : valueTime.hashCode());
    result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
    Value other = (Value) obj;
    if (dataType == null) {
      if (other.dataType != null)
        return false;
    } else if (!dataType.equals(other.dataType))
      return false;
    if (expired == null) {
      if (other.expired != null)
        return false;
    } else if (!expired.equals(other.expired))
      return false;
    if (refused == null) {
      if (other.refused != null)
        return false;
    } else if (!refused.equals(other.refused))
      return false;
    if (valueBoolean == null) {
      if (other.valueBoolean != null)
        return false;
    } else if (!valueBoolean.equals(other.valueBoolean))
      return false;
    if (valueDate == null) {
      if (other.valueDate != null)
        return false;
    } else if (!valueDate.equals(other.valueDate))
      return false;
    if (valueDateTime == null) {
      if (other.valueDateTime != null)
        return false;
    } else if (!valueDateTime.equals(other.valueDateTime))
      return false;
    if (valueDouble == null) {
      if (other.valueDouble != null)
        return false;
    } else if (!valueDouble.equals(other.valueDouble))
      return false;
    if (valueInteger == null) {
      if (other.valueInteger != null)
        return false;
    } else if (!valueInteger.equals(other.valueInteger))
      return false;
    if (valueLong == null) {
      if (other.valueLong != null)
        return false;
    } else if (!valueLong.equals(other.valueLong))
      return false;
    if (valueString == null) {
      if (other.valueString != null)
        return false;
    } else if (!valueString.equals(other.valueString))
      return false;
    if (valueTime == null) {
      if (other.valueTime != null)
        return false;
    } else if (!valueTime.equals(other.valueTime))
      return false;
    if (weight == null) {
      if (other.weight != null)
        return false;
    } else if (!weight.equals(other.weight))
      return false;
    return true;
  }

	@Override
	public int compareTo(Value obj) {
		if (this == obj)
			return 0;

		switch (dataType.getClassName()) {
		case "java.lang.Integer":
		case "Integer":
			return Integer.compare(valueInteger, obj.valueInteger); 
		case "java.time.LocalDateTime":
		case "LocalDateTime":
			return this.valueDateTime.compareTo(obj.valueDateTime);
		case "java.time.LocalTime":
		case "LocalTime":
			return this.valueTime.compareTo(obj.valueTime);
		case "java.lang.Long":
		case "Long":
			return this.valueLong.compareTo(obj.valueLong);
		case "java.lang.Double":
		case "Double":
			return this.valueDouble.compareTo(obj.valueDouble);
		case "java.lang.Boolean":
		case "Boolean":
			return this.valueBoolean.compareTo(obj.valueBoolean);
		case "java.time.LocalDate":
		case "LocalDate":
			return this.valueDate.compareTo(obj.valueDate);
		case "org.javamoney.moneta.Money":
		case "java.lang.String":
		default:
			return this.valueString.compareTo(obj.valueString);

		}

	}
//
//	public class compare implements Comparator<Value> {
//		 
//	    @Override
//	    public int compare(Value first, Value second) {
//	       return first.compareTo(second);
//	    }
//	 
//	}
	

	static public Value getValueFromJsonObject(JsonObject json)
	{
		Value value = new Value();
		value.valueString = getString(json);
		value.valueBoolean = getBoolean(json);
		value.valueLong = getLong(json);
		value.valueDouble = getDouble(json);
		value.valueDateTime = getDateTime(json);
		value.valueDate = getDate(json);
		value.valueTime = getTime(json);
		value.weight = getWeight(json);
		
		return value;
	}
	

	static public  String getString(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueString");
		if (je != null) {
			return je.getAsString();
		} else {
			return null;
		}
	}
	

	static public  Boolean getBoolean(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueBoolean");
		if (je != null) {
			return je.getAsBoolean();
		} else {
			return null;
		}
	}
	

	static public  Double getDouble(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueDouble");
		if (je != null) {
			return je.getAsDouble();
		} else {
			return null;
		}
	}
	
	static public  Double getWeight(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("weight");
		if (je != null) {
			return je.getAsDouble();
		} else {
			return 0.0;
		}
	}
	
	
	
	static public  Long getLong(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueLong");
		if (je != null) {
			return je.getAsLong();
		} else {
			return null;
		}
	}
	

	static public  Integer getInteger(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueInteger");
		if (je != null) {
			return je.getAsInt();
		} else {
			return null;
		}
	}
	
	
	static public  LocalDateTime getDateTime(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueDateTime");
		if (je != null) {
			return Value.getDateTime(je);
		} else {
			return null;
		}
	}
	

	static public  LocalDate getDate(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueDate");
		if (je != null) {
			return Value.getDate(je);
		} else {
			return null;
		}
	}
	
	static public  LocalTime getTime(JsonObject jsonObject)
	{
		JsonElement je = jsonObject.get("valueTime");
		if (je != null) {
			return Value.getTime(je);
		} else {
			return null;
		}
	}
	
	static public LocalDateTime getDateTime(JsonElement jsonElement) {
		if (jsonElement == null) {
			return null;
		}
		String value = jsonElement.getAsString();
		
		List<String> formatStrings = Arrays.asList("yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss",
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ","yyyy-MM-dd'T'HH:mm:ss.SSS","yyyy-MM-dd'T'HH:mm:ss.SS","yyyy-MM-dd'T'HH:mm:ss.S");
		for (String formatString : formatStrings) {
			try {
				Date olddate = new SimpleDateFormat(formatString).parse(value);
				final LocalDateTime dateTime = olddate.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				return dateTime;
				
			} catch (ParseException e) {
			}

		}
		return null;
	}
	
	static public LocalDate getDate(JsonElement jsonElement) {
		if (jsonElement == null) {
			return null;
		}
		String value = jsonElement.getAsString();
		
		Date olddate = null;
		try {
			olddate = DateUtils.parseDate(value, "M/y", "yyyy-MM-dd", "yyyy/MM/dd",
					"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		} catch (java.text.ParseException e) {
			try {
				olddate = DateUtils.parseDate(value, "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss",
						"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		final LocalDate date = olddate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return date;
	}
	
	static public LocalTime getTime(JsonElement jsonElement) {
		if (jsonElement == null) {
			return null;
		}
		String value = jsonElement.getAsString();
		
		Date olddate = null;
		try {
			olddate = DateUtils.parseDate(value, "HH:mm:ss", "HH:mm:ss.SSSZ","HH:mm:ss.S","HH:mm:ss.SS","HH:mm:ss.SSS");
		} catch (java.text.ParseException e) {
		}
		final LocalTime time = olddate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		
		return time;
	}



	public boolean isAnswered() {
		if (!StringUtils.isBlank(valueString)) {
			return true;
		}
			if ((valueBoolean != null)||(valueInteger != null) || (valueDouble != null) || (valueDate != null) || (valueDateTime != null)||(valueTime != null)) {
				return true;
			} else {
				return false;
			}
	}
	

}
