#!/bin/bash

set -eu

CLUSTER_NAME=prezius-cluster
DB_NAME=prezius_reporting_db # Must contain only letters, numbers and underscores

function createDb() {
  SPACE=$1
  PLAN=$2

  cf target -o BAZG-Prezius -s $SPACE
  cf create-service postgresql ${PLAN} ${CLUSTER_NAME}-${SPACE}

  CLUSTER_GUID=$(cf service ${CLUSTER_NAME}-${SPACE} --guid)
  echo "Cluster GUID: ${CLUSTER_GUID}"

  cf create-service postgresql-database postgresql-database-standard ${DB_NAME} -c "{\"instance_guid\":\"${CLUSTER_GUID}\"}"
  echo "Done"
}

createDb dev postgresql-15-single-starter-1
createDb ref postgresql-15-single-starter-1
createDb abn postgresql-15-single-starter-1
createDb prod postgresql-15-single-starter-1
