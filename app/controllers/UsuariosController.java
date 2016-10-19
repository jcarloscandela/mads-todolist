package controllers;

import java.util.List;
import javax.inject.*;

import play.*;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.*;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.*;

import services.*;
import models.*;

public class UsuariosController extends Controller {

    @Inject FormFactory formFactory;

    @Transactional(readOnly = true)
    // Devuelve una lista con todos los usuarios
    public Result listaUsuarios() {

        //Esto es una especie de cookie que cuando se crea un usuario
        //al volver a la lista de usuario, muestra el mensaje
        Logger.debug("Muestro la lista de usuarios");
        String mensaje = flash("mensaje");

        List<Usuario> usuarios = UsuariosService.findAllUsuarios();
        return ok(listaUsuarios.render(usuarios, mensaje));
    }

    // Devuelve un formulario para crear un nuevo usuario
    public Result formularioNuevoUsuario() {
        return ok(formCreacionUsuario.render(formFactory.form(Usuario.class),""));
    }

    @Transactional
    // Añade un nuevo usuario en la BD y devuelve código HTTP
    // de redirección a la página de listado de usuarios
    public Result guardaNuevoUsuario() {

        Form<Usuario> usuarioForm = formFactory.form(Usuario.class).bindFromRequest();
        if (usuarioForm.hasErrors()) {
            return badRequest(formCreacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }else{

          Usuario usuario = usuarioForm.get();

          Usuario usuarioData = UsuariosService.loginUsuario(usuario);//nuevo
          if(usuarioData.login != null){
              return badRequest(formCreacionUsuario.render(usuarioForm, "El usuario ya existe"));
          }

          Logger.debug("Usuario a guardar: " + usuario.toString());
          usuario = UsuariosService.guardaUsuario(usuario);
          flash("mensaje", "El usuario se ha guardado correctamente");
          return redirect(controllers.routes.UsuariosController.listaUsuarios());
        }

    }

    @Transactional
    public Result grabaUsuarioModificado() {

        Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
    		if (usuarioForm.hasErrors()){
    			return badRequest(formModificacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }
    		Usuario usuario = usuarioForm.get();
        Logger.debug("Usuario a modificar: " + usuario.toString());
        //Controlar que no se ponga un mismo login al guardar

    		usuario = UsuariosService.modificaUsuario(usuario);
    		flash("mensaje", "El usuario se ha modificado correctamente");
        return redirect(controllers.routes.UsuariosController.listaUsuarios());
    }

    @Transactional
    public Result detalleUsuario(Integer id) {
      Usuario usuario = UsuariosService.findUsuario(id);
          if(usuario != null){
            return ok(formDetalleUsuario.render(usuario));
          }else{
            return notFound(String.format("El usuario con id: " + id + ", no existe"));
          }
    }

    @Transactional
    public Result editaUsuario(Integer id) {
        Usuario usuario = UsuariosService.findUsuario(id);
            if(usuario != null){
              Form<Usuario> formulario = Form.form(Usuario.class);
              Form<Usuario> usuarioForm = formulario.fill(usuario);

              return ok(formModificacionUsuario.render(usuarioForm,""));
            }else{
              return notFound(String.format("El usuario con id: " + id + ", no existe y no se puede modificar"));
            }
    }

    @Transactional
    public Result borraUsuario(Integer id) {
        //Logger.debug("Intento de borrar xd");
        if (UsuariosService.deleteUsuario(id)){
          return ok();//redirect(controllers.routes.UsuariosController.listaUsuarios());

        }else{
          return notFound(String.format("El usuario con id: " + id + ", no existe y no se puede borrar"));
        }
    }

    @Transactional
    public Result login() {
      return ok(login.render(Form.form(Usuario.class),""));
    }

    @Transactional
    public Result autenticar(){
  		Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();

  		if (usuarioForm.hasErrors()){
  			return badRequest(login.render(usuarioForm, "Login erroneo"));
      }else{

        Usuario usuario = usuarioForm.get();
        Usuario usuarioData = UsuariosService.loginUsuario(usuario);
        Logger.debug("Autenticación de: " +  usuario.toString());

        if(usuarioData == null){
            return badRequest(login.render(usuarioForm,  "No existe el usuario"));
        }else{

          if(usuario.login.equals("admin") && usuario.password.equals("admin")){
            return redirect(controllers.routes.UsuariosController.listaUsuarios());
          }else{
            if(!usuarioData.password.equals(usuario.password)){
              return badRequest(login.render(usuarioForm,  "La contraseña es incorrecta"));
            }else{
              		//flash("autenticar", "El usuario se ha autenticado correctamente");
                  flash("mensaje", usuarioData.nombre);
              		return redirect(controllers.routes.UsuariosController.bienvenida());
            }
          }
        }
  	}
  }

    @Transactional
    public Result bienvenida(){
        String mensaje = flash("mensaje");
      return ok(bienvenida.render(mensaje));
    }

    @Transactional
    public Result formularioRegistroUsuario() {
        return ok(formRegistroUsuario.render(formFactory.form(Usuario.class),""));
    }

    @Transactional
    public Result registroNuevoUsuario() {

        Form<Usuario> usuarioForm = formFactory.form(Usuario.class).bindFromRequest();
        if (usuarioForm.hasErrors()) {
            return badRequest(formRegistroUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }else{

          Usuario usuario = usuarioForm.get();
          Usuario usuarioData = UsuariosService.loginUsuario(usuario);

          if(usuarioData.login != null){
            if(usuarioData.password == null){
              Logger.debug("La password es null");
              usuario.id = usuarioData.id;
              usuario = UsuariosService.modificaUsuario(usuario);

              flash("mensaje", "El usuario se ha modificado correctamente su contraseña");
              return redirect(controllers.routes.UsuariosController.login());

            }else{
              return badRequest(formRegistroUsuario.render(usuarioForm, "El usuario ya existe"));
            }
          }

          Logger.debug("Usuario a guardar: " + usuario.toString());
          usuario = UsuariosService.guardaUsuario(usuario);
          flash("mensaje", "El usuario se ha registrado correctamente");
          return redirect(controllers.routes.UsuariosController.login());


        }

    }



}
