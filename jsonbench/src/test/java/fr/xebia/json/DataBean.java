package fr.xebia.json;

import java.util.Date;

/**
 * Simple bean de démonstration pour les tests de sérialisation JSON.
 * 
 * @author slm
 * 
 */
public class DataBean {

    private String type;

    private long version = 1;

    private Date date;

    private boolean error;

    private int id;

    public DataBean() {
        date = new Date();
    }

    public DataBean(String type) {
        date = new Date();
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataBean [date=").append(date).append(", error=").append(error).append(", id=").append(id).append(", type=")
                .append(type).append(", version=").append(version).append("]");
        return builder.toString();
    }
}
