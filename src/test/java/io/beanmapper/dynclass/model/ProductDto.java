package io.beanmapper.dynclass.model;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class ProductDto {

    public Long id;

    public String name;

    public String upc;

    @BeanCollection(elementType = AssetDto.class)
    public List<AssetDto> assets;

    @BeanCollection(elementType = ArtistDto.class)
    public List<ArtistDto> artists;

    public OrganizationDto organization;

}
