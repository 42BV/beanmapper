# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Unreleased

## [2.2.0] - 2017-11-01
### Added
- Issue [#92](https://github.com/42BV/beanmapper/issues/92), **BeanCollection no longer required for mapping collections**; BeanMapper is now capable of determining the collection element type of the target collection by examining the generic type. It will use this type as input for the mapping process. BeanCollection.elementType is no longer a required value. BeanMapper will merge collection information from target and source, giving preference to target information. 

## [2.2.0] - 2017-11-01
### Fixed
- **BREAKING CHANGE** Issue [#93](https://github.com/42BV/beanmapper/issues/93), **config() not threadsafe**; it is possible for override configurations to be reused between threads, theoretically allowing fields to be changed before the map is called. This is not threadsafe. Now, nowhere is override configuration reused; it will always create a new override configuration. Both config() and wrapConfig() have been replaced with wrap(), which does the same as wrapConfig(). Internally, some OverrideConfiguration properties have been delegated to an OverrideField which takes care of returning the right value for a property. The clear() method has been removed; calling wrap automatically resets these properties (expect for downsize source/target, which are primarily used internally).
- Issue [#60](https://github.com/42BV/beanmapper/issues/60), **Unmatched BeanProperty did not throw an exception**; properties annotated with BeanProperty must match. If they do not, an exception must be thrown. Due to a bug, this did not always occur (only with BeanProperty on the target side). The current mechanism keep tabs on matched properties and does a final verification. If unmatched properties remain that should have been matched, an exception is thrown.
### Changed
- Issue [#89](https://github.com/42BV/beanmapper/issues/89), **Use sensible implementation for Set**; when a set is created an no preferredCollectionClass is passed, the handler will look at the collection element type. If the type is Comparable, it will return a TreeSet. If not, it will return a HashSet.
### Added
- Issue [#90](https://github.com/42BV/beanmapper/issues/90), **Introduce a global flushEnabled, BeanCollection.flushAfterClear default true**; a global flushEnabled setting has been introduced, which is false by default. The BeanCollection.flushAfterClear has changed from a default false to true. BeanMapper asserts that both settings must be true before flushing. BeanMapper Spring [#28](https://github.com/42BV/beanmapper-spring/issues/28) sets flushEnabled=true when Lazy is used, because this offers the best chance of the EntityManager running in a transaction context. 

## [2.1.0] - 2017-10-25
### Fixed
- Issue [#83](https://github.com/42BV/beanmapper/issues/83), **The name field from an enum is not mapped to target field**; in the resolution of issue [#78](https://github.com/42BV/beanmapper/issues/78) the definition of getter fields has been tightened, because previously all private fields were tagged as available as well. One project made use of this loophole by reading the name field of an enumeration class to a String field. With the new fix this is no longer possible, since the name field is private. This fix makes an exception for the name field of an enum class. It will be considered available for reading.
### Added
- Issue [#84](https://github.com/42BV/beanmapper/issues/84), **BeanMapper executes the list of AfterClearFlusher instances**; when a clear method on a collection is called, BeanMapper makes sure to run down the list of registered AfterClearFlusher instances. On every instance, the flush method will be called. By default **no** AfterClearFlusher instances are added. BeanMapper has no notion of ORMs; this is left to [beanmapper-spring](https://github.com/42BV/beanmapper-spring) and [beanmapper-spring-boot-starter](https://github.com/42BV/beanmapper-spring-boot-starter). Note that the flusher chain is only called when it has been set to do so in the BeanCollection annotation or the override configuration (flushAfterClear).

## [2.0.0] - 2017-10-12
### Fixed
- **POSSIBLE BREAKING CHANGE!** Issue [#59](https://github.com/42BV/beanmapper/issues/59), **@BeanCollection usage should be CLEAR not REUSE**; the current collection usage is REUSE, which means the collection is used as is, keeping the target elements in the collection. In Hibernate, this will not trigger the deletion of orphans. The dominant option is to use CLEAR, which means that any target collection instance will be used, but its contents removed by calling clear(). By calling this method, a managed list (such as Hibernate does), will keep track of the removed elements (orphan deletion, if enabled).
- Issue [#76](https://github.com/42BV/beanmapper/issues/76), **Support anonymous classes**; Instances that were created anonymously were not supported by the Unproxy. Now, it will check whether the class is anonymous. If this is the case, it will refer to its superclass and re-enter the unproxy process. 
- **POSSIBLE BREAKING CHANGE!** Issue [#68](https://github.com/42BV/beanmapper/issues/68), **Refactored collection mapping**; collection mapping happened in two different styles. One was handled by MapCollectionStrategy, the other by CollectionConverter. CollectionConverter now defers to MapCollectionStrategy. The latter has been extended to be able to deal with more precise mappings and clearing/reusing collections. BeanCollection no longer supports the targetCollectionType(), which is now inferred and handled by the appropriate CollectionHandler. Instead, use preferredCollectionType if you care for a specific collection class instead of the one provided by the CollectionHandler.

## [1.0.1] - 2017-10-04
### Fixed
- Issue [#78](https://github.com/42BV/beanmapper/issues/78), BeanMapper contained an error that considered all fields (even private fields) as readable. This dormant error, previously seems not to have manifested itself, but with the strict handling of mappings it did. The fix has been to make sure to check the field modifiers for public access.

## [1.0.0] - 2017-10-04
### Added
- Issue [#75](https://github.com/42BV/beanmapper/issues/75), **Optional strict handling of BeanMapper mappings**; two layers of protection have been introduced. The first is the possibility to register a class pair with one side being strict. The strict side must have matching properties for all its valid properties. If properties are not matched, an exception will be thrown detailing the mismatches. The second layer of protection works on the Form/Result convention. It checks whether the source is consider a form Ie, classname has the suffix 'Form') or a target is a result (ie, classname has the suffix 'Result'). If this is the case, the other side must have matching properties as well. This second layer of defense works runtime right before the mapping takes place. Note that the suffix can be changed and the convention for strict mapping can be disabled.