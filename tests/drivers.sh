# 1) Crear un driver
curl -X POST https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"Fabian","lastname":"Tobar","dni":"1033724","username":"fabiantobarn@gmail.com","licenseCategory":"c1","licenseExpiration":"2026-06-20","address":"fake street 123","whatsapp":"3045311743","phone":"3045311743","email":"fabiantobarn@gmail.com","password":"123456","locality":"Tunjuelito","neighborhood":"San Carlos","latitude":10.0,"longitude":50.0,"role":"driver"}'

# 2) Listar todos los drivers
curl -X GET https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers

# 3) Obtener un driver por ID
curl -X GET https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1

# 4) Actualizar un driver (ID = 1)
curl -X PUT https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1 \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"Fabian Ricardo","lastname":"Tobar Numesqui","dni":"1033724","username":"fabiantobarn@gmail.com","licenseCategory":"c1","licenseExpiration":"2026-06-20","address":"fake street 123","whatsapp":"3045311743","phone":"3045311743","email":"fabiantobarn@gmail.com","password":"123456","locality":"Tunjuelito","neighborhood":"San Carlos","latitude":10.0,"longitude":50.0,"role":"driver"}'

# 5) Eliminar un driver (ID = 1)
curl -X DELETE https://ckemxpwktk.execute-api.us-east-1.amazonaws.com/Prod/api/v1/drivers/1
