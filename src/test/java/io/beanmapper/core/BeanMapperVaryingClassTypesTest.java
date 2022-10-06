package io.beanmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

class BeanMapperVaryingClassTypesTest {

    @Test
    void sourceHasDifferentAccessorClassTypes() {
        SourceWithStringFieldAndLongAccessors source = new SourceWithStringFieldAndLongAccessors() {{
            setValue(41L);
        }};
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        TargetWithLongField target = beanMapper.map(source, new TargetWithLongField());
        assertEquals((Long) 42L, target.value);
    }

    @Test
    void listHasNoFieldProperty() {
        SourceWithList source = new SourceWithList();
        source.list.add("Alpha");
        TargetWithListButNoFieldProperty target = new TargetWithListButNoFieldProperty();
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        target = beanMapper.map(source, target);
        assertEquals(source.list, target.someOtherListName);
    }

    @Test
    void listFromStringToLongToString() {
        SourceWithList source = new SourceWithList();
        source.list.add("42");
        TargetWithDifferentSetterAndFieldTypes target = new TargetWithDifferentSetterAndFieldTypes();
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        target = beanMapper.map(source, target);
        assertEquals(target.someOtherListName.get(0), source.list.get(0));
    }

    @Test
    void targetListMustBeUntouched() {
        SourceWithList source = new SourceWithList();
        source.list.add("1138");
        TargetWithDifferentSetterAndFieldTypesButSimilarNames target =
                new TargetWithDifferentSetterAndFieldTypesButSimilarNames();
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        target = beanMapper.map(source, target);
        assertEquals("42", target.list.get(0));
    }

    public class TargetWithLongField {
        public Long value;
    }

    public class SourceWithList {
        public List<String> list = new ArrayList<>();
    }

    public class SourceWithStringFieldAndLongAccessors {
        private String value;

        public Long getValue() {
            return Long.parseLong(value) + 1;
        }

        public void setValue(Long number) {
            this.value = number.toString();
        }
    }

    public class TargetWithListButNoFieldProperty {
        public List<String> someOtherListName;

        public void setList(List<String> list) {
            someOtherListName = list;
        }
    }

    public class TargetWithDifferentSetterAndFieldTypes {
        public List<String> someOtherListName = new ArrayList<>();

        public void setList(List<Long> list) {
            for (Long number : list) {
                someOtherListName.add(number.toString());
            }
        }
    }

    public class TargetWithDifferentSetterAndFieldTypesButSimilarNames {
        public List<String> list = new ArrayList<>();

        public TargetWithDifferentSetterAndFieldTypesButSimilarNames() {
            this.list.add("42");
        }

        public void setList(List<Long> list) {
        }
    }

}
