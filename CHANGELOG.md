# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Unreleased

### Added

- Added diagnostics, allowing users to check what mappings and conversion are performed as part of a given mapping.

### NB

Diagnostics are only collected when enabled. Diagnostics can be enabled by using the `BeanMapper.wrap(DiagnosticsDetailLevel)`-method.

Each DiagnosticsDetailLevel comes with a different logging strategy.
* `DiagnosticsDetailLevel.COUNT_TOTAL`: Logs the total number of mappings and conversion performed as part of the top-level mapping, in the format: `[Mapping   ] SourceClass -> TargetClass {total mappings: 38, total conversions: 70, max depth: 7}`
* `DiagnosticsDetailLevel.COUNT_PER_PAIR`: Logs the total number of mappings and conversion per type pair.
* `DiagnosticsDetailLevel.TREE_COMPLETE`: Logs the entire mapping and conversion tree.


## [4.1.5]

### Fixed

- Issue [#203](https://github.com/42BV/beanmapper/issues/203) **Expand caching to include other expensive, unchanging operations**; Added the BeanConverterStore and the CanonicalClassNameStore, to reduce reflective calls.

### Added

- Caching for BeanConverter-selection.
- Caching for canonical class names.

## [4.1.4]

### Fixed

- Issue [#196](https://github.com/42BV/beanmapper/issues/196) **Calls to the logger should not contain a call to String.formatted(Object...)**; Fixed by removing any formatting from performance logging. Trace-logging should keep the level of detail provided by the formatting.
- Issue [#197](https://github.com/42BV/beanmapper/issues/197) **Implement caching for Unproxy results.**; Created UnproxyResultStore, allowing for thread-safe caching and retrieval of unproxied classes.
- Issue [#199](https://github.com/42BV/beanmapper/issues/199) **Use of OverrideField causes excessive resource consumption.**; Use of OverrideField was phased out. OverrideField itself is now deprecated for removal.
- Issue [#201](https://github.com/42BV/beanmapper/issues/201) **Calls from OverrideConfiguration to parentConfiguration cause unnecessary overhead.**; Introduced fields in the OverrideConfiguration, to limit the overhead of calls to the parent configuration.

## [4.1.3] - 2024-03-27

### Fixed

- Issue [#190](https://github.com/42BV/beanmapper/issues/190) **Enum with abstract overrides cannot be mapped.**; Due to a change in the way Class.getCanonicalName() works in newer versions of Java, this would return null for Enums with an abstract method.

### Added

- PR [#193](https://github.com/42BV/beanmapper/pull/193) **Create Logger, replace existing logging.**; Added performance logging and replaced regular logging with a trace-logger.

### Fixed

- Issue [#181](https://github.com/42BV/beanmapper/issues/181) **RecordToAnyConverter#copyFieldsToIntermediary(Object, Map) throws a NPE if Map contains an Entry 
  where the value is null.**; Added null-check to RecordToAnyConverter#copyFieldsToIntermediary(Object, Map), preventing a NPE whenever a field in the 
  source-object is null. 
- Issue [#185](https://github.com/42BV/beanmapper/issues/185) **OverrideConfiguration#strictMappingProperties cause of severe hit to performance.**; Replaced 
  usage of OverrideField, with direct call to underlying configuration.

## [4.1.0] - 2022-11-10

### Updated

- Upgraded JUnit 5.8.2 to 5.9.0
- Upgraded SLF4J 2.0.0 to 2.0.3
- Upgraded Javassist 3.29.1-GA to 3.29.2-GA
- Upgraded jackson-databind 2.13.4 to 2.13.4.2

### Fixed

- Issue [#130](https://github.com/42BV/beanmapper/issues/130) **@BeanConstruct does not map collection constructor arguments**; Added
  DefaultBeanInitializer#mapParameterizedArguments(Type[], Object[]), allowing BeanMapper to properly map constructor parameters with a type-parameter, when
  using the @BeanConstruct-annotation.
- Issue [#132](https://github.com/42BV/beanmapper/issues/132) **Mapping the same field twice ignores one of the mappings.**; Whenever a field is mapped, a check
  will be performed to detect field shadowing in this way. If field shadowing is detected, a FieldShadowingException is thrown. However, a
  BeanProperty-annotation may take on the name of a field that is non-public and for which an accessor is not exposed. Fields annotated with BeanIgnore are not
  considered when checking for shadowing.
- Issue [#149](https://github.com/42BV/beanmapper/issues/149) **Support mapping of JDK 16+ records to classes.**; Added support for mapping record through the
  usual BeanMapper#map(Object, Class<?>)-method.
- Issue [#152](https://github.com/42BV/beanmapper/issues/152) **Methods that return a Collection should never return null.**; All methods that return a
  Collection, will return an empty Collection of the target type, rather than returning null.
- Issue [#153](https://github.com/42BV/beanmapper/issues/153) **https://github.com/42BV/beanmapper/issues/153**; Rather than using the Boolean-wrapper, all
  occurrences of Boolean that are not absolutely necessary due to generics, have been replaced with the primitive boolean, or the
  Trinary-enum.
- Issue [#166](https://github.com/42BV/beanmapper/issues/166) **Source with BeanAlias-annotated fields cannot be downsized.**; Modified
  ClassGenerator#createClass, to check whether a generated field is annotated with BeanAlias. If so, the name of the generated field will be set to the value on
  the BeanAlias-annotation, and the annotation will be removed from the generated field.
- Issue [#168](https://github.com/42BV/beanmapper/issues/168) **ClassGeneratorTest#shouldNotFailConcurrently always succeeds.**; By catching Throwable, and
  saving Throwable in the List, rather than exception, the test should work correctly, and fail when it should.

### Added

- BeanMapper#map(Collection, Class) allowing users to map from a Collection to the type actual type of the
- Support for mapping Queue. A Queue will be mapped to an ArrayDeque by default. The order of elements is guaranteed to be preserved, except when the Queue is
  mapped to a Queue that inherently modifies the order of elements (e.g. PriorityQueue).
- BeanMapper#map(Queue, Class) allowing users to map a Queue to a new Queue, mapping the elements of the source to the target class.
- BeanMapper#map(Object[], Class) allowing users to map an array to an array with elements of the type of the target class. Also works for primitive arrays.
- Support for mapping to and from JDK16 record-classes.
- @BeanRecordConstruct-annotation, used to give BeanMapper instructions on how to map a record.
- BeanRecordConstructMode-enum, which allows the user to exclude certain constructors from being used to map a record, or force one to be used.
- Configuration#addCustomDefaultValueForClass(Class<?>, Object value) and Configuration#getDefaultValueForClass(Class<?>), which can be used to define default
  values for classes during runtime.
- RecordToAnyConverter, which creates a dynamic class based off of the given Record, which contains only public fields, corresponding to the RecordComponents.
  The dynamic class is then filled with values from the source Record, and used as an intermediary between the source and the target.
- MappingException, to serve as a common super-class for Exceptions thrown while mapping classes, and records.
- Default values for Optional, List, Set and Map in the DefaultValues.
- BeanMapper#map(Object, ParameterizedType), allowing for smarter mapping of collections and optionals.
- BeanMatchStore#detectBeanPropertyFieldShadowing(PropertyAccessor, io.beanmapper.annotations.BeanProperty), which will throw a FieldShadowingException when
  appropriate.
- Mapping of Optional-objects to a target class, using BeanMapper#map(Optional, Class).

### Changed

- Methods in OverrideConfiguration that used to perform NOP, now throw a BeanConfigurationOperationNotAllowedException.
- Removed deprecated method BeanMapper#config(), BeanMapper#wrapConfig().
- Removed deprecated class BeanMapperAware.
- @BeanAlias now applicable to RecordComponent.
- Expanded the use of type parameters to various methods throughout the library, including the BeanMapper-class.
- BeanConverters may be offered null-values now. Will default to returning the default value for the relevant type.

### Removed

- slf4j-api dependency, as it is transitive through jcl-over-slf4j.

## [4.0.1] - 2022-09-22

### Fixed

- Issue [#121](https://github.com/42BV/beanmapper/issues/121) **Mapping an Enum field to an Enum field of the same type fails when a custom toString method is
  present.**; When an Enum with a custom toString-method was mapped to an Enum, the mapping would fail. Fixed by adding an instanceof check in the
  AnyToEnumConverter, making it use Enum#name() to get the name of an Enum, rather than toString.
- Issue [#137](https://github.com/42BV/beanmapper/issues/137) **https://github.com/42BV/beanmapper/issues/137**; Mapping a class with a getter that returns an
  Optional, would fail, as an Optional can typically not be mapped to the target class. Fixed implementing an OptionalToObjectConverter, which handles unpacking
  an Optional, and additionally delegates further conversion back to the BeanMapper.
- DynamicClassGeneratorHelper was removed, due to returning null, and replaced with passing the baseclass for a generated class immediately to the constructor
  of GeneratedClass.

## [4.0.0] - 2022-09-15

### Changed

- Upgraded JUnit 4 to JUnit 5
- Replaced travis build with Github actions workflow
- Upgraded jackson-databind dependency which had an owasp error
- Upgraded to Java 17, from Java 8

### Fixed

- Issue [#141](https://github.com/42BV/beanmapper/issues/141), **Dynamic Mapping throws InaccessibleObjectException in Java 16+**; when a form is dynamically
  mapped to a class, an InaccessibleObjectException would be thrown, due to updated Reflection requirements. Fixed by updating ```GeneratedClass``` to make use
  of ```CtClass#toClass(Class)```, rather than ```CtClass#toClass()```.

## [3.1.0] - 2019-06-21

### Added

- Added value alias for `@BeanProperty` annotation, so using `name =` is no longer needed. It is still supported to maintain backwards compatibility

## [3.0.1] - 2018-07-19

### Fixed

- Issue [#118](https://github.com/42BV/beanmapper/issues/118), **Nested ParameterizedTypes throws a ClassCastException**; when a type is a nested generic (eg,
  List<List<String>>) it threw a ClassCastException, even if no mapping took place. Fixed by checking the Type in ```AbstractBeanPropertyClass``` and calling
  for rawType if it concerns a ParameterizedType.
- Issue [#116](https://github.com/42BV/beanmapper/issues/116), **Mappable nested classes analyzed for wrong direction**; when dealing with a mappable nested
  class, the applied direction is used as-is. This resulted in errors when the field could only be accessed through a method accessor and that the complementing
  method accessor was not available (ie, get and no set, or set and no get). The problem has been fixed by inverting the method direction on dealing with a
  mappable nested class.

## [3.0.0] - 2018-07-17

### Fixed

- **BREAKING CHANGE**: BeanConverter implementations no longer work with BeanField, but with BeanProperty. The name BeanField is no longer correct. A
  BeanProperty is the combination of BeanField and BeanMethod accessors (get/set).
- Issue [#114](https://github.com/42BV/beanmapper/issues/114), **Bean property Class types are determined depending on the accessor that will be used**;
  previously the Class was determined on the basis of the bean field. Now, it will look at the way a property will be accessed (getter/setter/field) and use the
  proper Class, including generics (ie, Type).

## [2.4.1] - 2018-06-19

### Fixed

- Issue [#109](https://github.com/42BV/beanmapper/issues/109), **Specify the return type for AbstractBeanConverter.doConvert**; on extending
  AbstractBeanConverter, it is beneficial for the developer to immediately see the expected return type for the ```doConvert``` method.
- Bug [#111](https://github.com/42BV/beanmapper/issues/111), **BeanCollection null list overrides the existing list**; when a source and target have been
  assigned to a collection handler, it will now treat a null value for the source as special, subscribing it to the BeanCollectionUsage rules (default: CLEAR).
  That is, it will REUSE the target, CLEAR it, or CONSTRUCT a new one. This is the most logical behaviour with Hibernate on the other side.
- Bug [#112](https://github.com/42BV/beanmapper/issues/112), **Strict mapping messes up the build sequence**; The validation took place before all configuration
  was done, resulting in collection handlers not being available in some cases. The validation of strict classes is done as part of the last step of
  the ```BeanMapperBuilder.build()``` method, after all required steps have been taken.

## [2.4.0] - 2018-03-28

### Added

- Issue [#107](https://github.com/42BV/beanmapper/issues/107), **Test for access by running against LogicSecuredCheck instance**; ability to add
  LogicSecuredCheck classes to BeanMapper's configuration. These classes can be called upon using the @BeanLogicSecured annotation. It allows for more complex
  interaction with the enveloping security system, such as not only checking against roles, but also comparing fields in the source or target against
  information known about the Principal.
- Issue [#105](https://github.com/42BV/beanmapper/issues/105), **Ability to deal with @BeanRoleSecured by delegating to a RoleSecuredCheck**; when a field is
  tagged as @BeanRoleSecured, BeanMapper will query its attached SecuredPropertyHandler. The handler will most likely be associated with a security
  implementation, such as Spring Security (not handled here). If no handler is present, access is granted by default.
- Issue [#106](https://github.com/42BV/beanmapper/issues/106), **When a @BeanRoleSecured is found without a RoleSecuredCheck being set, throw an exception**;
  the absence of a RoleSecuredCheck is by default a reason to throw an exception when @BeanRoleSecured is used anywhere within the application.

## [2.3.2] - 2018-03-06

### Fixed

- Issue [#104](https://github.com/42BV/beanmapper/pull/104), **IllegalAccessExceptions that occurred after JMV garbage collection**; since all
  PropertyDescriptor instances are kept in a static cache and PD relies on Java's SoftReference, the reference can be discarded. This means that if the
  accessible boolean was set to true, it will be discarded. The error can manifest itself after a GC run and if the application relies on package-protected
  classes. The fix: the setAccessible method should be called very soon prior to calling the invoke method rather than only once at the creation of the
  PropertyDescriptorPropertyAccessor.

## [2.3.1] - 2017-11-02

### Fixed

- Issue [#99](https://github.com/42BV/beanmapper/issues/99), **Collections in superclasses did not get their generic types read**; one of the attributes of
  getDeclaredField(field) is that it only works on the active class. The fix means that the superclasses will be checked for presence of the field. When found,
  it will call getDeclaredField on that class to get its generic type. Also, collection mapping instructions are not used when no collection element type can be
  determined; that is the one crucial element required for mapping collections.
- Issue [#100](https://github.com/42BV/beanmapper/issues/100), **Collections.EmptySet can not have add() called on**; BeanCollectionUsage will check the
  canonical classname of the collection. If it starts with "java.util.Collections.", it will be tagged as reconstructable.

## [2.3.0] - 2017-11-02

### Added

- Issue [#92](https://github.com/42BV/beanmapper/issues/92), **BeanCollection no longer required for mapping collections**; BeanMapper is now capable of
  determining the collection element type of the target collection by examining the generic type. It will use this type as input for the mapping process.
  BeanCollection.elementType is no longer a required value. BeanMapper will merge collection information from target and source, giving preference to target
  information.
- Issue [#97](https://github.com/42BV/beanmapper/issues/97), **StringToEnumConverter replaced with AnyToEnumConverter**; this makes it possible to convert enums
  to enums as well. Functionality is required because lists are no longer copied by reference by default.

## [2.2.0] - 2017-11-01

### Fixed

- **BREAKING CHANGE** Issue [#93](https://github.com/42BV/beanmapper/issues/93), **config() not threadsafe**; it is possible for override configurations to be
  reused between threads, theoretically allowing fields to be changed before the map is called. This is not threadsafe. Now, nowhere is override configuration
  reused; it will always create a new override configuration. Both config() and wrapConfig() have been replaced with wrap(), which does the same as wrapConfig()
  . Internally, some OverrideConfiguration properties have been delegated to an OverrideField which takes care of returning the right value for a property. The
  clear() method has been removed; calling wrap automatically resets these properties (expect for downsize source/target, which are primarily used internally).
- Issue [#60](https://github.com/42BV/beanmapper/issues/60), **Unmatched BeanProperty did not throw an exception**; properties annotated with BeanProperty must
  match. If they do not, an exception must be thrown. Due to a bug, this did not always occur (only with BeanProperty on the target side). The current mechanism
  keep tabs on matched properties and does a final verification. If unmatched properties remain that should have been matched, an exception is thrown.

### Changed

- Issue [#89](https://github.com/42BV/beanmapper/issues/89), **Use sensible implementation for Set**; when a set is created an no preferredCollectionClass is
  passed, the handler will look at the collection element type. If the type is Comparable, it will return a TreeSet. If not, it will return a HashSet.

### Added

- Issue [#90](https://github.com/42BV/beanmapper/issues/90), **Introduce a global flushEnabled, BeanCollection.flushAfterClear default true**; a global
  flushEnabled setting has been introduced, which is false by default. The BeanCollection.flushAfterClear has changed from a default false to true. BeanMapper
  asserts that both settings must be true before flushing. BeanMapper Spring [#28](https://github.com/42BV/beanmapper-spring/issues/28) sets flushEnabled=true
  when Lazy is used, because this offers the best chance of the EntityManager running in a transaction context.

## [2.1.0] - 2017-10-25

### Fixed

- Issue [#83](https://github.com/42BV/beanmapper/issues/83), **The name field from an enum is not mapped to target field**; in the resolution of
  issue [#78](https://github.com/42BV/beanmapper/issues/78) the definition of getter fields has been tightened, because previously all private fields were
  tagged as available as well. One project made use of this loophole by reading the name field of an enumeration class to a String field. With the new fix this
  is no longer possible, since the name field is private. This fix makes an exception for the name field of an enum class. It will be considered available for
  reading.

### Added

- Issue [#84](https://github.com/42BV/beanmapper/issues/84), **BeanMapper executes the list of AfterClearFlusher instances**; when a clear method on a
  collection is called, BeanMapper makes sure to run down the list of registered AfterClearFlusher instances. On every instance, the flush method will be
  called. By default **no** AfterClearFlusher instances are added. BeanMapper has no notion of ORMs; this is left
  to [beanmapper-spring](https://github.com/42BV/beanmapper-spring) and [beanmapper-spring-boot-starter](https://github.com/42BV/beanmapper-spring-boot-starter)
  . Note that the flusher chain is only called when it has been set to do so in the BeanCollection annotation or the override configuration (flushAfterClear).

## [2.0.0] - 2017-10-12

### Fixed

- **POSSIBLE BREAKING CHANGE!** Issue [#59](https://github.com/42BV/beanmapper/issues/59), **@BeanCollection usage should be CLEAR not REUSE**; the current
  collection usage is REUSE, which means the collection is used as is, keeping the target elements in the collection. In Hibernate, this will not trigger the
  deletion of orphans. The dominant option is to use CLEAR, which means that any target collection instance will be used, but its contents removed by calling
  clear(). By calling this method, a managed list (such as Hibernate does), will keep track of the removed elements (orphan deletion, if enabled).
- Issue [#76](https://github.com/42BV/beanmapper/issues/76), **Support anonymous classes**; Instances that were created anonymously were not supported by the
  Unproxy. Now, it will check whether the class is anonymous. If this is the case, it will refer to its superclass and re-enter the unproxy process.
- **POSSIBLE BREAKING CHANGE!** Issue [#68](https://github.com/42BV/beanmapper/issues/68), **Refactored collection mapping**; collection mapping happened in two
  different styles. One was handled by MapCollectionStrategy, the other by CollectionConverter. CollectionConverter now defers to MapCollectionStrategy. The
  latter has been extended to be able to deal with more precise mappings and clearing/reusing collections. BeanCollection no longer supports the
  targetCollectionType(), which is now inferred and handled by the appropriate CollectionHandler. Instead, use preferredCollectionType if you care for a
  specific collection class instead of the one provided by the CollectionHandler.

## [1.0.1] - 2017-10-04

### Fixed

- Issue [#78](https://github.com/42BV/beanmapper/issues/78), BeanMapper contained an error that considered all fields (even private fields) as readable. This
  dormant error, previously seems not to have manifested itself, but with the strict handling of mappings it did. The fix has been to make sure to check the
  field modifiers for public access.

## [1.0.0] - 2017-10-04

### Added

- Issue [#75](https://github.com/42BV/beanmapper/issues/75), **Optional strict handling of BeanMapper mappings**; two layers of protection have been introduced.
  The first is the possibility to register a class pair with one side being strict. The strict side must have matching properties for all its valid properties.
  If properties are not matched, an exception will be thrown detailing the mismatches. The second layer of protection works on the Form/Result convention. It
  checks whether the source is consider a form Ie, classname has the suffix 'Form') or a target is a result (ie, classname has the suffix 'Result'). If this is
  the case, the other side must have matching properties as well. This second layer of defense works runtime right before the mapping takes place. Note that the
  suffix can be changed and the convention for strict mapping can be disabled.
