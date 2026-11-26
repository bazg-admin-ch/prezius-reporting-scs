#!/bin/bash
# when an SSL error occurs:
# cp /usr/share/ca-certificates/extra/BITProxyCA_AllInOneCerts.crt  ~/.aws/certs/AdminCCRootChain.crt
#
export AWS_CA_BUNDLE=$HOME/.aws/certs/AdminCCRootChain.crt

set -e

PROFILE="nivel-prezius-dev"

# Variables – configure if needed
DOMAIN="maven"
NPM_DOMAIN="npm"
DOMAIN_OWNER="000000000000"
REGION="eu-central-1"
REPO="main"
NPM_REPO="main"

SETTINGS_FILE="$HOME/.m2/settings.xml"
BACKUP_FILE="$HOME/.m2/settings.xml.backup"

echo "Checking AWS SSO login status..."

if ! aws sts get-caller-identity --profile "$PROFILE" &>/dev/null; then
    echo "AWS SSO session expired or missing. Logging in..."
    aws sso login --profile "$PROFILE"
else
    echo "AWS SSO session is active."
fi

echo "Checking for existing settings.xml..."

if [[ -f "$SETTINGS_FILE" ]]; then
    if ! grep -q "codeartifact" "$SETTINGS_FILE"; then
        echo "Backing up existing settings.xml to settings.xml.backup..."
        cp "$SETTINGS_FILE" "$BACKUP_FILE"
    else
        echo "settings.xml already references codeartifact – skipping backup."
    fi
fi

echo "Fetching CodeArtifact token and repository URLs..."

AUTH_TOKEN=$(aws codeartifact get-authorization-token \
    --domain "$DOMAIN" \
    --domain-owner "$DOMAIN_OWNER" \
    --region "$REGION" \
    --profile "$PROFILE" \
    --query authorizationToken \
    --output text)

REPO_URL=$(aws codeartifact get-repository-endpoint \
    --domain "$DOMAIN" \
    --domain-owner "$DOMAIN_OWNER" \
    --repository "$REPO" \
    --format maven \
    --region "$REGION" \
    --profile "$PROFILE" \
    --query repositoryEndpoint \
    --output text)

REPO_URL_RELEASE="${REPO_URL%/maven}/release"
REPO_URL_SNAPSHOT="${REPO_URL%/maven}/snapshot"

echo "Writing new settings.xml..."

cat > "$SETTINGS_FILE" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>nivel-codeartifact</id>
            <username>aws</username>
            <password>$AUTH_TOKEN</password>
        </server>
        <server>
            <id>nivel-codeartifact-release</id>
            <username>aws</username>
            <password>$AUTH_TOKEN</password>
        </server>
        <server>
            <id>nivel-codeartifact-snapshot</id>
            <username>aws</username>
            <password>$AUTH_TOKEN</password>
        </server>
    </servers>

    <mirrors>
        <mirror>
            <id>nivel-codeartifact</id>
            <url>$REPO_URL</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
    </mirrors>

   <proxies>
        <proxy>
            <id>bit-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.example.com</host>
            <port>8080</port>
            <nonProxyHosts>.admin.ch</nonProxyHosts>
        </proxy>
    </proxies>

   <profiles>
        <profile>
            <id>nivel-codeartifact-profile</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <releaseRepositoryId>nivel-codeartifact-release</releaseRepositoryId>
                <releaseRepositoryName>NIVEL AWS CodeArtifact Release Repository</releaseRepositoryName>
                <releaseRepositoryUrl>$REPO_URL_RELEASE</releaseRepositoryUrl>
                <snapshotRepositoryId>nivel-codeartifact-snapshot</snapshotRepositoryId>
                <snapshotRepositoryName>NIVEL AWS CodeArtifact Snapshot Repository</snapshotRepositoryName>
                <snapshotRepositoryUrl>$REPO_URL_SNAPSHOT</snapshotRepositoryUrl>
            </properties>
            <repositories>
                <repository>
                    <id>nivel-codeartifact</id>
                    <name>NIVEL AWS CodeArtifact Repository</name>
                    <url>$REPO_URL</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>interval:10</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>nivel-codeartifact</id>
                    <name>NIVEL AWS CodeArtifact Plugin Repository</name>
                    <url>$REPO_URL</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>interval:10</updatePolicy>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>
</settings>
EOF

echo "settings.xml updated successfully."

echo "Configuring NPM repository for CodeArtifact"
aws codeartifact login --tool npm \
          --domain ${NPM_DOMAIN} \
          --domain-owner "${DOMAIN_OWNER}" \
          --region "${REGION}" \
          --profile "${PROFILE}" \
          --repository "${NPM_REPO}"
