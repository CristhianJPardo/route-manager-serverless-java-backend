# 1) Crear un cliente
curl -X POST https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/clients \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"Jane","lastname":"Doe","dni":"12345678","username":"jdoe","address":"Calle 123","whatsapp":"3001234567","phone":"555-1234","email":"jane.doe@example.com","password":"secret","locality":"Bogotá","neighborhood":"Chapinero","latitude":4.6243,"longitude":-74.0638,"role":"CLIENT","registrationDate":"2025-07-23","precedence":1,"enabled":true,"credentialsNonExpired":true,"accountNonExpired":true,"accountNonLocked":true}'

# 2) Listar todos los clientes
curl -X GET https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/clients

# 3) Obtener un cliente por ID
curl -X GET https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/clients/1

# 4) Actualizar un cliente (ID = 1)
curl -X PUT https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/clients/1 \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"JaneUpdated","lastname":"DoeUpdated","address":"Carrera 45","whatsapp":"3107654321","phone":"555-9876","email":"jane.updated@example.com","locality":"Bogotá","neighborhood":"Usaquén","latitude":4.7109,"longitude":-74.0721,"role":"CLIENT","precedence":2,"enabled":true,"credentialsNonExpired":true,"accountNonExpired":true,"accountNonLocked":true}'

# 5) Eliminar un cliente (ID = 1)
curl -X DELETE https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/clients/1
