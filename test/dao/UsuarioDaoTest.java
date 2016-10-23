package dao;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.util.List;
import models.*;
import play.*;
import java.util.*;

public class UsuarioDaoTest {
    Database db;
    JPAApi jpa;

    @Before
    public void initDatabase() {
        db = Databases.inMemoryWith("jndiName", "DefaultDS");
        // Necesario para inicializar el nombre JNDI de la BD
        db.getConnection();
        // Se activa la compatibilidad MySQL en la BD H2
        db.withConnection(connection -> {
            connection.createStatement().execute("SET MODE MySQL;");
        });
        jpa = JPA.createFor("memoryPersistenceUnit");
    }

    @After
    public void shutdownDatabase() {
        db.withConnection(connection -> {
            connection.createStatement().execute("DROP TABLE Usuario;");
        });
        jpa.shutdown();
        db.shutdown();
    }

    @Test
    public void creaBuscaUsuario() {
        Integer id = jpa.withTransaction(() -> {
            Usuario nuevo = new Usuario("pepe", "pepe");
            nuevo = UsuarioDAO.create(nuevo);
            return nuevo.id;
        });

        jpa.withTransaction(() -> {
            Usuario usuario = UsuarioDAO.find(id);
            assertThat(usuario.login, equalTo("pepe"));
        });
    }

    @Test
    public void buscaUsuarioLogin() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuarioDAO.findUsuarioPorLogin("pepe");
            assertNull(usuario);
        });
    }

    @Test
    public void buscaUsuarioBorrado() {
      Integer id = jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          nuevo = UsuarioDAO.create(nuevo);
          return nuevo.id;
      });
      jpa.withTransaction(() -> {
        UsuarioDAO.delete(id);
      });

      jpa.withTransaction(() -> {
          Usuario usuario = UsuarioDAO.find(id);
          assertNull(usuario);
      });
    }

    @Test
    public void buscaUsuariosVacio() {
      List<Usuario> listaVacia = Collections.emptyList();

        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuarioDAO.findAll();
            assertEquals(usuarios, listaVacia );
        });
    }

    @Test
    public void buscaUsuariosLista() {
      jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          nuevo = UsuarioDAO.create(nuevo);
          Usuario nuevo1 = new Usuario("juan", "juan");
          nuevo1 = UsuarioDAO.create(nuevo);
          Usuario nuevo2 = new Usuario("francisco", "fran");
          nuevo2 = UsuarioDAO.create(nuevo);
      });
        jpa.withTransaction(() -> {
            List<Usuario> usuario = UsuarioDAO.findAll();
            assertNotNull(usuario);
        });
    }

    @Test
    public void cuentaUsuarios() {
      jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          nuevo = UsuarioDAO.create(nuevo);
          Usuario nuevo1 = new Usuario("juan", "juan");
          nuevo1 = UsuarioDAO.create(nuevo1);
          Usuario nuevo2 = new Usuario("francisco", "fran");
          nuevo2 = UsuarioDAO.create(nuevo2);
      });
        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuarioDAO.findAll();
            assertEquals("Numero de usuarios",usuarios.size(), 3);
        });
    }
}
