
package services;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;
import org.junit.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import play.*;
import models.*;
import services.*;
import java.util.*;

public class UsuariosServiceTest {

    static Database db;
    static JPAApi jpa;
    JndiDatabaseTester databaseTester;

    @BeforeClass
    static public void initDatabase() {
        db = Databases.inMemoryWith("jndiName", "DefaultDS");
        // Necesario para inicializar el nombre JNDI de la BD
        db.getConnection();
        // Se activa la compatibilidad MySQL en la BD H2
        db.withConnection(connection -> {
            connection.createStatement().execute("SET MODE MySQL;");
        });
        jpa = JPA.createFor("memoryPersistenceUnit");
    }

    @Before
    public void initData() throws Exception {
        databaseTester = new JndiDatabaseTester("DefaultDS");
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new
        FileInputStream("test/resources/usuarios_dataset.xml"));
        databaseTester.setDataSet(initialDataSet);
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

        databaseTester.onSetup();
    }

    @After
    public void clearData() throws Exception {
        databaseTester.onTearDown();
    }

    @AfterClass
    static public void shutdownDatabase() {
        jpa.shutdown();
        db.shutdown();
    }

    @Test
    public void findUsuarioPorLogin() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuarioPorLogin("juan");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
            try {
                Date diezDiciembre93 = sdf.parse("10-12-1993");
                assertTrue(usuario.login.equals("juan") &&
                           usuario.fechaNacimiento.compareTo(diezDiciembre93) == 0);
            } catch (java.text.ParseException ex) {
                fail("Excepción ParseException");
            }
        });
    }

    @Test
    public void actualizaUsuario() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);
            usuario.apellidos = "Anabel Pérez";
            UsuariosService.modificaUsuario(usuario);
        });

        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);
            assertThat(usuario.apellidos, equalTo("Anabel Pérez"));
        });
    }

    @Test
    public void actualizaUsuarioLanzaExcepcionSiLoginYaExiste() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);

            Usuario desconectado = usuario.copy();
            desconectado.login = "juan";

            try {
                UsuariosService.modificaUsuario(desconectado);
                fail("No se ha lanzado excepción login ya existe");
            } catch (UsuariosException ex) {
            }
        });
    }

    @Test
    public void cuentaUsuarios() {
      jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          UsuariosService.guardaUsuario(nuevo);

      });
        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuariosService.findAllUsuarios();
            Logger.debug("Numero de usuarios: " + usuarios.size());
            assertEquals("Numero de usuarios",usuarios.size(), 3);
        });
    }

    @Test
    public void buscaUsuariosVacio() {
      List<Usuario> listaVacia = Collections.emptyList();

        jpa.withTransaction(() -> {
            UsuariosService.deleteUsuario(1);
            UsuariosService.deleteUsuario(2);
            List<Usuario> usuarios = UsuariosService.findAllUsuarios();
            assertEquals(usuarios, listaVacia);
        });
    }

}
