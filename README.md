# Beanmapper

Beanmapper is a Java library for mapping dissimilar Java classes with similar names. The use
cases for Beanmapper are the following:
* mapping from forms to entities, because:
** you want to accept a limited number of fields as input
* mapping from entities to results, because:
** you want to simplify the result for frontend processing
** you want to expose a limited number of fields for security reasons

## Maven dependency

In order to use Beanmapper in your project, simply add the following Maven dependency:

```xml
<dependency>
    <groupId>io.beanmapper</groupId>
    <artifactId>beanmapper</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Getting started

You want to map two dissimilar classes with no hierarchical relation (save java.lang.Object), 
but with a fairly similar naming schema for the fields.

![Basic use case for Beanmapper](docs/images/01-basic-use-case.png)

```java
public class SourceClass {
   public Long id;
   public String name;
   public LocalDate date;
}
```

```java
public class TargetClass {
   public String name;
   public LocalDate date;
}
```

```java
BeanMapper beanMapper = new BeanMapper();
SourceClass source = new SourceClass();
source.id = 42L;
source.name = "Henk";
source.date = LocalDate.of(2015, 4, 1));
TargetClass target = beanMapper.map(source, TargetClass.class);
```
