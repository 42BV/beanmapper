package io.beanmapper;

import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * Adapter that allows the bean mapper to be used in
 * the spring conversion service.
 *
 * @author Jeroen van Schagen
 * @since Aug 21, 2015
 */
public class BeanMapperConverterAdapter implements GenericConverter {
    
    /**
     * Delegate bean mapper.
     */
    private final BeanMapper beanMapper;
    
    /**
     * Construct a new instance.
     * @param beanMapper the bean mapper
     */
    public BeanMapperConverterAdapter(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // Skip converting between the same types
        if (sourceType.getType().equals(targetType.getType())) {
            return source;
        }
        return beanMapper.map(source, targetType.getType());
    }
    
}
