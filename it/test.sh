#!/bin/bash

PARENTBUILDIR="../target"
BUILDDIR="./target"
BUILDFILE="${BUILDDIR}/it-dummy-0.0.1-SNAPSHOT.pom"
BUILDSIG="${BUILDDIR}/it-dummy-0.0.1-SNAPSHOT.pom.asc"
PUBLICKEY="./build_at_test_com.public.gpg"

# check
if [ ! -e "${PARENTBUILDIR}" ]
then
    echo "**********"
    echo "error: parent build does not exist: ${PARENTBUILDIR}"
    echo "**********"
    exit 1
fi

# get the version under test
VERSION=`ls ${PARENTBUILDIR}/pgp-maven-plugin-awskms-*.jar | grep -v javadoc.jar | grep -v sources.jar | sed -e 's/^.*\/pgp-maven-plugin-awskms-//' -e 's/\.jar$//'`

# clean out last test
if [ -e "${BUILDDIR}" ]
then
    if ! rm -r "${BUILDDIR}"
    then
        echo "**********"
        echo "error: Failed to remove existing directory: ${BUILDDIR}"
        echo "**********"
        exit 1
    fi
fi

# mvn
if ! mvn -Dpgp-maven-plugin-awskms.version=${VERSION} clean verify
then
    echo "**********"
    echo "error: mvn failed to build successfully"
    echo "**********"
    exit 1
fi

# check outputs
if [ ! -e "${BUILDDIR}" ]
then
    echo "**********"
    echo "error: mvn failed: missing dir ${BUILDDIR}"
    echo "**********"
    exit 1
fi
if [ ! -e "${BUILDFILE}" ]
then
    echo "**********"
    echo "error: mvn failed: missing file ${BUILDFILE}"
    echo "**********"
    exit 1
fi
if [ ! -e "${BUILDSIG}" ]
then
    echo "**********"
    echo "error: mvn failed: missing sig ${BUILDSIG}"
    echo "**********"
    exit 1
fi

# check sig
if ! gpg --no-default-keyring --keyserver-options no-auto-key-retrieve --keyring "${PUBLICKEY}" --verify "${BUILDSIG}"
then
    echo "**********"
    echo "error: gpg failed to verify signature"
    echo "**********"
    exit 1
fi

echo "Success"
exit 0
