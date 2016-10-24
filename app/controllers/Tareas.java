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

    @Transactional
    public Result editarTarea(Integer id, Integer idT){
        Tarea tarea = TareasService.findTarea(idT);
        Form<Tarea> formulario = Form.form(Tarea.class);
        Form<Tarea> tareaForm = formulario.fill(tarea);
        return ok(formModificacionTarea.render(tareaForm,id,""));
    }

    @Transactional
    public Result grabaTareaModificada(Integer id){
        Form<Tarea> tareaForm = Form.form(Tarea.class).bindFromRequest();
        if (tareaForm.hasErrors())
            return badRequest(formModificacionTarea.render(tareaForm,id, "Hay errores en el formulario"));
        Tarea tarea = tareaForm.get();
        tarea = TareasService.modificaTarea(tarea);
        tarea.usuario = UsuariosService.findUsuario(id);
        flash("grabaTareaModificada", "La tarea se ha grabado correctamente");
        return redirect(controllers.routes.Tareas.listaTareas(id));
    }

    @Transactional
    public Result borraTarea(Integer id, Integer idT){
        if (TareasService.deleteTarea(idT))
              return redirect(controllers.routes.Tareas.listaTareas(id));
        else
            return notFound();
    }

}
