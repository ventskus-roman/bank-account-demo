# How to run?
``./gradlew bootRun``

# Endpoints 

### Create account
``curl -X PUT http://localhost:8080/account | json_pp``

(you can remove | json_pp part if you want)

Endpoint will return created account, please use id from the response
for next operations

### Get balance
``curl -X GET http://localhost:8080/account/1 | json_pp``

you can use your id

### Deposit 
``curl -X POST -d amount=100 http://localhost:8080/account/1/deposit | json_pp``

### Withdraw
``curl -X POST -d amount=100 http://localhost:8080/account/1/withdraw | json_pp``
