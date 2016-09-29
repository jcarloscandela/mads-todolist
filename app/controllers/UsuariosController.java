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
        }
        Usuario usuario = usuarioForm.get();
        Logger.debug("Usuario a guardar: " + usuario.toString());
        usuario = UsuariosService.guardaUsuario(usuario);
        flash("mensaje", "El usuario se ha guardado correctamente");
        return redirect(controllers.routes.UsuariosController.listaUsuarios());
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
    public Result detalleUsuario(String id) {
      Usuario usuario = UsuariosService.findUsuario(id);
          if(usuario != null){
            return ok(formDetalleUsuario.render(usuario));
          }else{
            return notFound(String.format("El usuario con id: " + id + ", no existe"));
          }
    }

    @Transactional
    public Result editaUsuario(String id) {
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
    public Result borraUsuario(String id) {
        return status(Http.Status.NOT_IMPLEMENTED);
    }

}
