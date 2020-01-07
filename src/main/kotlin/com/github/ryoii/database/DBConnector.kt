package com.github.ryoii.database

import com.github.ryoii.model.Experiment
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

    fun queryExperiments(): List<Experiment> {
        val list = ArrayList<Experiment>()
        conn.createStatement().executeQuery("SELECT * FROM IMAGE").apply {
            while (next()) {
                list.add(
                    Experiment(
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

    fun update(experiment: Experiment) {
        conn.prepareStatement("UPDATE IMAGE SET name=?,image_name=?,command=?,description=? WHERE id=?").use {
            it.write(experiment).executeUpdate()
        }
    }

    fun update(experiments: List<Experiment>) {
        conn.prepareStatement("UPDATE IMAGE SET name=?,image_name=?,command=?,description=? WHERE id=?").use {
            experiments.forEach { experiment: Experiment -> it.write(experiment).addBatch() }
            it.executeBatch()
        }
    }

    private fun PreparedStatement.write(experiment: Experiment) =
        this.apply {
            setString(1, experiment.name)
            setString(2, experiment.imageName)
            setString(3, experiment.command)
            setString(4, experiment.description)
            setInt(5, experiment.id!!)
        }


    fun save(experiment: Experiment) {
        conn.prepareStatement("INSERT INTO IMAGE(name, image_name, command, description) VALUES (?,?,?,?)").use {
            it.setString(1, experiment.name)
            it.setString(2, experiment.imageName)
            it.setString(3, experiment.command)
            it.setString(4, experiment.description)
            it.execute()
        }
    }

    fun delete(experiment: Experiment) {
        conn.prepareStatement("DELETE FROM IMAGE WHERE id = ?").use {
            it.setInt(1, experiment.id!!)
            it.execute()
        }
    }
}