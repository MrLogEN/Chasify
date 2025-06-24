#!/bin/bash

set -e  # Optional: fail immediately on any command error (but we'll do custom checks too)

# Function to run a command and check for "ERROR:" in output
run_and_check() {
  local output
  output=$("$@" 2>&1)   # run command, capture stdout+stderr
  if echo "$output" | grep -q '^ERROR:'; then
    echo "❌ Error detected during command: $*"
    echo "$output"
    exit 1
  else
    echo "$output"
  fi
}

# Load environment variables from .env
if [ -f .env ]; then
  set -a
  source .env
  set +a
else
  echo ".env file not found!"
  exit 1
fi

# Use variables
run_and_check podman run --name "$CONTAINER_NAME" \
  -e POSTGRES_USER="$DB_USER" \
  -e POSTGRES_PASSWORD="$DB_PASS" \
  -e POSTGRES_DB="$DB_NAME" \
  -p 5432:5432 \
  -d docker.io/library/postgres:17.5

echo "⏳ Waiting for PostgreSQL to be ready..."
until podman exec "$CONTAINER_NAME" pg_isready -U "$DB_USER" >/dev/null 2>&1; do
  sleep 1
done
echo "✅ PostgreSQL is ready."

# Step 3: Copy schema file into the container
run_and_check podman cp "$SCHEMA_FILE" "$CONTAINER_NAME":/schema.sql

# Step 4: Execute schema.sql inside the container
run_and_check podman exec -u postgres "$CONTAINER_NAME" psql -d "$DB_NAME" -f /schema.sql

echo "🎉 Schema loaded successfully into $DB_NAME!"

# Step 5: Create the application user and grant privileges

run_and_check podman exec -i "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" <<EOF
DO \$\$
BEGIN
  IF NOT EXISTS (
    SELECT FROM pg_catalog.pg_roles WHERE rolname = '${APP_USER}'
  ) THEN
    CREATE USER "${APP_USER}" WITH PASSWORD '${APP_PASS}';
  END IF;
END
\$\$;
GRANT USAGE ON SCHEMA public TO "${APP_USER}";
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "${APP_USER}";

DO \$\$
DECLARE
  seq RECORD;
BEGIN
  FOR seq IN
    SELECT sequence_schema, sequence_name
    FROM information_schema.sequences
    WHERE sequence_schema = 'public'
  LOOP
    EXECUTE 'GRANT USAGE, SELECT ON SEQUENCE ' || quote_ident(seq.sequence_schema) || '.' || quote_ident(seq.sequence_name) || ' TO "${APP_USER}"';
  END LOOP;
END
\$\$;
EOF

echo "👤 App user '${APP_USER}' created and permissions granted."

