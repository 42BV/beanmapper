package io.beanmapper.dynclass.model;

public class Asset {

    private Long id;

    private String name;

    private String isrc;

    private String internalMemo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInternalMemo() {
        return internalMemo;
    }

    public void setInternalMemo(String internalMemo) {
        this.internalMemo = internalMemo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }
}
