# Changelog
**0.2.2**
- Fixed #49

**0.2.1**
- Fixed #40
- Changed license from GPL v3 to MIT

**0.2.0**
- Renamed all references to "dbadmin" in "snapadmin". This also applies to the configuration parameter prefix (#29)
- SnapAdmin is now **disabled by default**. Remember to set `snapadmin.enabled=true` to activate it (#30)
- Alerts if SnapAdmin is running on unsecured routes (#32)
- User-aware audit logs (#34)
- Other fixed issues: #35, #27, #38

**0.1.9**
- Renamed project to SnapAdmin
- Proper 'step' values for input type "number"
- Fixed handling of java.sql.Date field type
- Other fixed issues: #16

**0.1.8**
- SQL console to run custom SQL queries (#20)
- Support for `Instant` (#21)
- Added JSONL format to export options
- `@Disable` annotation to ignore entire table (#24)
- Other fixed issues: #19

**0.1.7**
- Export to CSV and XLSX

**0.1.6**
- Support for JPA validation (#12)
- Support for UUID type (#13)
- Improved handling of 404 errors
- Bugfixes

**0.1.5**
- Access-control annotations ([#5](https://github.com/aileftech/spring-boot-database-admin/issues/5))
- Added support for `OffsetDateTime` ([#7](https://github.com/aileftech/spring-boot-database-admin/issues/7)) and several other field types (`char`, `short`, `byte`, `Date`).
- Unsupported field types are now handled gracefully ([#9](https://github.com/aileftech/spring-boot-database-admin/issues/9))
- Solved Spring Boot Devtools incompatibility ([#8](https://github.com/aileftech/spring-boot-database-admin/issues/8))

**0.1.4**
- Fixed critical issue which was preventing correct startup ([#1](https://github.com/aileftech/spring-boot-database-admin/issues/1))

**0.1.3**
- `@HiddenColumn`
- Improved UI on action logs page and several other improvements to faceted search
- Bugfixes

**0.1.2**
- Better handling of large text fields (shown as `textarea`)
- Added `CATEGORICAL` option to `Filterable`
- Several bug fixes

**0.1.0**
- Implemented action logs
- Implemented user settings

**0.0.4**
- Simplified setup/configuration: now it only requires a couple of config properties and 1 annotation
- Support of custom base url for the web UI, instead of hardcoded "/dbadmin"
- Continued implementation of automated testing with Selenium

**0.0.3**
- @DisplayImage; Selenium tests; Fixed/greatly improved edit page;

**0.0.2**
- Faceted search with `@Filterable` annotation

**0.0.1**
- First alpha release (basic CRUD features)
