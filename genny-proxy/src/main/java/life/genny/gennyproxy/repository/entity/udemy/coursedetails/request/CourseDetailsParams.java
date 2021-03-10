package life.genny.gennyproxy.repository.entity.udemy.coursedetails.request;

public class CourseDetailsParams {

    private String category;
    private String subcategory;
    private String ordering;
    private int rating;
    private int  page;

    private int page_size;
    private String search;

    public static class Builder {
        CourseDetailsParams courseDetailsParams = new CourseDetailsParams();

        public Builder withCategory(String category) {
            courseDetailsParams.category = category;
            return this;
        }

        public Builder  withSubcategory(String subcategory) {
            courseDetailsParams.subcategory = subcategory;
            return this;
        }

        public Builder withOrdering(String ordering) {
            courseDetailsParams.ordering = ordering;
            return this;
        }

        public Builder  withRating(int rating) {
            courseDetailsParams.rating = rating;
            return this;
        }

        public Builder withPage(int page) {
            courseDetailsParams.page = page;
            return this;
        }

        public Builder withPage_size(int page_size) {
            courseDetailsParams.page_size = page_size;
            return this;
        }

        public Builder withSearch(String search) {
            courseDetailsParams.search = search;
            return this;
        }

        public CourseDetailsParams build(){
            return courseDetailsParams;
        }

    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getOrdering() {
        return ordering;
    }

    public int getRating() {
        return rating;
    }

    public int getPage() {
        return page;
    }

    public int getPage_size() {
        return page_size;
    }

    public String getSearch() {
        return search;
    }


}
