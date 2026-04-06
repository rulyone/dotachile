# Stage 1: Build with Maven + JDK 11
FROM maven:3.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies and PostgreSQL JDBC driver (layer cache — only re-runs if pom.xml changes)
RUN mvn dependency:go-offline -B || true
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.6.0 -DoutputDirectory=/app/jdbc-drivers -B
COPY src/ src/
COPY web/ web/
RUN mvn package -DskipTests -B

# Stage 2: Deploy on Payara 5
FROM payara/server-full:5.2022.5

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