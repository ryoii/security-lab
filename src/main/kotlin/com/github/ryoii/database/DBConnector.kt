package com.github.ryoii.database

import com.github.ryoii.model.Image
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

object SQLiteConnector {

    private val conn: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        conn = DriverManager.getConnection("jdbc:sqlite:sec.db")
        createTable()
    }

    private fun createTable() {
        val sql = "CREATE TABLE IF NOT EXISTS IMAGE (" +
                "id          INTEGER PRIMARY KEY NOT NULL," +
                "name        VARCHAR(255) NOT NULL," +
                "image_name  VARCHAR(255) NOT NULL," +
                "command     VARCHAR(255) NOT NULL," +
                "description TEXT NOT NULL)"
        conn.createStatement().use {
            it.executeUpdate(sql)
        }
    }

    fun queryImages(): List<Image> {
        val list = ArrayList<Image>()
        conn.createStatement().executeQuery("SELECT * FROM IMAGE").apply {
            while (next()) {
                list.add(
                    Image(
                        id = getInt("id"),
                        name = getString("name"),
                        imageName = getString("image_name"),
                        command = getString("command"),
                        description = getString("description")
                    )
                )
            }
        }
        return list
    }

    fun update(image: Image) {
        conn.prepareStatement("UPDATE IMAGE SET name=?,image_name=?,command=?,description=? WHERE id=?").use {
            it.write(image).executeUpdate()
        }
    }

    fun update(images: List<Image>) {
        conn.prepareStatement("UPDATE IMAGE SET name=?,image_name=?,command=?,description=? WHERE id=?").use {
            images.forEach { image: Image -> it.write(image).addBatch() }
            it.executeBatch()
        }
    }

    private fun PreparedStatement.write(image: Image) =
        this.apply {
            setString(1, image.name)
            setString(2, image.imageName)
            setString(3, image.command)
            setString(4, image.description)
            setInt(5, image.id!!)
        }


    fun save(image: Image) {
        conn.prepareStatement("INSERT INTO IMAGE(name, image_name, command, description) VALUES (?,?,?,?)").use {
            it.setString(1, image.name)
            it.setString(2, image.imageName)
            it.setString(3, image.command)
            it.setString(4, image.description)
            it.execute()
        }
    }

    fun delete(image: Image) {
        conn.prepareStatement("DELETE FROM IMAGE WHERE id = ?").use {
            it.setInt(1, image.id!!)
            it.execute()
        }
    }
}