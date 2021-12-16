rm -rf cloudbuild
mkdir -p cloudbuild
cp target/demo-consumer-0.0.1-SNAPSHOT.jar cloudbuild 
cp Dockerfile cloudbuild 
gcloud builds submit --config cloudbuild.yaml cloudbuild

