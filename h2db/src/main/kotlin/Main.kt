package org.example
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

fun main() {
   val url = "jdbc:h2:./DataBase/mydb"
   val usuario = "user"
   val contrasena = "password"
   try {
       Class.forName("org.h2.Driver")
       val conexion = DriverManager.getConnection(url, usuario, contrasena)
       println("Conexión exitosa")
       conexion.close()
   } catch (e: SQLException) {
       println("Error en la conexión: ${e.message}")
   } catch (e: ClassNotFoundException) {
       println("No se encontró el driver JDBC: ${e.message}")
   }

}