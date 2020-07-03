# Simple scala backend with JWT tokens

Project requires java 11

## Build the docker image
`sbt docker:publishLocal`

## Start a container
```docker run --rm -p8080:8080 scala-api-jwt:0.1```

## Authenticate the user
`curl -X POST 'http://127.0.0.1:8080/api/login?username=john&password=secret' -i`

## Get user's profile
`curl 'http://127.0.0.1:8080/api/user' -H 'Authorization: Bearer <JWT>' -i`

## Refresh a token (old token gets expired)
`curl -X POST 'http://127.0.0.1:8080/api/refresh' -H 'Authorization: Bearer <JWT>' -i`

## Logout
`curl -X POST 'http://127.0.0.1:8080/api/logout' -H 'Authorization: Bearer <JWT>' -i`
