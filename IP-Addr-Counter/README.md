# IP Addresses Counter

Assignment for Lightspeed.

**IP Addresses Counter** is a simple Java application designed to count the number of unique IP addresses from a given input text file.

## Usage

### Running the Application

#### Requirements

JDK 21+ installed.

1. **Build the Application**:

- Use Maven to build the project:

```sh
mvnw clean package
```

- Or for Windows machines:

```sh
mvnw.cmd clean package
```

- This will generate a `IpAddrCounter.jar` file in the `target` directory.

2. **Run the JAR File**:

- Execute the application by running the following command, replacing `yourfile.txt` with the path to your input file:
```sh
java -jar target/IpAddrCounter.jar yourfile.txt
```
- The application expects a single command-line argument, which is the filename of the input file containing IP addresses.