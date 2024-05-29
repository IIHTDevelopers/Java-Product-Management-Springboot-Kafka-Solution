### Product Application

- Provides product operation like alloted/cancellation using Apache kafka.

### Product Application Producer Module

- ** Create Product using api endpoint /api/products/ and publish product event message to kafka using topic create-product.

### Product Application Consumer Module

- ** ProductEventsConsumerManualOffset Listening product event and store details in database.
- Incase of any failure happen in ProductEventsConsumerManualOffset then New message has been published to
  retry-create-product topic and store failure record in database

### Product Application Retry Module

- ** ProductEventsRetryConsumer is Responsible to listen failed consumed message.
- A scheduler RetryScheduler is running wit 5 sec internal to fetch all failure record from database and publish again
  on kafka

---
