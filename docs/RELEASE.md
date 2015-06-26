# Release notes

## 0.2.5

 - Can now only map between fields when they are public.
 - Restricted mapping to fields that are marked final.
 - Fixed conversion issue with proxied enumerations.
 - No longer performing value conversions when the target value is not writable.

## 0.2.4

 - Mapping now also workings using the bean getter and setters.
 - Added various default converters:
  - Number to number
  - String to number
  - String to enumeration
  - Object to string
 - Simplified converter interface, you can now extend from the SimpleBeanConverter.
 - Resolved issues with proxy beans not being able to map. Unproxy logic is configurable by setBeanUnproxy.
 - Bean instantiation is now configurable by setBeanInitializer, the default is still a no-arg constructor.
