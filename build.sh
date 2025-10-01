#!/bin/bash

# Create output directory
mkdir -p out/production/WebCoderFX

# Compile Java files
echo "Compiling Java files..."
javac --module-path /usr/lib/jvm/java-21-openjdk/lib \
      --add-modules javafx.controls,javafx.fxml,javafx.web \
      -d out/production/WebCoderFX \
      src/com/webcoderfx/*.java

# Copy CSS file
echo "Copying resources..."
cp src/styles.css out/production/WebCoderFX/

# Run the application
echo "Running application..."
java --module-path /usr/lib/jvm/java-21-openjdk/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.web \
     -cp out/production/WebCoderFX \
     com.webcoderfx.Main