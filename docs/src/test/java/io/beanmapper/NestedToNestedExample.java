package io.beanmapper;

import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.BeanMapperBuilder;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class NestedToNestedExample {

    @Test
    public void mapNestedToNested() {
        Pet pet = new Pet("Loebas", LocalDate.now(), 1L, "Dog");

        PetResult petResult = new BeanMapperBuilder()
                .addPackagePrefix(PetTypeResult.class)
                .build()
                .map(pet, PetResult.class);

        assertEquals("Loebas", petResult.name);
        assertEquals("Dog", petResult.type.name);
    }

    public static class Pet {
        public String nickname;
        public LocalDate birthDate;
        public PetType type;

        public Pet(String nickname, LocalDate birthDate, Long typeId, String typeName) {
            this.nickname = nickname;
            this.birthDate = birthDate;
            this.type = new PetType();
            this.type.name = typeName;
            this.type.id = typeId;
        }
    }

    public static class PetType {
        public Long id;
        public String name;
    }

    public static class PetResult {
        @BeanProperty(name = "nickname")
        public String name;
        public PetTypeResult type;
    }

    public static class PetTypeResult {
        public String name;
    }
}
