# SQL SELECT query parser

Assignment for Lightspeed.

**SQL SELECT query parser** is a Java app designed to parse SQL SELECT requests.

## Important Notice

After a few days working on it, I've finally noticed that it is *not* required to use only use the features of the standard Java/Kotlin library like in **IP Addresses Counter** or **Deep Clone**. In this case it's probably more appropriate to use tools like ANTLR to parse it, but I was too deep in the tokenizer to rebuild it. Currently, the app parse conditions in WHERE clauses and such as flat strings. 

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

- This will generate a `SqlParser.jar` file in the `target` directory.

2. **Run the JAR File**:

- Execute the application by running the following command:
```sh
java -jar target/SqlParser.jar
```