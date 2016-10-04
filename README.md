# Bienvenido a la aplicación ToDo-List

## Funcionalidad registro

Para el registro he creado un form similar al form de Creación de usuario pero con una ruta nueva, que se puede acceder desde /registro
El siguiente método es el encargado del registro
```  registroNuevoUsuario() ```

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14606401_10207131078055997_2875591396215274111_n.jpg?oh=ed1e844e0ebfbd7572f2125c15552ae7&oe=58AA54FB" height="300">

Para controlar que los usuarios registrados no tengan el mismo login que otros usuarios, hago una llamada a la base de datos con
``` Usuario usuarioData = UsuariosService.loginUsuario(usuario) ```
En caso de que se inserte el mismo usuario genera un error:

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14601099_10207131077615986_4774162199891009147_n.jpg?oh=91bfc4a80b9ded84947cdb9ec3f50ff9&oe=58A6D852" height="300">
  
loginUsuario(Usuario) se encarga de buscar en la BD si el usuario que se le pasa por parámetro existe, mediante la siguiente query
```
return (Usuario) JPA.em().createQuery("select u from Usuario u where u.login =" + "'" + usuario.login + "'").getSingleResult();
```
De esta forma podemos saber si el usuario existe o si existe pero no dispone de contraseña, que en este caso nuestra aplicación en vez de
mandar un error de que "El usuario ya existe", manda un mensaje de "El usuario ha modificado correctamente su contraseña".

Una vez registrados, la página nos redirige al login otra vez, para que volvamos a introducir los datos. 
### Código de registro

```java
  public Result registroNuevoUsuario() {

        Form<Usuario> usuarioForm = formFactory.form(Usuario.class).bindFromRequest();
        
        //Compruebo que no hay errores en el formulario
        if (usuarioForm.hasErrors()) {
            return badRequest(formRegistroUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }else{

          Usuario usuario = usuarioForm.get();
          Usuario usuarioData = UsuariosService.loginUsuario(usuario);
          
          //Si el login del usuario recogido de la base de datos no es null, es que existe dicho usuario ya
          if(usuarioData.login != null){
            //si no tiene contraseña, se puede elegir aquí
            if(usuarioData.password == null){
             
              usuario.id = usuarioData.id;
              usuario = UsuariosService.modificaUsuario(usuario);

              flash("mensaje", "El usuario se ha modificado correctamente su contraseña");
              return redirect(controllers.routes.UsuariosController.login());

            }else{
              return badRequest(formRegistroUsuario.render(usuarioForm, "El usuario ya existe"));
            }
          }
         
          //Cuando no exista el usuario, se creará
          usuario = UsuariosService.guardaUsuario(usuario);
          flash("mensaje", "El usuario se ha registrado correctamente");
          return redirect(controllers.routes.UsuariosController.login());


        }

    }

```
## Funcionalidad login
El login tiene un diseño simple, con dos campos, uno para introducir el nick del usuario y otro para la contraseña.
Para acceder al login tan sólo hace falta dirigirnos a la dirección /login

Una vez introducidos los datos correctamente si nos logueamos aparace la pagina de bienvenida con el nombre del logueado.

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14517609_10207131077415981_6796341952681934955_n.jpg?oh=a175267fb99b7aa66538fff4ad9ea2a8&oe=58A6E16B" height="300">

Si la contraseña es incorrecta nos lanzará el error:

<img src="https://scontent-mad1-1.xx.fbcdn.net/v/t1.0-9/14492409_10207131077895993_926492743987855108_n.jpg?oh=22b70cd79e1e9c16eb0ab010c0027ef4&oe=58622E90" height="300">

Por otra parte si queremos registrarnos, existe el botón de Registrarse

### Código de login
Autenticar
```java

  public Result autenticar(){
  		Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();

  		if (usuarioForm.hasErrors()){
  			return badRequest(login.render(usuarioForm, "Login erroneo"));
      }else{

        Usuario usuario = usuarioForm.get();
        Usuario usuarioData = UsuariosService.loginUsuario(usuario);

        //Si la informacion recuperada de la BD es null es que el usuario no existe
        if(usuarioData == null){
            return badRequest(login.render(usuarioForm,  "No existe el usuario"));
        }else{
          
          //aqui se autentica al administrador
          if(usuario.login.equals("admin") && usuario.password.equals("admin")){
            return redirect(controllers.routes.UsuariosController.listaUsuarios());
          }else{
            //En esta parte se comprueba que la contraseña introducida sea la misma que la de la base de datos
            if(!usuarioData.password.equals(usuario.password)){
              return badRequest(login.render(usuarioForm,  "La contraseña es incorrecta"));
            }else{
              		
                  flash("mensaje", usuarioData.nombre);
              		return redirect(controllers.routes.UsuariosController.bienvenida());
            }
          }
        }
  	}
  }

```
## Administración
Para acceder a la lista de usuarios, el usuario que acceda a la página debe ser admin, accediendo con una cuenta predeterminada (admin, admin). Si nos logueamos con esos datos, nos llevará directamente a la lista de usuarios (en un futuro se implementará un panel de control), en la lista de usuarios se puede borrar,modificar o crear usuarios. Para controlar que el que entra es admin lo he hecho mediante el siguiente código, aunque posteriormente se debería crear un subclase de usuarios para los administradores, moderadores, etc.
```
 if(usuario.login.equals("admin") && usuario.password.equals("admin")){
    return redirect(controllers.routes.UsuariosController.listaUsuarios());
 }
 ```
 

