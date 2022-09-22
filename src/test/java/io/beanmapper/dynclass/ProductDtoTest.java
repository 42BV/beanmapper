package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.Artist;
import io.beanmapper.dynclass.model.ArtistDto;
import io.beanmapper.dynclass.model.Asset;
import io.beanmapper.dynclass.model.Organization;
import io.beanmapper.dynclass.model.Product;
import io.beanmapper.dynclass.model.ProductDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProductDtoTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void prepareBeanmapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    void mapToDynamicProductDtoOrgOnlyIdName() throws Exception {
        Product product = createProduct(false);
        beanMapper = beanMapper.wrap()
                .setTargetClass(ProductDto.class)
                .downsizeTarget(Arrays.asList("id", "name", "organization.id", "organization.name"))
                .build();
        Object productDto = beanMapper.map(product);
        String json = new ObjectMapper().writeValueAsString(productDto);
        assertEquals("{\"id\":42,\"name\":\"Aller menscher\",\"organization\":{\"id\":1143,\"name\":\"My Org\"}}", json);
    }

    @Test
    void mapToDynamicProductDtoWithLists() throws Exception {
        Product product = createProduct(true);
        beanMapper = beanMapper.wrap()
                .setTargetClass(ProductDto.class)
                .downsizeTarget(Arrays.asList("id", "name", "assets.id", "assets.name", "artists"))
                .build();
        Object productDto = beanMapper.map(product);

        String expectedJson =
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
                        "}";
        compareJson(productDto, expectedJson);
    }

    @Test
    void mapList() throws Exception {
        List<Artist> artists = createArtists();
        beanMapper = beanMapper.wrap()
                .setTargetClass(ArtistDto.class)
                .downsizeTarget(Arrays.asList("id", "name"))
                .build();
        Object dto = beanMapper.map(artists);
        String json = new ObjectMapper().writeValueAsString(dto);
        assertEquals("[{\"id\":1141,\"name\":\"Artist 1\"},{\"id\":1142,\"name\":\"Artist 2\"}]", json);
    }

    @Test
    void mapListWithNestedEntries() throws Exception {
        List<Product> products = new ArrayList<Product>();
        products.add(createProduct(42L, true));
        products.add(createProduct(43L, true));
        beanMapper = beanMapper.wrap()
                .setTargetClass(ProductDto.class)
                .downsizeTarget(Arrays.asList("id", "assets.id"))
                .build();
        Object dto = beanMapper.map(products);
        String expectedJson =
                "[" +
                        "{\"id\":42,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}," +
                        "{\"id\":43,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}" +
                        "]";
        compareJson(dto, expectedJson);
    }

    private Product createProduct(boolean includeLists) {
        return createProduct(42L, includeLists);
    }

    private Product createProduct(Long productId, boolean includeLists) {
        Organization organization = new Organization() {{
            setId(1143L);
            setName("My Org");
            setContact("Henk");
        }};

        Product product = new Product() {{
            setId(productId);
            setName("Aller menscher");
            setUpc("12345678901");
            setInternalMemo("Secret message, not to be let out");
            setOrganization(organization);
        }};

        if (includeLists) {
            product.setAssets(createAssets());
            product.setArtists(createArtists());
        }

        return product;
    }

    private List<Asset> createAssets() {
        return new ArrayList<>() {{
            add(createAsset(1138L, "Track 1", "NL-123-ABCDEFGH"));
            add(createAsset(1139L, "Track 2", "NL-123-ABCDEFGI"));
            add(createAsset(1140L, "Track 3", "NL-123-ABCDEFGJ"));
        }};
    }

    private List<Artist> createArtists() {
        return new ArrayList<>() {{
            add(createArtist(1141L, "Artist 1"));
            add(createArtist(1142L, "Artist 2"));
        }};
    }

    private Asset createAsset(Long id, String name, String isrc) {
        return new Asset() {{
            setId(id);
            setName(name);
            setIsrc(isrc);
        }};
    }

    private Artist createArtist(long id, String name) {
        return new Artist() {{
            setId(id);
            setName(name);
        }};
    }

    private void compareJson(Object object, String expectedJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(object);

        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode expectedJsonNode = objectMapper.readTree(expectedJson);
        assertEquals(jsonNode, expectedJsonNode);
    }

}
