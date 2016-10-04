# Bienvenido a la aplicación ToDo-List

## Funcionalidad registro

Para el registro he creado un form similar al form de Creación de usuario pero con una ruta nueva, que se puede acceder desde /registro
El siguiente método es el encargado del registro
```  registroNuevoUsuario() ```

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14606401_10207131078055997_2875591396215274111_n.jpg?oh=ed1e844e0ebfbd7572f2125c15552ae7&oe=58AA54FB" height="300">

Para controlar que los usuarios registrados no tengan el mismo login que otros usuarios, hago una llamada a la base de datos con
```Usuario usuarioData = UsuariosService.loginUsuario(usuario)```
En caso de que se inserte el mismo usuario genera un error:

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14601099_10207131077615986_4774162199891009147_n.jpg?oh=91bfc4a80b9ded84947cdb9ec3f50ff9&oe=58A6D852" height="300">
  
loginUsuario(Usuario) se encarga de buscar en la BD si el usuario que se le pasa por parámetro existe, mediante la siguiente query
```
return (Usuario) JPA.em().createQuery("select u from Usuario u where u.login =" + "'" + usuario.login + "'").getSingleResult();
```
De esta forma podemos saber si el usuario existe o si existe pero no dispone de contraseña, que en este caso nuestra aplicación en vez de
mandar un error de que "El usuario ya existe", manda un mensaje de "El usuario ha modificado correctamente su contraseña".

Una vez registrados, la página nos redirige al login otra vez, para que volvamos a introducir los datos. 

## Funcionalidad login
El login tiene un diseño simple, con dos campos, uno para introducir el nick del usuario y otro para la contraseña

Una vez introducidos los datos correctamente si nos logueamos aparace la pagina de bienvenida con el nombre del logueado.

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14517609_10207131077415981_6796341952681934955_n.jpg?oh=a175267fb99b7aa66538fff4ad9ea2a8&oe=58A6E16B" height="300">

Si la contraseña es incorrecta nos lanzará el error:

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14492409_10207131077895993_926492743987855108_n.jpg?oh=22b70cd79e1e9c16eb0ab010c0027ef4&oe=58622E90" height="300">

Por otra parte si queremos registrarnos, existe el botón de Registrarse



