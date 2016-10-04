# Bienvenido a la aplicación ToDo-List

## Funcionalidad registro

Para el registro he creado un form similar al form de Creación de usuario pero con una ruta nueva, que se puede acceder desde /registro
El siguiente método es el encargado del registro
```  registroNuevoUsuario() ```

Para controlar que los usuarios registrados no tengan el mismo login que otros usuarios, hago una llamada a la base de datos con
```Usuario usuarioData = UsuariosService.loginUsuario(usuario)```
  
loginUsuario(Usuario) se encarga de buscar en la BD si el usuario que se le pasa por parámetro existe, mediante la siguiente query
```
return (Usuario) JPA.em().createQuery("select u from Usuario u where u.login =" + "'" + usuario.login + "'").getSingleResult();
```
De esta forma podemos saber si el usuario existe o si existe pero no dispone de contraseña, que en este caso nuestra aplicación en vez de
mandar un error de que "El usuario ya existe", manda un mensaje de "El usuario ha modificado correctamente su contraseña".

Una vez registrados, la página nos redirige al login otra vez, para que volvamos a introducir los datos. 

## Funcionalidad login
El login tiene un diseño simple, con dos campos, uno para introducir el nick del usuario y otro para la contraseña
Una vez introducidos los datos correctamente si nos logueamos aparace la pagina de bienvenida.
Por otra parte si queremos registrarnos, existe el botón de Registrarse


![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")
