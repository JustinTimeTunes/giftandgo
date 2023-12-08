# Full Stack - Technical Assessment

> Author: **Justin Tolson**

> 8 December 2023
---

## The Tests...
There are nine tests in the test harness.

To run them (in Linux) use this command in the project root directory:

`./gradlew test`

The test report should be available here:

`build/reports/tests/test/index.html`

---

## The Application

To run the app, use this command in the project root directory:

`./gradlew bootRun`

Once the app is running you can send requests such as these, which will return an **OutcomeFile.json**.

`curl -F file='@EntryFile.txt' -O -J http://localhost:8080/uploadFile`
 
or

`curl -F file='@EntryFile.txt' -O -J http://localhost:8080/uploadFile?skipValidation=true`

### Test Files

The original test file, plus three invalid ones are located here:

`src/test/resources`

Alternatively, **Postman** can be used for testing.

---

## Assumptions

Failed validation could've caused the whole request to fail, but instead I chose to only fail the individual lines with the errors, processing the other lines as normal.
This does mean that the results may be a combination of successful parses along with the errored ones.
This may not be ideal, but for a simple project it should demonstrate basic functionality.

Also, validation could've included making sure the speeds are positive numbers, and that the top speed is greater than the average speed.
However, having an open-ended spec, I wasn't sure exactly what was needed, so I decided that might be overkill for this project.
