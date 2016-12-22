package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.exceptions.BeanNoTargetException;

public enum MapStrategyType {
    CREATE_DYNAMIC_CLASS() {
        @Override
        public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
            return new MapToDynamicClassStrategy(beanMapper, configuration);
        }
    },
    MAP_COLLECTION() {
        @Override
        public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
            return new MapCollectionStrategy(beanMapper, configuration);
        }
    },
    MAP_TO_CLASS() {
        @Override
        public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
            return new MapToClassStrategy(beanMapper, configuration);
        }
    },
    MAP_TO_INTERFACE() {
        @Override
        public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
            return new MapToInterfaceStrategy(beanMapper, configuration);
        }
    },
    MAP_TO_INSTANCE() {
        @Override
        public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
            return new MapToInstanceStrategy(beanMapper, configuration);
        }
    };

    public static MapStrategy getStrategy(BeanMapper beanMapper, Configuration configuration) {
        return determineStrategy(configuration).generateMapStrategy(beanMapper, configuration);
    }

    public static MapStrategyType determineStrategy(Configuration configuration) {
        if (configuration.getDownsizeSource() != null || configuration.getDownsizeTarget() != null) {
            return CREATE_DYNAMIC_CLASS;
        } else if (configuration.getCollectionClass() != null) {
            return MAP_COLLECTION;
        } else if (configuration.getTargetClass() != null) {
            if (configuration.getTargetClass().isInterface()) {
                return MAP_TO_INTERFACE;
            } else {
                return MAP_TO_CLASS;
            }
        } else if (configuration.getTarget() != null) {
            return MAP_TO_INSTANCE;
        } else {
            throw new BeanNoTargetException();
        }
    }

    public MapStrategy generateMapStrategy(BeanMapper beanMapper, Configuration configuration) {
        throw new BeanNoTargetException();
    }

}
