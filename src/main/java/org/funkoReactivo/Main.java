package org.funkoReactivo;

import org.funkoReactivo.services.files.CsvManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String path = Paths.get("").toAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data";
        String file_path = path + File.separator + "funkos.csv";

        CsvManager csvManager = new CsvManager();
        csvManager.readCsv(file_path).subscribe(
                funko -> System.out.println(funko)
        );
    }
}