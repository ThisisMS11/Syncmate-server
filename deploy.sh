#!/bin/bash
set -e

PROJECT_ID="macro-precinct-458804-t0"
SERVICE_NAME="syncmate"
REGION="us-central1"
IMAGE="gcr.io/$PROJECT_ID/$SERVICE_NAME"

echo "Building and pushing image to GCR..."
gcloud builds submit --tag "$IMAGE"

echo "Deploying new revision to Cloud Run..."
gcloud run deploy "$SERVICE_NAME" \
  --image "$IMAGE" \
  --region "$REGION" \
  --platform managed

echo "Done! Service URL:"
gcloud run services describe "$SERVICE_NAME" --region "$REGION" --format "value(status.url)"
