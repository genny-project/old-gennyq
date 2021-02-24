package life.genny.qwanda;

public interface GennyInterface {
	String singleQuoteSeparator = "\'";
	Long getId();
	String getCode();
	boolean isChanged(GennyInterface obj);
	void updateById(long id);
}
