# incubator-storage-service

Writing files
```
curl --data-binary @"filename" -H "Authorization: Bearer <access_token>" 'https://testing.datascience.ch:9000/write/folder%2Ffilename'
```

Reading files
```
curl -H "Authorization: Bearer <access_token>" 'https://testing.datascience.ch:9000/read/folder%2Ffilename'
```