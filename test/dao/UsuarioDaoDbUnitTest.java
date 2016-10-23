package dao;

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
import play.*;
import java.util.*;
import models.*;

public class UsuarioDaoDbUnitTest {

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
        // Definimos como operación TearDown DELETE_ALL para que se
        // borren todos los datos de las tablas del dataset
        // (el valor por defecto DbUnit es DatabaseOperation.NONE)
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);

        // Definimos como operación SetUp CLEAN_INSERT, que hace un
        // DELETE_ALL de todas las tablase del dataset, seguido por un
        // INSERT. (http://dbunit.sourceforge.net/components.html)
        // Es lo que hace DbUnit por defecto, pero así queda más claro.
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
            Usuario usuario = UsuarioDAO.findUsuarioPorLogin("juan");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
            try {
                Date diezDiciembre93 = sdf.parse("10-12-1993");
                assertTrue(usuario.login.equals("juan") &&
                           usuario.fechaNacimiento.compareTo(diezDiciembre93) == 0);
            } catch (java.text.ParseException ex) {

            }
        });
    }

    @Test
    public void actualizaUsuario() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuarioDAO.find(2);
            usuario.apellidos = "Anabel Pérez";
            UsuarioDAO.update(usuario);
        });

        jpa.withTransaction(() -> {
            Usuario usuario = UsuarioDAO.find(2);
            assertThat(usuario.apellidos, equalTo("Anabel Pérez"));
        });
    }

    @Test
    public void borroUsuario() {
        jpa.withTransaction(() -> {
          List<Usuario> usuarios1 = UsuarioDAO.findAll();
          UsuarioDAO.delete(1);
        });

        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuarioDAO.findAll();
            assertEquals(usuarios.size(), 1);
        });
    }

    @Test
    public void creoUsuario() {
      jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          nuevo = UsuarioDAO.create(nuevo);
      });
        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuarioDAO.findAll();
            assertEquals("Numero de usuarios",usuarios.size(), 3);
        });
    }

    @Test
    public void creoUsuarioyborro() {
      jpa.withTransaction(() -> {
          Usuario nuevo = new Usuario("pepe", "pepe");
          nuevo = UsuarioDAO.create(nuevo);
          UsuarioDAO.delete(1);
      });
        jpa.withTransaction(() -> {
            List<Usuario> usuarios = UsuarioDAO.findAll();
            assertEquals("Numero de usuarios",usuarios.size(), 2);
        });
    }




}
