# hello-java-lambda

Pequeño ejemplo de una función Lambda en Java expuesta vía API Gateway, empaquetada con Maven Shade y desplegada automáticamente con GitHub Actions + AWS SAM.

---

## 🎯 Objetivo

- Construir un “fat-jar” de la función Java con Maven Shade.  
- Empaquetar y desplegar el stack Serverless (SAM) en AWS usando GitHub Actions.  
- Exponer un endpoint `GET /hello` que responda "Hola mundo desde Lambda en Java!".

---

## 🛠️ Estructura de carpetas

```
.
├── .github
│   └── workflows
│       └── deploy.yml
├── README.md
├── docs
│   ├── diagram.py
│   └── architecture.png
├── pom.xml
├── sam-template.yaml
└── src
    └── main
        └── java
            └── helloworld
                └── HelloWorldHandler.java
```

---

## 🔧 Flujo CI/CD (`.github/workflows/deploy.yml`)

1. Checkout del repo.  
2. Setup Java 11 (Temurin).  
3. Verificar AWS CLI v2 preinstalado.  
4. Configurar credenciales con `aws-actions/configure-aws-credentials@v2`.  
5. Instalar SAM CLI.  
6. Maven **clean package** (genera `target/hello-java-lambda-1.0-SNAPSHOT.jar`).  
7. Verificación de que el JAR contiene `helloworld/HelloWorldHandler.class`.  
8. `sam package` → sube artefacto a S3.  
9. `sam deploy` → crea/actualiza el stack en CloudFormation.

---

## 🗺️ Plantilla SAM (`sam-template.yaml`)

\`\`\`yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: Lambda Java + API Gateway example

Globals:
  Function:
    Timeout: 10
    Runtime: java11
    MemorySize: 512

Resources:
  HelloWorldFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: helloworld.HelloWorldHandler
      CodeUri: target/hello-java-lambda-1.0-SNAPSHOT.jar
      Events:
        ApiEvent:
          Type: Api
          Properties:
            Path: /hello
            Method: GET

Outputs:
  HelloWorldApiUrl:
    Description: "URL de tu endpoint"
    Value: !Sub "https://${ServerlessRestApi}.execute-api.${AWS::Region}.amazonaws.com/Prod/hello"

  HelloWorldApiCurl:
    Description: "Comando curl para invocar la API"
    Value: !Sub |
      curl -X GET \
        "https://${ServerlessRestApi}.execute-api.${AWS::Region}.amazonaws.com/Prod/hello"
\`\`\`

---

## 📈 Diagrama de Arquitectura

Para generar el diagrama CI/CD:

1. Instala las dependencias:
   \`\`\`
   pip install diagrams graphviz
   \`\`\`
2. Ejecuta:
   \`\`\`
   python docs/diagram.py
   \`\`\`
3. El script generará \`docs/architecture.png\`.  
4. Inclúyelo en tu README:
   \`\`\`markdown
   ![Arquitectura CI/CD](docs/architecture.png)
   \`\`\`

---

## 🔑 Secrets de GitHub

| Nombre                  | Descripción                                |
|-------------------------|--------------------------------------------|
| \`AWS_ACCESS_KEY_ID\`     | Access key AWS con permisos de deploy.     |
| \`AWS_SECRET_ACCESS_KEY\` | Secret key AWS correspondiente.            |
| \`AWS_DEFAULT_REGION\`    | Región AWS (ej: \`us-east-1\`).            |
| \`S3_BUCKET\`             | Bucket S3 donde se subirán los artefactos. |
| \`STACK_NAME\`            | Nombre del CloudFormation stack.           |
