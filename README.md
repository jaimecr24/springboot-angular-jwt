# Ejemplo integración Spring Boot + Angular + jwt + MySql (docker)

## Creación del directorio de trabajo:

<p>Crear proyecto para java desde start.spring.io</p>
<p>Dentro del directorio crear proyecto de angular en 'client'<p>
<p>$ng new client</p>
<p>Abrir terminal de ubuntu y ejecutar los siguientes comandos:</p>
<p>$ cat client/.gitignore >> .gitignore</p>
<p>$ rm -rf client/node* client/src/favicon.ico client/.gitignore client/.git</p>
<p>$ cp -rf client/* .</p>
<p>$ cp client/.??* .</p>
<p>$ rm -rf client</p>
<p>$ sed -i -e 's,dist/client,target/classes/static,' angular.json</p>

## Creación de la base de datos con docker:

docker run --name testdb -e MYSQL_ROOT_PASSWORD=123456 -e TZ="Europe/Madrid" -p 33006:3306 -d mysql

<p>Desde dentro del terminal entrar en MySql:</p>
<p>#mysql -p123456</p>
<p>>create database testdb;</p>

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
