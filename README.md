# Draft store performance tests

Performance test for [draft store](https://github.com/hmcts/draft-store)

## Configuration
Set urls for draft-store, idam and s2s with the following environment variables:
- `DRAFT_STORE_BASE_URL`
- `IDAM_URL`
- `S2S_URL`

## Running
```bash
$ ./gradlew gatlingRun-uk.gov.hmcts.reform.draftstore.CreateMultipleDrafts
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
