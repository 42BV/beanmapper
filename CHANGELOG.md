# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Issue [#76](https://github.com/42BV/beanmapper/issues/78), **Support anonymous classes**; Instances that were created anonymously were not supported by the Unproxy. Now, it will check whether the class is anonymous. If this is the case, it will refer to its superclass and re-enter the unproxy process. 

## [1.0.1] - 2017-10-04
### Fixed
- Issue [#78](https://github.com/42BV/beanmapper/issues/78), BeanMapper contained an error that considered all fields (even private fields) as readable. This dormant error, previously seems not to have manifested itself, but with the strict handling of mappings it did. The fix has been to make sure to check the field modifiers for public access.

## [1.0.0] - 2017-10-04
### Added
- Issue [#75](https://github.com/42BV/beanmapper/issues/75), **Optional strict handling of BeanMapper mappings**; two layers of protection have been introduced. The first is the possibility to register a class pair with one side being strict. The strict side must have matching properties for all its valid properties. If properties are not matched, an exception will be thrown detailing the mismatches. The second layer of protection works on the Form/Result convention. It checks whether the source is consider a form Ie, classname has the suffix 'Form') or a target is a result (ie, classname has the suffix 'Result'). If this is the case, the other side must have matching properties as well. This second layer of defense works runtime right before the mapping takes place. Note that the suffix can be changed and the convention for strict mapping can be disabled.