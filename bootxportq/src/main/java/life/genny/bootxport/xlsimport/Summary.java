package life.genny.bootxport.xlsimport;

public class Summary {
    private int invalid;
    private int total;
    private int skipped;
    private int newItem;
    private int updated;

    public int getInvalid() {
        return invalid;
    }

    public int getTotal() {
        return total;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getNewItem() {
        return newItem;
    }

    public int getUpdated() {
        return updated;
    }

    public Summary() {
        this.invalid = 0;
        this.total = 0;
        this.skipped = 0;
        this.newItem = 0;
        this.updated = 0;
    }

    public void addInvalid() {
        this.invalid += 1;
    }

    public void addTotal() {
        this.total += 1;
    }

    public void addSkipped() {
        this.skipped += 1;
    }

    public void addNew() {
        this.newItem += 1;
    }

    public void addUpdated() {
        this.updated += 1;
    }
}
