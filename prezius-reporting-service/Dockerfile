FROM 000000000000.dkr.ecr.eu-central-2.amazonaws.com/jeap-runtime-coretto:25.20261118061525@sha256:96d7100d6c79d879899248a053c5a81ba3466053c3b0e9e5b3e3593e998ac1c9

# Copy application files
COPY target/prezius-reporting-service.jar /app/app.jar

# Copy : target/classes*.jks to /truststore
COPY target/classes/*.jks  /truststore/

ENTRYPOINT ["java", "-jar", "app.jar"]
