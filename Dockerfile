# Stage 1: Build with Maven + JDK 11
FROM maven:3.8-openjdk-11@sha256:805f366910aea2a91ed263654d23df58bd239f218b2f9562ff51305be81fa215 AS build
# Pinned 2026-04-20. Multi-arch manifest-list digest (amd64+arm64). Tag maven:3.8-openjdk-11 is effectively frozen (last push 2022-08-03 — openjdk-11 variants purged from Docker Official Images; no newer maven:3.8.x-openjdk-11 exists). Update cadence: monthly (verify tag still resolves; if purged, migrate to maven:3.8.8-eclipse-temurin-11).
WORKDIR /app
COPY pom.xml .
# Download dependencies and PostgreSQL JDBC driver (layer cache — only re-runs if pom.xml changes)
RUN mvn dependency:go-offline -B || true
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.6.0 -DoutputDirectory=/app/jdbc-drivers -B
COPY src/ src/
COPY web/ web/
RUN mvn package -DskipTests -B

# Stage 2: Deploy on Payara 5
FROM payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75
# Payara 5 EOL — 5.2022.5 is the last release on the 5.x line. DO NOT upgrade to Payara 6 (Jakarta EE 10 breaks javax.* imports across the codebase).

# Add PostgreSQL JDBC driver (copied from build stage)
USER root
COPY --from=build /app/jdbc-drivers/postgresql-42.6.0.jar /opt/payara/appserver/glassfish/domains/domain1/lib/
RUN chown payara:payara /opt/payara/appserver/glassfish/domains/domain1/lib/postgresql-42.6.0.jar

# Create uploads directory
RUN mkdir -p /home/dotachile/UPLOADS && \
    chown -R payara:payara /home/dotachile

USER payara

# Set JSF to Development mode (Facelets auto-refresh for dev-sync.sh hot reload)
ENV JVM_ARGS="-Djavax.faces.PROJECT_STAGE=Development"

# Copy post-boot commands for datasource/realm/mail configuration
COPY docker/post-boot-commands.asadmin /opt/payara/config/post-boot-commands.asadmin

# Deploy the WAR
COPY --from=build /app/target/DotaCL.war /opt/payara/deployments/DotaCL.war

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD bash -c 'echo > /dev/tcp/localhost/8080' || exit 1