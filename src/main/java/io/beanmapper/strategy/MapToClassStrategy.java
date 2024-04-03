package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.UnproxyResultStore;
import io.beanmapper.utils.BeanMapperTraceLogger;

public class MapToClassStrategy extends MapToInstanceStrategy {

    public MapToClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S, T> T map(S source) {
        Class<?> targetClass = getConfiguration().getTargetClass();

        if (getConfiguration().isConverterChoosable() || source instanceof Record) {
            BeanUnproxy unproxy = getConfiguration().getBeanUnproxy();
            Class<?> valueClass = UnproxyResultStore.getInstance().getOrComputeUnproxyResult(source.getClass(), unproxy);
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
