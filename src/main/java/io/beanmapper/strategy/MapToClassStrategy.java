package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.BeanMapperTraceLogger;

public class MapToClassStrategy extends MapToInstanceStrategy {

    public MapToClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S, T> T map(S source) {
        Class<?> targetClass = getConfiguration().getTargetClass();

        if (getConfiguration().isConverterChoosable() || source instanceof Record) {
            Class<?> valueClass = getConfiguration().getBeanUnproxy().unproxy(source.getClass());
            BeanConverter converter = getConverterOptional(valueClass, targetClass);
            if (converter != null) {
                BeanMapperTraceLogger.log("Converter called for source of class {}, while mapping to class {}\t{}->", source.getClass(), targetClass,
                        converter.getClass().getSimpleName());
                return (T) converter.convert(getBeanMapper(), source, targetClass, null);
            }
        }

        final BeanMatch beanMatch = getBeanMatch(source.getClass(), targetClass);
        final Object target = getConfiguration().getBeanInitializer().instantiate(
                targetClass,
                getConstructorArguments(source, beanMatch));

        return (T) map(source, target, beanMatch);
    }
}
