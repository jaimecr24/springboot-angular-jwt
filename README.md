# Ejemplo integración Spring Boot + Angular + jwt + MySql (docker)

## Creación de la base de datos con docker:

docker run --name testdb -e MYSQL_ROOT_PASSWORD=123456 -e TZ="Europe/Madrid" -p 33006:3306 -d mysql

<p>Desde dentro del terminal entrar en MySql:</p>
<p>#mysql -p123456</p>
<p>>create database dbtest;</p>

## Datos para las pruebas:
Usuarios: user, mod, admin

Emails: user@gmail.com, mod@gmail.com, admin@gmail.com

Password: 12345678 (el mismo para todos)

## Ejecutar back

mvn java:exec

## Ejecutar front

ng serve

### Ejemplo creado a partir de https://www.bezkoder.com/spring-boot-login-example-mysql/
### añadiendo refresh token con rotación
