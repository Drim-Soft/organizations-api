#  Construir imagen Docker
docker build -t planifika-users-api .

#  Ejecutar contenedor
docker run --env-file .env -p 8080:8080 planifika-users-api

ejecutar sonar:
curl http://localhost:9000/api/authentication/validate -u "sqp_9f85b7048ee91ca34aedeb13af96735b563f90d6:"

FUNCIONO ASÍ EN GIT BASH:
$ mvn clean verify sonar:sonar \
  -Dsonar.projectKey=organizationsapi \
  -Dsonar.projectName=organizationsapi \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_9f85b7048ee91ca34aedeb13af96735b563f90d6