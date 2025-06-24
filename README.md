# Chasify
A multiuser time tracker that allows users to collaborate on projects.

The Chasify time tracker uses a hierarchy of `Project>Task>Activity` which represents the time complexity from the most complex (left) to the least complex (right).

The real time tracking happend in the activity where a timer can be started.

## Setup

### Prerequisities

1. postgres 17.5
2. java 21
3. docker/podman

### Compilation and running
The compilation of the project can be done with:
```bash
mvn clean package
```

Then you can find the resulting jar in `target/chasify-version.jar`

You can run the jar by running:
```bash
java -jar chasify-version.jar
```
Some test require docker to run. However, you don't necessarily need docker for running the application itself.

The app expects the PostgreSql DB to listen on port `5432` and to have a db called `chasify` with a `public` chema.

> [!WARNING]
> The app reaquires a `.env` file with the following keys to function properly:
> `APP_USER` and `APP_PASS`.

You can specify additional keys when using the `setup.sh` which requires these:
- DB_USER - name of the super user
- DB_PASS - I recommend using no special characters as that might break the setup script
- APP_USER - resctricted app user
- APP_PASS - I recommend using no special characters as that might break the setup script
- CONTAINER_NAME - name for the container to be created
- DB_NAME - the name of the db to be created expects name `chasify`
- SCHEMA_FILE - the path to the schema.sql file relative to the setup script

The `.env` file should be placed next to the jar archive with proper db credentials present

## Limitations

There is a bug when returning to activities overview from creating activity. The new activity does not show in the list of activities. User must navigate back to tasks and then to activities to see the newly added activity.

Archivation is not implemented.

Admin role does not have any meaning at the moment
