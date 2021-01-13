package team.quad;



import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BaseEntityReq implements Serializable {
        public String code;
        public String name;
        public boolean active;
        public Set<Object> baseEntityAttributes = new HashSet<>();
        public Set<Object> links = new HashSet<>();
        public Set<Object> questions = new HashSet<>(0);

        public BaseEntityReq( ) {
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