# TNExT Integration Tests

To run integration tests using Docker:

```sh
docker-compose up
```

To run tests on a Mac:

1. `brew install python3 openssl`
2. `pip3 install -r api-tests/requirements-mac.txt`
3. `resttest.py http://localhost:8080 api-tests/*.yml`