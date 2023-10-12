package org.funkoReactivo;

import org.funkoReactivo.enums.Modelo;
import org.funkoReactivo.models.Funko;
import org.funkoReactivo.repositories.funko.FunkoRepositoryImpl;
import org.funkoReactivo.services.database.DataBaseManager;
import org.funkoReactivo.services.files.CsvManager;
import org.funkoReactivo.services.funkos.FunkoCacheImpl;
import org.funkoReactivo.services.funkos.FunkoServiceImpl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        /*String path = Paths.get("").toAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data";
        String file_path = path + File.separator + "funkos.csv";

        CsvManager csvManager = new CsvManager();
        csvManager.readCsv(file_path).subscribe(
                funko -> System.out.println(funko)
        );*/

        DataBaseManager db = DataBaseManager.getInstance();
        FunkoRepositoryImpl repository = FunkoRepositoryImpl.getInstance(db);
        FunkoServiceImpl service = FunkoServiceImpl.getInstance(repository, new FunkoCacheImpl(15, 1, 1, TimeUnit.MINUTES));

        System.out.println("FIND ALL DE FUNKOS");
        service.findAll().subscribe(
                funko -> System.out.println(funko)
        );

        service.stopCleaner();

    }
}