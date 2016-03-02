package io.beanmapper.dynclass.model;

public class Organization {

    private Long id;

    private String name;

    private String contact;

    private String ourSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOurSecret() {
        return ourSecret;
    }

    public void setOurSecret(String ourSecret) {
        this.ourSecret = ourSecret;
    }
}
