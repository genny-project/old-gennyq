package life.genny.models;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.concurrent.Immutable;

import com.google.gson.annotations.Expose;

import io.vavr.Tuple;
import life.genny.qwanda.Context;
import life.genny.utils.VertxUtils;



//@Immutable
public class QuestionGroup implements Serializable {
	@Expose
	private String code;
	@Expose
	private String name;
	@Expose
	private String sourceAlias = null; // This is used to permit source setting.
	@Expose
	private String targetAlias = null; // This is used to permit target setting.
	@Expose
	private Set<QuestionTheme> questionThemes = new HashSet<QuestionTheme>();
	@Expose
	private Set<String> themeCodes;
	@Expose
	private Set<Context> contexts;

	/**
	 * static factory method for builder
	 */
	public static Builder builder(final String code) {
		return new Builder(code);
	}
	
	/**
	 * forces use of the Builder
	 */
	private QuestionGroup() {
	}

	public String getCode() {
		return code;
	}



	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sourceAlias
	 */
	public String getSourceAlias() {
		return sourceAlias;
	}

	/**
	 * @return the targetAlias
	 */
	public String getTargetAlias() {
		return targetAlias;
	}
	
	public void setTargetAlias(String targetAlias)
	{
		this.targetAlias = targetAlias;
	}

	public Set<QuestionTheme> getQuestionThemes() {
		return Collections.unmodifiableSet(questionThemes);
	}
	
	public Set<String> getThemeCodes() {
		return Collections.unmodifiableSet(themeCodes);
	}

	
	
	/**
	 * @return the contexts
	 */
	public Set<Context> getContexts() {
		return contexts;
	}





	/**
	 * more fluent Builder
	 */
	public static class Builder {
		private QuestionGroup managedInstance = new QuestionGroup();
		private Frame3.Builder parentBuilder;
		private Consumer<QuestionGroup> callback;

		
		public Builder(final String code) {
			managedInstance.code = code;
		}

		public Builder sourceAlias(final String aliasCode)
		{
			managedInstance.sourceAlias = aliasCode;
			return this;
		}
		
		public Builder targetAlias(final String aliasCode)
		{
			managedInstance.targetAlias = aliasCode;
			return this;
		}
		
		public Builder(Frame3.Builder b, Consumer<QuestionGroup> c, String code) {
			managedInstance.code = code;
			parentBuilder = b;
			callback = c;
		}
		
		public Builder(Frame3.Builder b, Consumer<QuestionGroup> c, String attributeCode, String questionName) {
			managedInstance.code = attributeCode;
			managedInstance.name = questionName;
			parentBuilder = b;
			callback = c;
		}


		public Builder(Frame3.Builder b, Consumer<QuestionGroup> c, QuestionGroup questionGroup) {
			managedInstance = questionGroup;
			parentBuilder = b;
			callback = c;
		}

				
		/**
		 * fluent setter for questionThemes in the list
		 * 
		 * @param none
		 * @return
		 */
		public QuestionTheme.Builder addTheme(Theme theme) {
			if (managedInstance.questionThemes == null) {
				managedInstance.questionThemes = new HashSet<QuestionTheme>();
			}
			Consumer<QuestionTheme> f = obj -> { managedInstance.questionThemes.add(obj);};
			return new QuestionTheme.Builder(this, f, theme);
		}

		/**
		 * fluent setter for questionThemes in the list
		 * 
		 * @param none
		 * @return
		 */
		public Builder addContext(Context context) {
			if (managedInstance.contexts == null) {
				managedInstance.contexts = new HashSet<Context>();
			}
			managedInstance.contexts.add(context);
			return this;
		}
		
		
		/**
		 * fluent setter for questionThemes in the list
		 * 
		 * @param none
		 * @return
		 * @throws Exception 
		 */
		public QuestionTheme.Builder addTheme(String themeCode,GennyToken serviceToken) throws Exception {
			Theme theme = VertxUtils.getObject(serviceToken.getRealm(), "", themeCode, Theme.class, serviceToken.getToken());
			if (theme != null) {
				if (managedInstance.questionThemes == null) {
					managedInstance.questionThemes = new HashSet<QuestionTheme>();
				}

			} else {
				throw new Exception("Could not load Theme "+themeCode+" - Does it exist yet?");
			}
			Consumer<QuestionTheme> f = obj -> { managedInstance.questionThemes.add(obj);};

			return new QuestionTheme.Builder(this, f, theme);
		}


		
		public QuestionGroup build() {
			return managedInstance;
		}
		
		
		public Frame3.Builder end() {
			callback.accept(managedInstance);
			return parentBuilder;
		}
	}
	
	public static QuestionGroup clone(QuestionGroup obj) {
		QuestionGroup newQuestionGroup = new QuestionGroup();
		newQuestionGroup.code = obj.getCode();
		newQuestionGroup.contexts = obj.getContexts();
		newQuestionGroup.name = obj.getName();
		newQuestionGroup.questionThemes = obj.questionThemes;
		newQuestionGroup.sourceAlias = obj.getSourceAlias();
		newQuestionGroup.targetAlias = obj.getTargetAlias();
		newQuestionGroup.themeCodes = obj.themeCodes;
		return  newQuestionGroup;
	}

	@Override
	public String toString() {
		return getCode();
	}
	
	
	
}