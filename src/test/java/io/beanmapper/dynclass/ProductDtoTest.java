package io.beanmapper.dynclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductDtoTest {

    private BeanMapper beanMapper;

    @Before
    public void prepareBeanmapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    public void mapToDynamicProductDtoOrgOnlyIdName() throws Exception {
        Product product = createProduct(false);
        beanMapper = beanMapper.config()
                .setTargetClass(ProductDto.class)
                .limitTarget(Arrays.asList("id", "name", "organization.id", "organization.name"))
                .build();
        Object productDto = beanMapper.map(product);
        String json = new ObjectMapper().writeValueAsString(productDto);
        assertEquals("{\"id\":42,\"name\":\"Aller menscher\",\"organization\":{\"id\":1143,\"name\":\"My Org\"}}", json);
    }

    @Test
    public void mapToDynamicProductDtoWithLists() throws Exception {
        Product product = createProduct(true);
        beanMapper = beanMapper.config()
                .setTargetClass(ProductDto.class)
                .limitTarget(Arrays.asList("id", "name", "assets.id", "assets.name", "artists"))
                .build();
        Object productDto = beanMapper.map(product);
        String json = new ObjectMapper().writeValueAsString(productDto);
        assertEquals(
                "{\"id\":42,\"name\":\"Aller menscher\"," +
                    "\"assets\":[" +
                        "{\"id\":1138,\"name\":\"Track 1\"}," +
                        "{\"id\":1139,\"name\":\"Track 2\"}," +
                        "{\"id\":1140,\"name\":\"Track 3\"}" +
                    "]," +
                    "\"artists\":[" +
                        "{\"id\":1141,\"name\":\"Artist 1\"}," +
                        "{\"id\":1142,\"name\":\"Artist 2\"}" +
                    "]" +
                "}", json);
    }

    @Test
    public void mapList() throws Exception {
        List<Artist> artists = createArtists();
        beanMapper = beanMapper.config()
                .setTargetClass(ArtistDto.class)
                .limitTarget(Arrays.asList("id", "name"))
                .build();
        Object dto = beanMapper.map(artists);
        String json = new ObjectMapper().writeValueAsString(dto);
        assertEquals("[{\"id\":1141,\"name\":\"Artist 1\"},{\"id\":1142,\"name\":\"Artist 2\"}]", json);
    }

    @Test
    public void mapListWithNestedEntries() throws Exception {
        List<Product> products = new ArrayList<Product>();
        products.add(createProduct(42L, true));
        products.add(createProduct(43L, true));
        beanMapper = beanMapper.config()
                .setTargetClass(ProductDto.class)
                .limitTarget(Arrays.asList("id", "assets.id"))
                .build();
        Object dto = beanMapper.map(products);
        String json = new ObjectMapper().writeValueAsString(dto);
        assertEquals("" +
                "[" +
                    "{\"id\":42,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}," +
                    "{\"id\":43,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}" +
                "]", json);
    }

    private Product createProduct(boolean includeLists) {
        return createProduct(42L, includeLists);
    }

    private Product createProduct(Long productId, boolean includeLists) {
        Product product = new Product();
        product.setId(productId);
        product.setName("Aller menscher");
        product.setUpc("12345678901");
        product.setInternalMemo("Secret message, not to be let out");

        if (includeLists) {
            product.setAssets(createAssets());
            product.setArtists(createArtists());
        }

        Organization organization = new Organization();
        organization.setId(1143L);
        organization.setName("My Org");
        organization.setContact("Henk");
        product.setOrganization(organization);

        return product;
    }

    private List<Asset> createAssets() {
        List<Asset> assets = new ArrayList<Asset>();
        assets.add(createAsset(1138L, "Track 1", "NL-123-ABCDEFGH"));
        assets.add(createAsset(1139L, "Track 2", "NL-123-ABCDEFGI"));
        assets.add(createAsset(1140L, "Track 3", "NL-123-ABCDEFGJ"));
        return assets;
    }

    private List<Artist> createArtists() {
        List<Artist> artists = new ArrayList<Artist>();
        artists.add(createArtist(1141L, "Artist 1"));
        artists.add(createArtist(1142L, "Artist 2"));
        return artists;
    }

    private Asset createAsset(Long id, String name, String isrc) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(name);
        asset.setIsrc(isrc);
        return asset;
    }

    private Artist createArtist(long id, String name) {
        Artist artist = new Artist();
        artist.setId(id);
        artist.setName(name);
        return artist;
    }

}
