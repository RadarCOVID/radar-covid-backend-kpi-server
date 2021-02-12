# KPI Service

<p align="center">
    <a href="https://github.com/Radar-COVID/radarcovid-kpi-server/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/Radar-COVID/radarcovid-kpi-server?style=flat"></a>
    <a href="https://github.com/Radar-COVID/radarcovid-kpi-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/Radar-COVID/radarcovid-kpi-server?style=flat"></a>
    <a href="https://github.com/Radar-COVID/radarcovid-kpi-server/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg?style=flat"></a>
</p>

## Introduction

[Key Performance Indicator (KPI)](https://en.wikipedia.org/wiki/Performance_indicator) Service in terms of the Radar COVID project allows saving indicators from the apps to be considered in order to take into account in decisions about COVID-19 is affecting.

## Prerequisites

These are the frameworks and tools used to develop the solution:

- [Java 11](https://openjdk.java.net/).
- [Maven](https://maven.apache.org/).
- [Spring Boot](https://spring.io/projects/spring-boot) version 2.3.
- [ArchUnit](https://www.archunit.org/) is used to check Java architecture.
- [PostgreSQL](https://www.postgresql.org/).
- [Redis](https://redis.io/) is used for caching.
- Testing:
    - [Spock Framework](http://spockframework.org/).
    - [Docker](https://www.docker.com/), because the use of Testcontainers.
    - [Testcontainers](https://www.testcontainers.org/).

## Installation and Getting Started

### Building from Source

To build the project, you need to run this command:

```shell
mvn clean package -P<environment>
```

Where `<environment>` has these possible values:

- `local-env`. To run the application from local (eg, from IDE o from Maven using `mvn spring-boot:run`). It is the default profile, using `application-local.yml` configuration file.
- `docker-env`. To run the application in a Docker container with `docker-compose`, using `application-docker.yml` configuration file.
- `pre-env`. To run the application in the Preproduction environment, using `application-pre.yml` configuration file.
- `pro-env`. To run the application in the Production environment, using `application-pro.yml` configuration file.

The project also uses Maven profile `aws-env` to include dependencies when it is running on AWS environment, so the compilation command for Preproduction and Production environments would be:

```shell
mvn clean package -P pre-env,aws-env
mvn clean package -P pro-env,aws-env
```

All profiles will load the default [configuration](./kpi-server-boot/src/main/resources/application.yml).

### Running the Project

Depends on the environment you selected when you built the project, you can run the project:

- From the IDE, if you selected `local-env` environment (or you didn't select any Maven profile).
- From Docker. Once you build the project, you will have in `kpi-server-boot/target/docker` the files you would need to run the application from a container (`Dockerfile` and the Spring Boot fat-jar).

If you want to run the application inside a docker in local, once you built it, you should run:

```shell
docker-compose up -d postgres
docker-compose up -d redis
docker-compose up -d backend
```

#### Database

This project doesn't use either [Liquibase](https://www.liquibase.org/) or [Flyway](https://flywaydb.org/) because:

1. DB-Admins should only have database privileges to maintain the database model ([DDL](https://en.wikipedia.org/wiki/Data_definition_language)).
2. Applications should only have privileges to maintain the data ([DML](https://en.wikipedia.org/wiki/Data_manipulation_language)).

Because of this, there are two scripts:

- [`01-KPI-DDL.sql`](./sql/01-KPI-DDL.sql). Script to create the model.
- [`02-KPI-DML.sql`](./sql/02-KPI-DML.sql). Script with inserts.

Also is needed the same data model used in Verification to get statistics data. So to be able to launch KPI on local environment we copied the SQL files from Verification.

- [`03-VERIFICATION-DDL.sql`](./sql/03-VERIFICATION-DDL.sql). Script to create the model.
- [`04-VERIFICATION-DML.sql`](./sql/04-VERIFICATION-DML.sql). Script with inserts.

### API Documentation

Along with the application there comes with [OpenAPI Specification](https://www.openapis.org/), which you can access in your web browser when the Verification Service is running (unless in Production environment, where it is inactive by default):

```shell
<base-url>/openapi/api-docs
```

If running in local, you can get the OpenAPI accessing http://localhost:8080/openapi/api-docs. You can download the YAML version in `/openapi/api-docs.yaml`.

If running in local, you can get:
- OpenAPI: http://localhost:8080/openapi/api-docs
- Swagger UI: http://localhost:8080/openapi/ui 

#### Endpoints

| Endpoint | Description |
| -------- | ----------- |
| `/apple/token` | Verify and authorize a token device provided by Device Check API to uniquely identify iOS device
| `/apple` | With an authorized token device, save KPI data
| `/google` | Save KPI data if safety net token provided is valid. Safety Net API provide us a token to uniquely identify Android device
| `/statistics/basics` | Get basic statistics. Total downloads and positives 
| `/statistics/downloads` | Get downloads statistics. Total/monthly/weekly/SO downloads
| `/statistics/codes` | Get delivered codes statistics. Total/Accumulated/monthly/weekly delivered codes by CCAA 
| `/statistics/ratio` | Get ratio of delivered codes statistics. Accumulated/monthly/weekly ratio by CCAA
| `/statistics/positives` | Get reported positives on RadarCOVID statistics. Total/monthly/weekly positives.
| `/statistics/positives/ccaa` | Get reported positives on RadarCOVID statistics by CCAA. Total/monthly/weekly positives by CCAA.
| `/statistics/positives/ccaa/ratio` | Get ratio of reported positives on RadarCOVID statistics. Total/monthly/weekly positives ratio by CCAA.

### Safety Net

Safety Net API is used to verify if endpoint is called from a real Android device. This API provides a JWT token on app side.

To get a valid token you must set the following parameter on [`application.yml`](./kpi-server-boot/src/main/resources/application.yml) with your own apk certificate provided by Safety Net API.

```shell
application.kpi.safety-net.apk-digest
```

Other way is up KPI application with safety net simulation mode setting the following parameter:

```shell
application.kpi.safety-net.simulate.enabled: true
```

### Device Check

Device Check API is used to verify if endpoint is called from a real iOS device. This API provides a Base64 token on app side.

To validate device token is needed request an apple endpoint on `https://api.devicecheck.apple.com/v1/validate_device_token`. It's necessary create an Apple ID on your account, needed to authenticate on validation endpoint. Once you have created your Apple ID, must set the following parameters on [`application.yml`](./kpi-server-boot/src/main/resources/application.yml):

```shell
application.kpi.device-check.credentials.private-key: <Base64 Private KEY provided by .p8 file>
application.kpi.device-check.credentials.public-key: <Base64 Public KEY provided by .p8 file>
application.kpi.device-check.team-id: <TEAM_ID>
application.kpi.device-check.key-id: <KEY_ID>
```

Other way is up KPI application with device check simulation mode setting the following parameter:

```shell
application.kpi.device-check.simulate.enabled: true
```

### Modules

KPI Service has four modules:

- `kpi-server-parent`. Parent Maven project to define dependencies and plugins.
- `kpi-server-api`. [DTOs](https://en.wikipedia.org/wiki/Data_transfer_object) exposed.
- `kpi-server-boot`. Main application, global configurations and properties. This module also has integration tests and Java architecture tests with ArchUnit:
- `kpi-server-service`. Business and data layers.

## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type       | Channel                                                |
| ---------- | ------------------------------------------------------ |
| **Issues** | <a href="https://github.com/Radar-COVID/radarcovid-kpi-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/Radar-COVID/radarcovid-kpi-server?style=flat"></a> |

## Contribute

If you want to contribute with this exciting project follow the steps in [How to create a Pull Request in GitHub](https://opensource.com/article/19/7/create-pull-request-github).

## License

This Source Code Form is subject to the terms of the [Mozilla Public License, v. 2.0](https://www.mozilla.org/en-US/MPL/2.0/).
