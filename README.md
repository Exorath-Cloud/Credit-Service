# Credit-Service
The Credit-Service is responsible for tracking and modifying the credit balance of our customers. Cloud product usage consumes credits.

##Endpoints
###/accounts/{accountId} [GET]
###Gets an account by it's id

**argument**
- accountId (string): the id of the account to retrieve

**Response**:
 
 If the account does not exist: ```{}```
 
 If the account exists:
```json
{
  "balance": 15,
  "accountId": "3928391f-b400-4cb2-bf7b-5591fe5a7e34",
  "pendingTransactions":[]
}
```
- balance (int): the amount of currency in the account
- accountId (string) [OPTIONAL]: the id of this account, may not be returned
- pendingTransactions (string array): a list of uncompleted transactionIds
- hasMorePendingTransactions (boolean) [OPTIONAL]: When the returned list of pendingTransactions does not represent all pending transactions. Defaults to false.

###/accounts/{accountId}/unsafeInc?amount=10 [POST]
####Increments the balance without transaction security (meaning the action could never occur or more then once)
If the balance is decremented, this request may be rejected due to insufficient balance.

**arguments**
- accountId (string): the id of the balance to increment
- amount (int): the amount of credits to increment (negative means decrement)

**Response**:

Success statement:
```json
{
  "success": false,
  "error": "player has insufficient balance"
}
```
If success = true it means that the increment was done at least once (most likely once), false means it was most likely never done (could unlikely be more).
If the response is not of this format, something may have happened and there is no way to know what.

###/accounts/{accountId}/inc?amount=10&transactionId=e98f2b2a-35ac-4228-a7b2-389fc339d414 [GET]
####Creates or finishes an increment transaction. Calling this multiple times with same transactionId will not issue multiple transactions.

**Arguments**:
- accountId (string): the id of the balance to increment
- amount (int): the amount of credits to increment (negative means decrement)
- transactionId (string) [OPTIONAL]: the transactionId to associate this transaction with, a new one will be generated if empty.

**Response**:

This simply responds the transaction after processing. If the id already processed this, completed still == true. If the id belongs to a transaction of a different account, this account will be returned!
```json
{
  "accountId": "3928391f-b400-4cb2-bf7b-5591fe5a7e34",
  "transactionId": "e98f2b2a-35ac-4228-a7b2-389fc339d414",
  "transactionState":"COMPLETED",
  "lastUpdate":1482445012748,
  "amount":1
}
```

- accountId (string): the id of the balance to increment
- amount (int): the amount of credits to increment (negative means decrement)
- transactionId (string)[OPTIONAL]: the transactionId to associate this transaction with, a new one will be generated if empty.
- lastUpdate (UNIX timestamp)[OPTIONAL]: the lastTime this transaction was updated (if not returned this means the transaction failed before any updating could happen, fe. when parameters don't match)
- transactionState (TRANSACTION_STATE): Either NOT_CREATED, PENDING, APPLIED, COMPLETED, or CANCELLED.
