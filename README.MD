#  Construir imagen Docker
docker build -t planifika-users-api .

#  Ejecutar contenedor
docker run --env-file .env -p 8080:8080 planifika-users-api