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

            statement.executeUpdate(
                "CREATE TABLE Usuario (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nombre VARCHAR(255) NOT NULL," +
                        "email VARCHAR(255) UNIQUE" +
                        ");"
            )

            statement.executeUpdate(
                "CREATE TABLE Producto (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nombre VARCHAR(255) NOT NULL," +
                        "precio DECIMAL NOT NULL," +
                        "stock INT NOT NULL" +
                        ");"
            )

            statement.executeUpdate(
                "CREATE TABLE Pedido (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "precioTotal DECIMAL NOT NULL," +
                        "idUsuario INT," +
                        "FOREIGN KEY (idUsuario) REFERENCES Usuario(id)" +
                        ");"
            )

            statement.executeUpdate(
                "CREATE TABLE LineaPedido (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "cantidad INT NOT NULL," +
                        "precio DECIMAL NOT NULL," +
                        "idPedido INT," +
                        "idProducto INT," +
                        "FOREIGN KEY (idPedido) REFERENCES Pedido(id)," +
                        "FOREIGN KEY (idProducto) REFERENCES Producto(id)" +
                        ");"
            )

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
                statement.setInt(1, linea[0] as Int)
                statement.setInt(2, linea[1] as Int)
                statement.setInt(3, linea[2] as Int)
                statement.setDouble(4, linea[3] as Double)
                statement.executeUpdate()
            }
            println("Líneas de pedido insertadas")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun consultarLineasPedido(connection: Connection, idPedido: Int) {
    val sql = "SELECT * FROM LineaPedido WHERE idPedido = ?"

    try {
        connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, idPedido)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val cantidad = resultSet.getInt("cantidad")
                val precio = resultSet.getDouble("precio")
                val idProducto = resultSet.getInt("idProducto")

                println("ID: $id, Producto ID: $idProducto, Cantidad: $cantidad, Precio: $precio")
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun consultarSumaPedidosUsuario(connection: Connection, nombreUsuario: String) {
    val sql = """
        SELECT SUM(p.precioTotal) AS total
        FROM Pedido p
        JOIN Usuario u ON p.idUsuario = u.id
        WHERE u.nombre = ?
    """

    try {
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, nombreUsuario)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val total = resultSet.getDouble("total")
                println("Total de los pedidos de '$nombreUsuario': $total")
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun consultarUsuariosPorProducto(connection: Connection, nombreProducto: String) {
    val sql = """
        SELECT DISTINCT u.nombre
        FROM Usuario u
        JOIN Pedido p ON u.id = p.idUsuario
        JOIN LineaPedido lp ON p.id = lp.idPedido
        JOIN Producto pr ON lp.idProducto = pr.id
        WHERE pr.nombre = ?
    """

    try {
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, nombreProducto)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val nombreUsuario = resultSet.getString("nombre")
                println(nombreUsuario)
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun eliminarUsuario(connection: Connection, nombreUsuario: String) {
    val sql = "DELETE FROM Usuario WHERE nombre = ?"

    try {
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, nombreUsuario)
            val filasAfectadas = statement.executeUpdate()
            println("Usuarios eliminados: $filasAfectadas")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun eliminarProductoPrecio(connection: Connection, precioProducto: Double) {
    val sql = "DELETE FROM Producto WHERE precio = ?"

    try {
        connection.prepareStatement(sql).use { statement ->
            statement.setDouble(1, precioProducto)
            val filasAfectadas = statement.executeUpdate()
            println("Productos eliminados: $filasAfectadas")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun eliminarPedido(connection: Connection, idPedido: Int) {
    try {
        val sqlEliminarLineas = "DELETE FROM LineaPedido WHERE idPedido = ?"
        connection.prepareStatement(sqlEliminarLineas).use { statement ->
            statement.setInt(1, idPedido)
            val filasAfectadas = statement.executeUpdate()
            println("Líneas de pedido eliminadas: $filasAfectadas")
        }

        val sqlEliminarPedido = "DELETE FROM Pedido WHERE id = ?"
        connection.prepareStatement(sqlEliminarPedido).use { statement ->
            statement.setInt(1, idPedido)
            val filasAfectadas = statement.executeUpdate()
            println("Pedidos eliminados: $filasAfectadas")
        }

    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun main() {
    val url = "jdbc:h2:./DataBase/mydb"
    val usuario = "user"
    val contrasena = "password"

    try {
        DriverManager.getConnection(url, usuario, contrasena).use { connection ->
            crearTablas(connection)
            insertUsuarios(connection)
            insertProductos(connection)
            insertPedidos(connection)
            insertLinea(connection)

            consultarLineasPedido(connection, 1)
            consultarSumaPedidosUsuario(connection, "Ataulfo Rodríguez")
            consultarUsuariosPorProducto(connection, "Abanico")

            eliminarUsuario(connection, "Cornelio Ramírez")
            eliminarProductoPrecio(connection, 24.99)
            eliminarPedido(connection, 3)
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}
