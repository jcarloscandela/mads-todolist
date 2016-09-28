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
        String mensaje = flash("guardaUsuario");
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
        flash("guardaUsuario", "El usuario se ha guardado correctamente");
        return redirect(controllers.routes.UsuariosController.listaUsuarios());
    }

    @Transactional
    public Result grabaUsuarioModificado() {
        return status(Http.Status.NOT_IMPLEMENTED);
    }

    @Transactional
    public Result detalleUsuario(String id) {
        return status(Http.Status.NOT_IMPLEMENTED);
    }

    @Transactional
    public Result editaUsuario(String id) {
        return status(Http.Status.NOT_IMPLEMENTED);
    }

    @Transactional
    public Result borraUsuario(String id) {
        return status(Http.Status.NOT_IMPLEMENTED);
    }

}
