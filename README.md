# prezius-reporting-scs

Prezius Reporting provides operational reports for precious-metal analysis and hallmarking activities at the Swiss Federal Office for Customs and Border Security. Reporting views are populated from events published by connected Prezius services.

## Features

- Retrieve analysis and hallmarking reports.
- Filter operational data by control office.
- Export filtered hallmarking reporting data as CSV.

## Build and usage

Use Java 25, Maven and Node.js 22 with npm. Run `mvn verify` from the repository root to build and test the modules. The Angular application is in `prezius-reporting-ui`; its `package.json` provides build, test and development-server commands.

Builds require access to the configured artifact repositories. Configure database, messaging, authentication and service integrations before starting the executable backend JAR. The published source does not include a complete deployment environment or operational reporting data.

## Consumed billing events

The service consumes `PreziusBillingCreatedEvent` version `1.6.0` to create reporting items for analysis and hallmarking orders. Billing types `MANUAL`, `APPLICATION` and `REGISTRATION` are ignored: they do not create a report item, including when the business-partner ID is empty.

## License

Licensed under the [Apache License 2.0](LICENSE).
See [third-party licenses](THIRD-PARTY-LICENSES.md) for dependency notices.
