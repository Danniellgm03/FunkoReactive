package org.funkoReactivo;

import org.funkoReactivo.enums.Modelo;
import org.funkoReactivo.models.Funko;
import org.funkoReactivo.repositories.funko.FunkoRepositoryImpl;
import org.funkoReactivo.services.database.DataBaseManager;
import org.funkoReactivo.services.files.CsvManager;
import org.funkoReactivo.services.files.JsonManager;
import org.funkoReactivo.services.funkos.FunkoCacheImpl;
import org.funkoReactivo.services.funkos.FunkoNotificacionImpl;
import org.funkoReactivo.services.funkos.FunkoServiceImpl;
import org.funkoReactivo.services.storage.FunkoStorageServImpl;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {

        FunkoServiceImpl service = FunkoServiceImpl.getInstance(
                FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance()),
                new FunkoCacheImpl(15, 1, 1, TimeUnit.MINUTES),
                FunkoNotificacionImpl.getInstance(),
                FunkoStorageServImpl.getInstance(
                        new CsvManager(),
                        new JsonManager()
                )
        );



        System.out.println("Sistema de obtención de notificaciones en Tiempo Real");
        service.getNotifications().subscribe(
                notificacion -> {
                    switch (notificacion.getTipo()) {
                        case NEW:
                            System.out.println("🟢 Funko insertado: " + notificacion.getContenido());
                            break;
                        case UPDATED:
                            System.out.println("🟠 Funko actualizado: " + notificacion.getContenido());
                            break;
                        case DELETED:
                            System.out.println("🔴 Funko eliminado: " + notificacion.getContenido());
                            break;
                    }
                },
                error -> System.err.println("Se ha producido un error: " + error),
                () -> System.out.println("Completado")
        );


        service.deleteAll().subscribe(
                funkos -> System.out.println("Funkos eliminados")
        );

        service.importCsv().subscribe(
                funkos -> System.out.println("Funkos importados")
        );

        System.out.println("Obtenemos todos los funkos");
        service.findAll().collectList().subscribe(
                alumnos -> System.out.println("Funkos: " + alumnos),
                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
                () -> System.out.println("Obtención de funkos completada")
        );

        service.backup().subscribe(
                successBackup -> System.out.println("Backup completado")
        );


        System.out.println("Obtenemos el funko mas caro");


        System.out.println("Obtenemos el funko mas caro");
        service.findAll().collectList().map(
                funkos -> getMostExpensiveFunko(funkos)
        ).subscribe(
                funko -> System.out.println("Funko mas caro: " + funko)
        );

        System.out.println("Obtenemos el precio medio de los funkos");
        service.findAll().collectList().map(
                funkos -> getAvgPricesFunkos(funkos)
        ).subscribe(
                avg -> System.out.println("Precio medio: " + avg)
        );

        System.out.println("Obtenemos los funkos agrupados por modelo");
        service.findAll().collectList().map(
                funkos -> getFunkosGroupByModel(funkos)
        ).subscribe(
                funkos -> System.out.println("Funkos agrupados por modelo: " + funkos)
        );



        System.out.println("Obtenemos el numero de funkos agrupados por modelo");
        service.findAll().collectList().map(
                funkos -> getCountFunkosGroupByModel(funkos)
        ).subscribe(
                funkos -> System.out.println("Numero de funkos agrupados por modelo: " + funkos)
        );


        System.out.println("Obtenemos los funkos que se lanzaron en 2023");
        service.findAll().collectList().map(
                funkos -> getFunkosRelease2023(funkos)
        ).subscribe(
                funkos -> System.out.println("Funkos lanzados en 2023: " + funkos)
        );

        System.out.println("Obtenemos los funkos con nombre Stitch y el numero de veces que aparece");
        service.findAll().collectList().map(
                funkos -> getFunkosWithNameStichANDCount(funkos)
        ).subscribe(
                funkos -> System.out.println("Funkos con nombre Stitch y el numero de veces que aparece: " + funkos)
        );

        service.stopCleaner();

    }

    private static Funko getMostExpensiveFunko(List<Funko> allFunkos){
        return allFunkos.stream()
                .max(Comparator.comparingDouble(Funko::getPrecio)).orElse(null);
    }

    private static Double getAvgPricesFunkos(List<Funko> allFunkos){
        return allFunkos.stream()
                .mapToDouble(Funko::getPrecio)
                .average().orElse(0);
    }

    private static Map<Modelo, List<Funko>> getFunkosGroupByModel(List<Funko> allFunkos){
        return  allFunkos.stream()
                .collect(Collectors.groupingBy(Funko::getModelo));
    }

    private static Map<Modelo, Long> getCountFunkosGroupByModel(List<Funko> allFunkos){
        return allFunkos.stream()
                .collect(Collectors.groupingBy(Funko::getModelo, Collectors.counting()));
    }

    private static List<Funko> getFunkosRelease2023(List<Funko> allFunkos){
        return allFunkos.stream()
                .filter(funko -> funko.getFecha().getYear() == 2023)
                .collect(Collectors.toList());
    }

    private static Map<String, Long> getFunkosWithNameStichANDCount(List<Funko> allFunkos){
        return allFunkos.stream()
                .filter(funko -> funko.getNombre().contains("Stitch"))
                .collect(Collectors.groupingBy(Funko::getNombre, Collectors.counting()));
    }
}