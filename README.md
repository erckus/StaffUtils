# StaffUtils

Plugin creado por Poetty si tienes una duda contacta a mi discord (poettyy)

Este plugin solo es compatible en bungeecord para todas las versiones de 1.7x hasta 1.20.1
Sirve para calcular el tiempo de cada usuario que lleva dentro del servidor, la base de datos se crea una carpeta de "user-data" y ahi se almacenan.

Próximamente se añadira MySQL donde podréis elegir si entre el default o mysql y más actualizaciones básicas.

Este plugin es totalmente gratuito

Variables en config.yml:

 - %player% - Mostrar el nombre del usuario.
 - %time% - Calcular el tiempo por horas.

Permisos para usar los comandos:
 - staffutils.admin: Uso de /tiempo <usuario>
 - staffutils.resetall: Para restablecer todos los datos de "user-data"
 - staffutils.reset: Para restablecer solo 1 usuario de los datos de "user-data"

Comandos:
 - /tiempo <usuario> - Calcula el tiempo que lleva jugando en el servidor.
 - /tiempo resetall - Restablece a todos los usuarios.
 - /tiempo reset <usuario> - Restablece solo 1 a un usuario la data.
 - /tiempo reloadconfig - Restablecer la config
