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


}
