package com.fiunam.databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Métodos base para las bases de datos de Alumnos y Materias
 */
public abstract class Database {
    protected static final String staticPathFiles = Path.of(".", "json").toString();
    protected final String pathFiles = Database.staticPathFiles;

    /**
     * Inicializa la base de datos, en cada subclase se especifica
     * el archivo del cual se obtendrán los datos.
     */
    protected abstract void initDB();

    /**
     * En caso de no existir un archivo, se crea uno con el nombre
     * especificado en cada clase
     */
    protected abstract void createDB();

    /**
     * Guarda los cambios de la base de datos, siempre debe
     * ejecutarse al finalizar el programa
     */
    public abstract void saveDB();


    /**
     * Retorna una impresión más detallada de cada elemento
     * con un formato más legible
     *
     * @return String con los datos
     */
    public abstract String printDB();

    /**
     * Restaura la base de datos.
     */
    public void reloadDB() {
        this.initDB();
    }

    /**
     * Crea la carpeta donde se guardarán los archivos json
     *
     * @throws FileNotFoundException En caso de que no se pueda crear, lanza
     *                               una excepción.
     */
    public static void createDir() throws Exception {
        final var mkdir = new File(Database.staticPathFiles).mkdir();
        if (!mkdir) {
            throw new Exception("No se pudo crear el directorio.");
        }
    }

    /**
     * Al imprimir los datos, se retornan con el formato de
     * printDB();
     *
     * @return String con los datos
     */
    public String toString() {
        return this.printDB();
    }

}
