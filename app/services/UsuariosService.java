package services;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import models.*;

public class UsuariosService {
    public static Usuario guardaUsuario(Usuario usuario) {
        return UsuarioDAO.create(usuario);
    }

    public static Usuario modificaUsuario(Usuario usuario) {
       Usuario existente = UsuarioDAO.findUsuarioPorLogin(usuario.login);
       if (existente != null && existente.id != usuario.id)
           throw new UsuariosException("Login ya existente: " + usuario.login);
       UsuarioDAO.update(usuario);
       return usuario;
     }

    public static Usuario findUsuario(Integer id) {
        return UsuarioDAO.find(id);
    }

    public static boolean deleteUsuario(Integer id) {
        if (UsuarioDAO.find(id) == null) {
            throw new UsuariosException("No existe usuario a borrar: " + id);
        }
        UsuarioDAO.delete(id);
        Logger.debug("Usuario con id: " + id + " ha sido borrado");
        return true;
    }


    public static List<Usuario> findAllUsuarios() {
        List<Usuario> lista = UsuarioDAO.findAll();
        //Logger.debug("Numero de usuarios: " + lista.size());
        return lista;
    }
    public static Usuario loginUsuario(Usuario usuario){
        return  UsuarioDAO.loginUsuario(usuario);
    }

    public static Usuario findUsuarioPorLogin(String login) {
        return UsuarioDAO.findUsuarioPorLogin(login);
    }

}
