package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;
import services.*;
import models.*;

public class Tareas extends Controller {

    /*
    *  CRUD de tareas
    *
    */
    @Transactional(readOnly = true)

    // Devuelve una página con la lista de tareas
    public Result listaTareas(Integer usuarioId) {
        List<Tarea> tareas = TareasService.listaTareasUsuario(usuarioId);
        String mensaje = flash("grabaTarea");
        return ok(listaTareas.render(tareas,usuarioId,mensaje));
    }

    //create
    public Result formularioNuevaTarea(Integer usuarioId){
        return ok(formCreacionTarea.render(Form.form(Tarea.class),usuarioId,""));
    }

    @Transactional
    public Result grabaNuevaTarea(Integer usuarioId){
        Form<Tarea> tareaForm = Form.form(Tarea.class).bindFromRequest();
        if (tareaForm.hasErrors())
            return badRequest(formCreacionTarea.render(tareaForm,usuarioId, "Hay errores en el formulario"));
        Tarea tarea = tareaForm.get();
        tarea.usuario = UsuariosService.findUsuario(usuarioId);
        tarea = TareasService.grabaTarea(tarea);
        flash("creaTarea", "El usuario se ha grabado correctamente");
        return redirect(controllers.routes.Tareas.listaTareas(usuarioId));
    }



}
