package org.example
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

fun crearTablas(connection: Connection) {
    try {
        connection.createStatement().use { statement ->
            statement.executeUpdate("DROP TABLE IF EXISTS LineaPedido")
            statement.executeUpdate("DROP TABLE IF EXISTS Pedido")
            statement.executeUpdate("DROP TABLE IF EXISTS Producto")
            statement.executeUpdate("DROP TABLE IF EXISTS Usuario")
            println("Tablas borradas")

            statement.executeUpdate("""
                CREATE TABLE Usuario (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(255) NOT NULL,
                    email VARCHAR(255) UNIQUE
                );
            """.trimIndent())

            statement.executeUpdate("""
                CREATE TABLE Producto (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(255) NOT NULL,
                    precio DECIMAL NOT NULL,
                    stock INT NOT NULL
                );
            """.trimIndent())

            statement.executeUpdate("""
                CREATE TABLE Pedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    precioTotal DECIMAL NOT NULL,
                    idUsuario INT,
                    FOREIGN KEY (idUsuario) REFERENCES Usuario(id)
                );
            """.trimIndent())

            statement.executeUpdate("""
                CREATE TABLE LineaPedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    cantidad INT NOT NULL,
                    precio DECIMAL NOT NULL,
                    idPedido INT,
                    idProducto INT,
                    FOREIGN KEY (idPedido) REFERENCES Pedido(id),
                    FOREIGN KEY (idProducto) REFERENCES Producto(id)
                );
            """.trimIndent())

            println("Tablas creadas")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun insertUsuarios(connection: Connection) {
    val sql = "INSERT INTO Usuario (nombre, email) VALUES (?, ?)"

    try {
        connection.prepareStatement(sql).use { statement ->
            val usuarios = listOf(
                "Facundo Pérez" to "facuper@mail.com",
                "Ataulfo Rodríguez" to "ataurod@mail.com",
                "Cornelio Ramírez" to "Cornram@mail.com"
            )

            for ((nombre, email) in usuarios) {
                statement.setString(1, nombre)
                statement.setString(2, email)
                statement.executeUpdate()
            }
            println("Usuarios insertados")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun insertProductos(connection: Connection) {
    val sql = "INSERT INTO Producto (nombre, precio, stock) VALUES (?, ?, ?)"

    try {
        connection.prepareStatement(sql).use { statement ->
            val productos = listOf(
                Triple("Ventilador", 10.0, 2),
                Triple("Abanico", 150.0, 47),
                Triple("Estufa", 24.99, 1)
            )

            for ((nombre, precio, stock) in productos) {
                statement.setString(1, nombre)
                statement.setDouble(2, precio)
                statement.setInt(3, stock)
                statement.executeUpdate()
            }
            println("Productos insertados")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun insertPedidos(connection: Connection) {
    val sql = "INSERT INTO Pedido (precioTotal, idUsuario) VALUES (?, ?)"

    try {
        connection.prepareStatement(sql).use { statement ->
            val pedidos = listOf(
                160.0 to 2,
                20.0 to 1,
                150.0 to 2
            )

            for ((precioTotal, idUsuario) in pedidos) {
                statement.setDouble(1, precioTotal)
                statement.setInt(2, idUsuario)
                statement.executeUpdate()
            }
            println("Pedidos insertados")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun insertLinea(connection: Connection) {
    val sql = "INSERT INTO LineaPedido (idPedido, idProducto, cantidad, precio) VALUES (?, ?, ?, ?)"

    try {
        connection.prepareStatement(sql).use { statement ->
            val lineasPedido = listOf(
                listOf(1, 1, 1, 10.0),
                listOf(1, 2, 1, 150.0),
                listOf(2, 1, 2, 20.0),
                listOf(3, 2, 1, 150.0)
            )

            for (linea in lineasPedido) {
                statement.setInt(1, (linea[0] as Int))
                statement.setInt(2, (linea[1] as Int))
                statement.setInt(3, (linea[2] as Int))
                statement.setDouble(4, (linea[3] as Double))
                statement.executeUpdate()
            }
            println("Líneas de pedido insertadas")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun main() {
   val url = "jdbc:h2:./DataBase/mydb"
   val usuario = "user"
   val contrasena = "password"
//   try {
//       Class.forName("org.h2.Driver")
//       val conexion = DriverManager.getConnection(url, usuario, contrasena)
//       println("Conexión exitosa")
//       conexion.close()
//   } catch (e: SQLException) {
//       println("Error en la conexión: ${e.message}")
//   } catch (e: ClassNotFoundException) {
//       println("No se encontró el driver JDBC: ${e.message}")
//   }

    try {
        DriverManager.getConnection(url, usuario, contrasena).use { connection ->
            crearTablas(connection)
            insertUsuarios(connection)
            insertProductos(connection)
            insertPedidos(connection)
            insertLinea(connection)
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}
