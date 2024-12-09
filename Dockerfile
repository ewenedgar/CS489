# Use an official JDK runtime as a parent image
FROM amazoncorretto:23

# Set the working directory in the container
WORKDIR /app

#is used to set an environment variable
ENV PORT=8080
# Copy the executable JAR file into the container
COPY build/libs/horelo-0.0.1-SNAPSHOT.jar /app

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/horelo-0.0.1-SNAPSHOT.jar"]

#docker tag myapp:latest ewen256/horelo:0.0.1-SNAPSHOT