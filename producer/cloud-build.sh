#!/bin/bash

BUILDDIR=cloudbuild

rm -rf $BUILDDIR
mkdir -p $BUILDDIR
cp target/demo-producer-0.0.1-SNAPSHOT.jar $BUILDDIR
cp Dockerfile $BUILDDIR

gcloud builds submit --config cloudbuild.yaml $BUILDDIR

