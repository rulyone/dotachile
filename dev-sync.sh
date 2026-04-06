#!/bin/bash
# Hot-reload development script for DotaCL.
#
# Usage:
#   ./dev-sync.sh java      Compile Java sources and push to container (triggers redeploy)
#   ./dev-sync.sh views     Sync all XHTML/CSS/images to container (instant, no redeploy)
#   ./dev-sync.sh all       Do both
#   ./dev-sync.sh watch     Watch for XHTML/CSS changes and auto-sync (requires fswatch)

set -e

CONTAINER=$(docker compose ps -q app | head -1)
APP_DIR=/opt/payara/appserver/glassfish/domains/domain1/applications/DotaCL

if [ -z "$CONTAINER" ]; then
    echo "Error: app container is not running. Start it with 'docker compose up'"
    exit 1
fi

sync_views() {
    echo "Syncing XHTML/CSS/images..."
    docker cp web/web/.         "$CONTAINER:$APP_DIR/web/"
    docker cp web/resources/.   "$CONTAINER:$APP_DIR/resources/"
    docker cp web/static/.      "$CONTAINER:$APP_DIR/static/"
    for f in web/*.xhtml; do
        docker cp "$f" "$CONTAINER:$APP_DIR/"
    done
    echo "Views synced."
}

sync_java() {
    echo "Compiling Java sources..."
    mvn compile -q
    echo "Syncing classes to container..."
    docker cp target/classes/. "$CONTAINER:$APP_DIR/WEB-INF/classes/"
    echo "Triggering redeploy..."
    docker exec "$CONTAINER" touch "$APP_DIR/.reload"
    echo "Done. Payara will redeploy within a few seconds."
}

watch_views() {
    if ! command -v fswatch &> /dev/null; then
        echo "fswatch is required for watch mode. Install with: brew install fswatch"
        exit 1
    fi
    echo "Watching web/ for changes... (Ctrl+C to stop)"
    fswatch -o web/ | while read; do
        sync_views
    done
}

case "${1:-all}" in
    java)   sync_java ;;
    views)  sync_views ;;
    all)    sync_java; sync_views ;;
    watch)  watch_views ;;
    *)      echo "Usage: $0 {java|views|all|watch}"; exit 1 ;;
esac