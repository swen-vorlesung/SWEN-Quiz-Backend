# SWEN Quiz Backend

This project is for educational purpose tho show off the software development cycle.

## Project Documentation

Read [here](https://github.com/ds-slohr/SWEN-Quiz-Backend/blob/main/src/docs/arc42.adoc) the project documentation.

## Project setup

### Run everything in Docker

First you need to build the project with maven:

```
mvn clean install
```

Then just run the docker-compose:

```
docker-compose up -d
```

### Just Run the SQL Server for local Development

Just run docker-compose for the ms-sql-server:
```
docker-compose up -d ms-sql-server
```

