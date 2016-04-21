package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanExpression;
import io.beanmapper.config.Configuration;

import java.lang.reflect.Method;

/**
 * 
 *
 * @author jeroen
 * @since Apr 21, 2016
 */
public class MapToInterfaceStrategy extends MapToInstanceStrategy {
    
    public MapToInterfaceStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }
    
    @Override
    public Object map(Object source) {
        Class targetClass = getConfiguration().getTargetClass();
        return buildNewProxy(source, targetClass);
    }
    
    private <T> T buildNewProxy(Object instance, Class<T> interfaceClass) {
        org.springframework.aop.framework.ProxyFactory proxyFactory = new org.springframework.aop.framework.ProxyFactory();
        proxyFactory.addInterface(interfaceClass);
        proxyFactory.addAdvisor(new org.springframework.aop.support.DefaultPointcutAdvisor(new AlwaysPointcut(), new MappingInterceptor(instance)));
        return (T) proxyFactory.getProxy();
    }
    
    public static class AlwaysPointcut extends org.springframework.aop.support.StaticMethodMatcherPointcut {
        
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return true;
        }
        
    }
    
    public static class MappingInterceptor implements org.aopalliance.intercept.MethodInterceptor {
        
        private static final String EXPRESSION_PREFFIX = "#{";
        private static final String EXPRESSION_SUFFIX = "}";

        private final org.springframework.expression.spel.standard.SpelExpressionParser parser;
        
        private final Object source;
        
        public MappingInterceptor(Object source) {
            this.source = source;
            
            org.springframework.expression.spel.SpelParserConfiguration config = new org.springframework.expression.spel.SpelParserConfiguration(true, true);
            parser = new org.springframework.expression.spel.standard.SpelExpressionParser(config);
        }

        @Override
        public Object invoke(org.aopalliance.intercept.MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            BeanExpression[] expressions = method.getDeclaredAnnotationsByType(BeanExpression.class);
            if (expressions.length == 1) {
                return evaluate(expressions[0].value());
            } else {
                return evaluate("#{" + method.getName() + "()}");
            }
        }

        private Object evaluate(String expressions) {
            // Attempt to evaluate as a single value
            if (expressions.startsWith(EXPRESSION_PREFFIX) && expressions.endsWith(EXPRESSION_SUFFIX)) {
                String rawExpression = expressions.substring(2, expressions.length() - 1);
                if (!rawExpression.contains(EXPRESSION_PREFFIX)) {
                    return evaluateSingle(rawExpression);
                }
            }
            
            // Otherwise concatenate the evaluations in a string value
            return evaluateConcatenated(expressions);
        }

        private Object evaluateConcatenated(String expressions) {
            StringBuilder result = new StringBuilder();
            for (int index = 0; index < expressions.length(); index++) {
                String remainder = expressions.substring(index);
                if (remainder.startsWith(EXPRESSION_PREFFIX) && remainder.contains(EXPRESSION_SUFFIX)) {
                    int endIndex = remainder.indexOf(EXPRESSION_SUFFIX);
                    String rawExpression = remainder.substring(2, endIndex);
                    result.append(evaluateSingle(rawExpression));
                    index += endIndex;
                } else {
                    result.append(expressions.charAt(index));
                }
            }
            return result.toString();
        }
        
        private Object evaluateSingle(String rawExpression) {
            org.springframework.expression.Expression expression = parser.parseExpression(rawExpression);
            return expression.getValue(source);
        }

    }
    
}
