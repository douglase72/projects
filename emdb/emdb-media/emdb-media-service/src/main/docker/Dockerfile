# Use Oracle Linux as the base OS with the GraalVM built on top of it.
FROM container-registry.oracle.com/graalvm/jdk:25

WORKDIR /deploy

# Create a non-root user to run the application.
RUN useradd -u 1001 appuser && chown -R 1001:0 /deploy
USER 1001

# Copy the pre-built application files from your local 'build/quarkus-app' directory
# into the container's working directory.
COPY build/quarkus-app/ ./

EXPOSE 60316

CMD ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "quarkus-run.jar"]