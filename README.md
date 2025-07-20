# Time Deposit Refactoring Kata - Take home assignment
## XA bank time deposit

## Time Deposit Balance Management API

### Overview
This application provides a RESTFull API to manage time deposits, including calculating and updating balances based on specific interest rules, and retrieving time deposit information along with their associated withdrawals.

It is built with **Spring Boot** and uses **PostgreSQL** as its database.

The application refactored an existing Java codebase, ensuring no breaking changes to core components while implementing new functionality.

### API Endpoints

The API provides the following endpoints:

* **`GET /time-deposits`**:
    * **Description:** Retrieves a list of all time deposits, including their associated withdrawals.
      * **Response:**
          ```json
          {
            "data": [
                {
                  "id": Integer,
                  "planType": String, // e.g., "BASIC", "STUDENT", "PREMIUM"
                  "balance": Decimal,
                  "days": Integer,
                  "withdrawals": [
                    {
                      "id": Integer,
                      "amount": Decimal,
                      "date": Date // YYYY-MM-DD
                    }
                  ]
                }
              ]
          }
          ```
* **`POST /time-deposits/update-balances`**:
    * **Description:** Triggers the calculation and update of all time deposit balances in the database according to predefined interest rules.
    * **Request Body:** None
    * **Response:** `204 Accepted`

For complete API details and specification, refer to the API specification file: [`src/main/resources/static/spec.yaml`](src/main/resources/static/spec.yaml).

### Database Schema

The application uses a PostgreSQL database with the following tables:

* **`time_deposits`**:
    - `id`: Integer (Primary Key)
    - `plan_type`: String (Enum: BASIC, STUDENT, PREMIUM)
    - `balance`: Decimal(Precision: 19, Scale: 2)
    - `days`: Integer
* **`withdrawals`**:
  - `id`: Integer (Primary Key)
  - `time_deposit_id`: Integer (Foreign Key referencing `time_deposit.id`)
  - `amount`: Decimal(Precision: 19, Scale: 2)
  - `date`: Date (YYYY-MM-DD)

### How to Run Locally

To run this application locally, you'll need **Docker** and a **Java Development Kit (JDK 17+)** with **Maven** installed.

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/soniaood/sonia-time-deposit-take-home-kata.git
    ```

2.  **Build the Application:**
    Navigate to the project root directory (where `pom.xml` is located) and build the Maven project:
    ```bash
    mvn clean install
    ```
3.  **Start the Database with Docker Compose:**
    From the project root, start the PostgreSQL database container:
    ```bash
    docker compose up -d db
    ```

4.  **Run the Spring Boot Application:**
    Once the database is up, start the Spring Boot application. This step will automatically apply Flyway migrations (to create the schema) and, `data.sql` gets executed to populate initial test data. Please refer to `spring.sql.init.mode` configuration in `application.properties` to control the test data creation.

    ```bash
     mvn spring-boot:run
    ```
    Alternatively, you can run the application directly from your IDE.


5.  **Access the API:**
    Once the application starts (`http://localhost:8080`), you can access the API endpoints using `curl`:

    * **Get All Time Deposits:**
        ```bash
        curl http://localhost:8080/time-deposits
        ```
    * **Update All Time Deposit Balances (Trigger Interest Calculation):**
        ```bash
        curl -X POST http://localhost:8080/time-deposits/update-balances
        ```

### 5. Implementation Details

* **Refactored Interest Calculation:** The original `TimeDepositCalculator.updateBalance` method signature was preserved. The behavior of this method remains unchanged. Its internal logic was refactored to use a **Strategy Pattern** with `TimeDepositInterestCalculator` interface and separate implementations (`BasicTimeDepositInterestCalculator`, `StudentTimeDepositInterestCalculator`, `PremiumTimeDepositInterestCalculator`).
  * Interest calculation follows the specified plan interest rate and day-based conditions.
  * The interest rate is defined within `TimeDepositPlan` enum (acting as the single source of truth for the plan's interest rates). In the other hand, each calculator implementation is responsible for its specific calculation rules.
  * Precision for balance amounts is maintained using `BigDecimal` throughout calculations defining scale. Intermediate interest calculations use a scale of 6 to avoid precision loss, while final balances are rounded to 2 decimal places.* **Type-Safe Entity Design:** The `planType` field in `TimeDeposit` is using `TimeDepositPlan` enum, ensuring type safety. A custom `AttributeConverter` handles the mapping between the enum and the database's string column, providing robustness and flexibility.
* **API Response Structure:** The `GET /time-deposits` endpoint returns data wrapped in a **`ListResponse`**. This design facilitates future extensions, such as implementing pagination or adding other response metadata, without breaking existing client contracts.
* **Code Integration:** A dedicated `TimeDepositMapper` handles conversions between the JPA entities, API DTOs, and the existing internal `TimeDeposit` class, ensuring no breaking changes were introduced.
* **No Exception Handling:** As per requirements, invalid input/exception handling is omitted for simplicity of the assignment.
* **Logic simplification:** As per requirements, `updateAllTimeDepositBalances` method focuses solely on applying interest, without handling withdrawals or other business logic. `POST /time-deposits/update-balances` endpoint will apply interest every time it's called, which is acceptable for this exercise.



### 6. Future Improvements & Considerations

* **Asynchronous Balance Updates & Performance Improvements:** The `updateAllTimeDepositBalances` method could be significantly optimized by making the balance updates asynchronous, allowing the API to return immediately while the updates are processed in the background. This improvement, potentially utilizing multi-threading (e.g., ExecutorService) or batch processing frameworks, would enhance performance for large numbers of time deposits and aligns with the current API `202 Accepted` response indicating background processing.
* **More Robust Error Handling:** Implementing comprehensive error handling, validation, and logging for improved readiness.
* **Database Configuration & Security:** Currently, database credentials (URL, username, password) and test data creation are directly configured within `application.properties`. While functional for local development, hardcoding sensitive information is a significant security risk. For improved security and flexibility, these properties should be externalized (using environment variables or dedicated secrets management systems).
* **Integration Tests:** Implementing integration tests to ensure the API endpoints and database interactions work as expected.
* **Performance Tests:** Implementing performance tests to evaluate the API's scalability and response times under load.