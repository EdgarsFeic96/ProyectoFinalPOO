package com.fiunam.databases;

import com.fiunam.logger.Logger;
import com.fiunam.materias.AdminMateria;
import com.fiunam.materias.Materia;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Crea y maneja la información de las materias
 */
public class DatabaseMaterias extends Database {
    private final Logger log = new Logger(DatabaseMaterias.class);
    private ArrayList<Materia> materias;
    private final String pathMateriasDB = Paths.get(super.pathFiles, "materias.json").toString();
    private int idMaterias;

    /**
     * Constructor principal de la lista de materias, inicia lo necesario para
     * manejar la lista.
     */
    public DatabaseMaterias() {
        this.materias = new ArrayList<>();
        this.initDB();
    }

    /**
     * Crea una copia de las materias, para no modificar la lista principal en algunas acciones,
     * como el filtrado de materias.
     * @return Copia de la lista de materias
     */
    public ArrayList<Materia> getCopiaMaterias() {
        return new ArrayList<>(this.materias);
    }

    public ArrayList<Materia> getCopiaMaterias(String key){
        ArrayList<Materia> materiasFiltradas = new ArrayList<>();
        for (Materia materia: this.materias){
            if (Objects.equals(materia.getArea(), key)){
                materiasFiltradas.add(materia);
            }
        }
        return materiasFiltradas;
    }

    @Override
    protected void initDB() {
        JSONDeserializer<ArrayList<Materia>> jsonDeserializer = new JSONDeserializer<>();

        try (FileReader file = new FileReader(this.pathMateriasDB, StandardCharsets.UTF_8)) {
            this.materias = jsonDeserializer.deserialize(file);
            try{
                this.idMaterias = Integer.parseInt(materias.get(materias.size()-1).getIdMateria());
            } catch (Exception e){
                this.idMaterias = 0;
                log.sendWarning("(%s) Listado de materias vacio, empezando en ID 0000.".formatted(e));
            }
        } catch (FileNotFoundException fe) {
            log.sendWarning("La base de datos \"MATERIAS\" no existe, esperando datos para crear una nueva.");
            this.createDB();
        } catch (Exception e) {
            log.sendError(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected void createDB() {
        try {
            Database.createDir();
            File file = new File(this.pathMateriasDB);
            if (!file.createNewFile()) throw new Exception("Error al crear el archivo " + this.pathMateriasDB);
        } catch (Exception e) {
            log.sendError(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void saveDB() {
        try (FileWriter file = new FileWriter(this.pathMateriasDB, StandardCharsets.UTF_8)) {
            JSONSerializer serializer = new JSONSerializer();

            file.write(serializer.prettyPrint(true).include("alumnos").serialize(this.materias));

        } catch (Exception e) {
            log.sendError(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String printDB() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.materias.size(); i++) {
            sb.append("[").append(i).append("] ");
            sb.append(this.materias.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Agrega un alumno a la base de datos
     * @param materia Objeto de una materia
     */
    public void agregarMateria(Materia materia) {
        materia.setIdMateria(String.valueOf(++this.idMaterias));
        this.materias.add(materia);
        log.sendInfo("Materia " + materia.getNombre() + " (" + materia.getIdMateria() + ") agregada.");
    }

    /**
     * Busca una materia en la lista a partir de su ID
     * @param idMateria ID de la materia
     * @return La materia si se encuentra, en caso contrario, una materia
     * con sus atributos nulos
     */
    public Materia readMateria(String idMateria) {
        for (Materia materia : this.materias) {
            if (Objects.equals(materia.getIdMateria(), idMateria)) {
                return materia;
            }
        }
        return new Materia();
    }

    /**
     * Da de baja la materia de todos los alumnos que estaban inscritos, y luego se elimina
     * @param dbAlumnos Base de datos de los alumnos.
     * @param idMateria ID de la materia
     */
    public void eliminarMateria(DatabaseAlumnos dbAlumnos, String idMateria) {
        for (int i = 0; i < this.materias.size(); i++) {
            if (Objects.equals(this.materias.get(i).getIdMateria(), idMateria)) {
                Materia materia = this.materias.get(i);

                while (materia.getAlumnos().size() > 0) {
                    AdminMateria.bajaMateria(this, dbAlumnos, materia.getIdMateria(), materia.getAlumnos().get(0));
                }

                this.materias.remove(i);
                log.sendInfo("Materia " + materia.getNombre() + " (" + materia.getIdMateria() + ") eliminada.");
                return;
            }
        }
        log.sendWarning("La materia con id \"" + idMateria + "\" no existe.");
    }
}
