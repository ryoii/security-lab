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
        val sql = "CREATE TABLE IF NOT EXISTS EXPERIMENT (" +
                "id             INTEGER PRIMARY KEY NOT NULL," +
                "exp_name       VARCHAR(255) NOT NULL," +
                "image_name     VARCHAR(255) NOT NULL," +
                "container_name VARCHAR(255) NOT NULL," +
                "port_map       VARCHAR(255) NOT NULL," +
                "mount          VARCHAR(255) NOT NULL," +
                "homepage       VARCHAR(255) NOT NULL," +
                "description    TEXT NOT NULL)"
        conn.createStatement().use {
            it.executeUpdate(sql)
        }
    }

    fun queryExperiments(): List<Experiment> {
        val list = ArrayList<Experiment>()
        conn.createStatement().executeQuery("SELECT * FROM EXPERIMENT").apply {
            while (next()) {
                list.add(
                    Experiment(
                        id = getInt("id"),
                        name = getString("exp_name"),
                        imageName = getString("image_name"),
                        containerName = getString("container_name"),
                        description = getString("description"),
                        portMap = getString("port_map"),
                        mount = getString("mount"),
                        homepage = getString("homepage")
                    )
                )
            }
        }
        return list
    }

    fun update(experiment: Experiment) {
        conn.prepareStatement("UPDATE EXPERIMENT SET exp_name=?,image_name=?,container_name=?,description=?,port_map=?,mount=?,homepage=? WHERE id=?")
            .use {
                it.write(experiment).writeId(experiment, 8).executeUpdate()
            }
    }

    fun update(experiments: List<Experiment>) {
        conn.prepareStatement("UPDATE EXPERIMENT SET exp_name=?,image_name=?,container_name=?,description=?,port_map=?,mount=?,homepage=? WHERE id=?")
            .use {
                experiments.forEach { experiment: Experiment -> it.write(experiment).writeId(experiment, 8).addBatch() }
                it.executeBatch()
            }
    }

    private fun PreparedStatement.write(experiment: Experiment) =
        this.apply {
            setString(1, experiment.name)
            setString(2, experiment.imageName)
            setString(3, experiment.containerName)
            setString(4, experiment.description)
            setString(5, experiment.portMap)
            setString(6, experiment.mount)
            setString(7, experiment.homepage)
        }

    private fun PreparedStatement.writeId(experiment: Experiment, index: Int) =
        this.apply { setInt(index, experiment.id!!) }

    fun save(experiment: Experiment) {
        conn.prepareStatement("INSERT INTO EXPERIMENT(exp_name,image_name,container_name,description,port_map,mount,homepage) VALUES (?,?,?,?,?,?,?)")
            .use { it.write(experiment).execute() }
    }

    fun delete(experiment: Experiment) {
        conn.prepareStatement("DELETE FROM EXPERIMENT WHERE id = ?")
            .use { it.writeId(experiment, 1).execute() }
    }
}