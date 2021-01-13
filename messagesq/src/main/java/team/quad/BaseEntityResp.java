package team.quad;

//import life.genny.models.attribute.EntityAttribute;
//import life.genny.models.entity.EntityEntity;
//import life.genny.models.entity.EntityQuestion;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BaseEntityResp implements Serializable {
        private String code;
        private String name;
        private boolean active;
         private Set<Object> baseEntityAttributes = new HashSet<>();
        private Set<Object> links = new HashSet<>();
        private Set<Object> questions = new HashSet<>(0);

        public BaseEntityResp( ) {
         }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Set<Object> getBaseEntityAttributes() {
            return baseEntityAttributes;
        }

        public void setBaseEntityAttributes(Set<Object> baseEntityAttributes) {
            this.baseEntityAttributes = baseEntityAttributes;
        }

        public Set<Object> getLinks() {
            return links;
        }

        public void setLinks(Set<Object> links) {
            this.links = links;
        }

        public Set<Object> getQuestions() {
            return questions;
        }

        public void setQuestions(Set<Object> questions) {
            this.questions = questions;
        }
}