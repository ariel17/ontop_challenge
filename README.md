# OnTop Home Challenge

Hi there! This is my implementation for the proposed home challenge. [Read the
requirements here](./docs/requirements.pdf).

## How to run it

```bash
# Locally
$ ./gradlew bootRun

# With Docker Compose
$ docker-compose up  # use -d to detach
```

## Diagrams

### Archicture

![Architecture diagram](./docs/architecture.png)

### Sequence

![Sequence diagram](./docs/sequence.png)

### Models

![Models diagram](./docs/models.png)

## Examples

### Successful transfer

![Postman success example](./docs/postman_success.png)

### Reverted transfer by Payment Provider error

![Postman provider error example](./docs/postman_provider_error.png)

### Failed by Wallet API error

![Postman wallet error example](./docs/postman_wallet_error.png)
