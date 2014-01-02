package es.cenobit.struts2.json.example.objects;

import java.util.Date;

public class Bar {

    private Long id;
    private String title;
    private String description;
    private Date pubDate;
    private Boolean status;

    public Bar(Long id, String title, String description, Date pubDate, Boolean status) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
