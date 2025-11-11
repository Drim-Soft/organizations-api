#  Construir imagen Docker
docker build -t planifika-users-api .

#  Ejecutar contenedor
docker run --env-file .env -p 8082:8082 planifika-users-api