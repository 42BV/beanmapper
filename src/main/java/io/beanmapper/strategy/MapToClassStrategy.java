package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.converter.BeanConverter;

@SuppressWarnings("unchecked")
public class MapToClassStrategy extends MapToInstanceStrategy {

    public MapToClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public Object map(Object source) {
        Class targetClass = getConfiguration().getTargetClass();

        if (getConfiguration().isConverterChoosable()) {
            Class<?> valueClass = getConfiguration().getBeanUnproxy().unproxy(source.getClass());
            BeanConverter converter = getConverterOptional(valueClass, targetClass);
            if (converter != null) {
                logger.debug("    " + converter.getClass().getSimpleName() + "->");
                return converter.convert(getBeanMapper(), source, targetClass, null);
            }
        }

        final BeanMatch beanMatch = getBeanMatch(source.getClass(), targetClass);
        final Object target = getConfiguration().getBeanInitializer().instantiate(
                targetClass,
                getConstructorArguments(source, beanMatch));

        return map(source, target, beanMatch);
    }
}
