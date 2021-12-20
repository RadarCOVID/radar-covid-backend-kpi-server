# Changelog

All notable changes to this project will be documented in this file. 

## [Unreleased]

## [1.3.2.RELEASE] - 2021-12-20

### Changed

- Updated log4j2 version to 2.17.0

## [1.3.1.RELEASE] - 2021-12-17

### Changed

- Updated log4j2 version to 2.15.0
- Updated testcontainers version to 1.16.2

## [1.3.0.RELEASE] - 2021-02-17

### Added

- Added penetration rate to total KPIs.
- Added statistics process batch.
- Added [efficient Docker images with Spring Boot 2.3](https://spring.io/blog/2020/08/14/creating-efficient-docker-images-with-spring-boot-2-3).
- Added [SafetyNet Attestation API](https://developer.android.com/training/safetynet) validation for KPIs from Android devices.
- Added [DeviceCheck API](https://developer.apple.com/documentation/devicecheck) validation for KPIs from iOS devices.
- Added export CSVs services for statistics.

### Fixed

- Fixed radar statistics views.
- Redis configuration on docker-compose mode.

## [1.2.0.RELEASE] - 2020-12-17

* KPI Service. Initial version.

### Added

- Key Performance Indicator (KPI) service.
- Radar statistics service: application downloads, delivered codes, positives communicated.

[Unreleased]: https://github.com/RadarCOVID/radar-covid-backend-kpi-server/compare/1.3.2.RELEASE...develop
[1.3.2.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-kpi-server/compare/1.3.1.RELEASE...1.3.2.RELEASE
[1.3.1.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-kpi-server/compare/1.3.0.RELEASE...1.3.1.RELEASE
[1.3.0.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-kpi-server/compare/1.2.0.RELEASE...1.3.0.RELEASE
[1.2.0.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-kpi-server/releases/tag/1.2.0.RELEASE