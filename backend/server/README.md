# CapitalPay
Capital Pay System on Java Spring Boot

Host:
https://capitalpay.kz

Example:
https://capitalpay.kz/users/sign-up

# Sing Up and Login



Sign Up
```
POST /users/sign-up HTTP/1.1
ost: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=F5B0474813239115FF08C8EE7F5817F8

{
    "username": "admin",
    "password": "qwerty"
}
```

Login requst
```
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=F5B0474813239115FF08C8EE7F5817F8

{
    "username": "admin",
    "password": "qwerty"
}
```
response Headers with JWT security token:
```
...
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYwMDIzNDA3OX0.u7EMvWXnMWVgJJtAiDXKik-2tiu5hlBFNdxKm5WJ3xD5MpiFq2bzoLvZqI8ywyyZT3f_w_hPuxBjHd8kLVXzqg
...
```
Request another endpoints with JWT security token
```
GET /users/test HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYwMDIzNDA3OX0.u7EMvWXnMWVgJJtAiDXKik-2tiu5hlBFNdxKm5WJ3xD5MpiFq2bzoLvZqI8ywyyZT3f_w_hPuxBjHd8kLVXzqg
Content-Type: application/json
Cookie: JSESSIONID=F5B0474813239115FF08C8EE7F5817F8

{
    "username":"admin",
    "password":"qwerty"
}
```
