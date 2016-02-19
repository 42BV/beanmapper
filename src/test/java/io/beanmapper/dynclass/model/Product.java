package io.beanmapper.dynclass.model;

import io.beanmapper.dynclass.model.Artist;
import io.beanmapper.dynclass.model.Asset;

import java.util.List;

public class Product {

    private Long id;

    private String name;

    private String upc;

    private String internalMemo;

    private List<Asset> assets;

    private List<Artist> artists;

    private Organization organization;

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

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
