# money-transfer
Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.


Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

Technology stack used:
1) Java 11
2) Spark Java as simple REST API server
3) Lombok to avoid boilerplate code
4) JUnit, async-http-client for tests
5) GSON for JSON serialization

Postman collection with sample requests attached

Endpoints:
1) Create account:
POST http://localhost:4567/api/accounts/create
Body:
{
	"id": "9e6d95bb-f78a-4723-b532-c9af05e01fa1",
	"balance": "100"
}

2) Create transaction (transactionId should be unique to avoid accidental duplicate transactions:
POST http://localhost:4567/api/transactions/create
Body:
{
	"transactionId": "19fa2d47-4132-40b2-9b2c-68074c6b5675",
	"receiverId": "9e6d95bb-f78a-4723-b532-c9af05e01fa1",
	"senderId": "5ed7c238-37cf-4528-845a-f5ad4e2ab6f1",
	"amount": "1"
}

3) Get all transactions:
GET http://localhost:4567/api/accounts

4) Get transaction by id:
GET http://localhost:4567/api/transactions/{id}

5) Get account by id:
GET http://localhost:4567/api/accounts/{id}
