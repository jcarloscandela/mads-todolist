package services;

import java.util.List;
import java.util.ArrayList;

import models.*;

public class TareasService {

    public static List<Tarea> listaTareasUsuario(Integer usuarioId) {
        Usuario usuario = UsuarioDAO.find(usuarioId);
        if (usuario != null) {
            return usuario.tareas;
        } else {
            throw new UsuariosException("Usuario no encontrado");
        }
    }

    public static Tarea grabaTarea(Tarea tarea){
     return TareaDAO.create(tarea);
   }

   public static Tarea findTarea(Integer id) {
     return TareaDAO.find(id);
  }
   public static Tarea modificaTarea(Tarea tarea) {
       return TareaDAO.update(tarea);
   }
   public static boolean deleteTarea(Integer id) {
         TareaDAO.delete(id);
         return true;
  }
}
