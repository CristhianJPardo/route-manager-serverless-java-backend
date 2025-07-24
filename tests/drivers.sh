# 1) Crear un driver
curl -X POST https://cfq1m4xlsb.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"Jane","lastname":"Doe","dni":"12345678","username":"jdoe","licenseCategory":"C1","licenseExpiration":"2026-12-31","address":"Calle 123","whatsapp":"3001234567","phone":"555-1234","email":"jane.doe@example.com","password":"secret","locality":"Bogotá","neighborhood":"Chapinero","latitude":4.6243,"longitude":-74.0638,"role":"DRIVER"}'

# 2) Listar todos los drivers
curl -X GET https://cfq1m4xlsb.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers

# 3) Obtener un driver por ID
curl -X GET https://cfq1m4xlsb.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1

# 4) Actualizar un driver (ID = 1)
curl -X PUT https://cfq1m4xlsb.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1 \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"JaneUpdated","lastname":"DoeUpdated","dni":"12345678","username":"jdoe","licenseCategory":"C1","licenseExpiration":"2026-12-31","address":"Calle 123","whatsapp":"3001234567","phone":"555-1234","email":"jane.doe@example.com","password":"secret","locality":"Bogotá","neighborhood":"Chapinero","latitude":4.6243,"longitude":-74.0638,"role":"DRIVER"}'

# 5) Eliminar un driver (ID = 1)
curl -X DELETE https://cfq1m4xlsb.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1
