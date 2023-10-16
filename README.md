# FunkoReactivo


## Autor
Daniel Garrido Muros

### Descripción

Este es un proyecto para aprender como se utiliza la programación
reactiva en java utilizando el mismo crud de las anteriores prácticas


### Repositorio de funkos reactivo

En este proyecto deberemos de crear el repositorio 
de manera que obtenga los datos de manera reactiva de la base de datos

``` java
    /**
     * Obtenemos instancia de FunkoRepositoryImpl
     * @param dataBaseManager
     * @return
     */
    public static FunkoRepositoryImpl getInstance(DataBaseManager dataBaseManager){
        if(instance == null){
            instance = new FunkoRepositoryImpl(dataBaseManager);
        }
        return instance;
    }

    /**
     * Guarda un funko en la base de datos
     * @param funko
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Mono<Funko> save(Funko funko) throws SQLException, SQLException {
        logger.debug("Guardando funko: {}", funko);
        if(funko == null){
            return Mono.empty();
        }
        String query = "INSERT INTO funkos (cod, myId, name, model, price, release_date) VALUES(?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getCOD())
                        .bind(1, funko.getMyId())
                        .bind(2, funko.getNombre())
                        .bind(3, funko.getModelo().toString())
                        .bind(4, funko.getPrecio())
                        .bind(5, funko.getFecha())
                        .returnGeneratedValues("id")
                        .execute()
                ).flatMap(res -> Mono.from(res.map((row, rowMetadata) ->{
                            funko.setId(row.get("id", Integer.class));
                            return funko;
                        }
                ))).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Actualiza un funko en la base de datos
     * @param funko
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Mono<Funko> update(Funko funko) throws SQLException, SQLException {
        logger.debug("Actualizando funko: {}", funko);
        if(funko == null){
            return Mono.empty();
        }
        String query = "UPDATE funkos SET name = ?, model = ?, price = ?, updated_at = ? WHERE id = ?";

        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getNombre())
                        .bind(1, funko.getModelo().toString())
                        .bind(2, funko.getPrecio())
                        .bind(3, LocalDateTime.now())
                        .bind(4, funko.getId())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Busca un funko por su id
     * @param integer
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Mono<Funko> findById(Integer integer) throws SQLException {
        logger.debug("Buscando funko por id: {}", integer);
        String query = "SELECT * FROM funkos WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, integer)
                        .execute()
                ).flatMap(result -> Mono.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }

    /**
     * Busca todos los funkos
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Flux<Funko> findAll() throws SQLException {
        logger.debug("Buscando todos los funkos");
        String query = "SELECT * FROM funkos";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .execute()
                ).flatMap(result -> Flux.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }

    /**
     * Elimina un funko por su id
     * @param integer
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Mono<Boolean> deleteById(Integer integer) throws SQLException {
        logger.debug("Eliminando funko por id: {}", integer);
        if(this.findById(integer).block() == null){
            return Mono.just(false);
        }
        String query = "DELETE FROM funkos WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                                .bind(0, integer)
                                .execute()
                        ).flatMapMany(Result::getRowsUpdated)
                        .hasElements(),
                Connection::close
        );
    }

    /**
     * Elimina todos los funkos
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Mono<Void> deleteAll() throws SQLException {
        logger.debug("Eliminando todos los funkos");
        String query = "DELETE FROM funkos";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .execute()
                ).then(),
                Connection::close
        );
    }

    /**
     * Busca un funko por su nombre
     * @param name
     * @throws SQLException
     * @throws SQLException
     */
    @Override
    public Flux<Funko> findByNombre(String name) throws SQLException {
        logger.debug("Buscando funko por nombre: {}", name);
        String query = "SELECT * FROM funkos WHERE name = ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .bind(0, name)
                        .execute()
                ).flatMap(result -> Flux.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }
```


### Servicio reactivo

Como en este proyecto utilizamos reactividad para obtener los datos
deberemos crar un servicio reactivo con cache 

``` java
/**
     * Instancia de la clase
     * @param repositoryFunko
     * @param cache
     * @param notificacion
     * @param storageFunko
     * @return FunkoServiceImpl
     */
    public static FunkoServiceImpl getInstance(FunkoRepository repositoryFunko, FunkoCache cache, FunkoNotificacionImpl notificacion,  FunkoStorageServ storageFunko){
        if(instance == null){
            instance = new FunkoServiceImpl(repositoryFunko, cache, notificacion, storageFunko);
        }
        return instance;
    }


    /**
     * Obtiene todos los funkos
     */
    @Override
    public Flux<Funko> findAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo todos los funkos");
        return repository.findAll();
    }

    /**
     * Obtiene los funkos con nombre
     * @param nombre
     */
    @Override
    public Flux<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo funkos con nombre: {}", nombre);
        return repository.findByNombre(nombre);
    }

    /**
     * Obtiene los funkos con id
     * @param id
     */
    @Override
    public Mono<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Buscando funko con id: {}", id);
        return cache.get(id).switchIfEmpty(repository.findById(id).flatMap(
                funko -> {
                    try {
                        return cache.put(funko.getId(), funko).then(Mono.justOrEmpty(funko));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        )).switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + id)));
    }

    /**
     * Guarda un funko
     * @param funko
     */
    public Mono<Funko> saveWithoutNotify(Funko funko) throws Exception {
        logger.debug("Guardando funko: {}", funko);
        return repository.save(funko).doOnSuccess(
        funkoSaved -> {
            try {
                cache.put(funkoSaved.getId(), funkoSaved);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Guarda un funko y notifica
     * @param funko
     */
    @Override
    public Mono<Funko> save(Funko funko) throws Exception {
        return saveWithoutNotify(funko).doOnSuccess(
        funkoSaved -> {
            try {
                notificacion.notify(new Notificacion<>(Notificacion.Tipo.NEW, funkoSaved));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).switchIfEmpty(Mono.error(new FunkoNoGuardado("No se ha podido guardar el funko con id cod: "+funko.getId())));
    }

    /**
     * Actualiza un funko sin notificar
     * @param funko
     */
    public Mono<Funko> updateWithoutNotify(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Actualizando funko: {}", funko);
        return this.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + funko.getId())))
                .flatMap(
                        funkoFound -> {
                            try {
                                return repository.update(funko).flatMap(updatedFunko ->
                                {
                                    try {
                                        return cache.put(updatedFunko.getId(), updatedFunko).then(Mono.just(updatedFunko)).doOnError(
                                                error -> logger.error(error.getMessage()));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    /**
     * Actualiza un funko y notifica
     * @param funko
     */
    @Override
    public Mono<Funko> update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        return updateWithoutNotify(funko).doOnSuccess(
                funkoUpdated -> {
                    try {
                        notificacion.notify(new Notificacion<>(Notificacion.Tipo.UPDATED, funkoUpdated));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    /**
     * Elimina un funko sin notificar
     * @param id
     */
    private Mono<Funko> deleteByIdWithoutNotification(Integer id) throws SQLException {
        logger.debug("Eliminando funko con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + id)))
                .flatMap(alumno -> {
                    try {
                        return cache.delete(alumno.getId())
                                .then(repository.deleteById(alumno.getId()))
                                .thenReturn(alumno);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Elimina un funko y notifica
     * @param id
     */
    @Override
    public Mono<Boolean> deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        return  deleteByIdWithoutNotification(id).doOnSuccess(
                funkoDeleted -> {
                    try {
                        notificacion.notify(new Notificacion<>(Notificacion.Tipo.DELETED, funkoDeleted));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).map(funko -> true);
    }

    /**
     * Elimina todos los funkos
     */
    @Override
    public Mono<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando todos los funkos");
        cache.clear();
        return repository.deleteAll().then(Mono.empty());
    }

    /**
     * Realiza un backup de los funkos
     */
    @Override
    public Mono<Boolean> backup() throws SQLException, ExecutionException, InterruptedException, ExportException {
        logger.debug("Realizando backup de los funkos");
        return repository.findAll().collectList().flatMap(
                funkos -> {
                    try {
                        return storageFunko.exportToJsonAsync(funkos);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).switchIfEmpty(Mono.just(false));
    }

    /**
     * Importa los funkos desde un csv
     */
    @Override
    public Flux<Funko> importCsv() throws ExecutionException, InterruptedException {
        logger.debug("Importando funkos desde csv");
        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        return storageFunko.importFromCsvAsync(file).flatMap(
            funko -> {
                try {
                    return save(funko);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        });
    }

    /**
     * Obtiene las notificaciones
     */
    public Flux<Notificacion<Funko>> getNotifications() {
        return notificacion.getNotificacion();
    }

    /**
     * Detiene el cleaner
     */
    public void stopCleaner(){
        cache.shutdown();
    }
```


Ademas para crear nuestro servicio necesitaremos 
nuestra clase de notificaciones que se encuentra en el proyecto.

### Notificaciones

``` java
  /**
     * Obtiene la instancia de FunkoNotificacionImpl
     */
    public static FunkoNotificacionImpl getInstance(){
        if(instance == null){
            instance = new FunkoNotificacionImpl();
        }
        return instance;
    }

    /**
     * Obtiene la notificacion
     */
    @Override
    public Flux<Notificacion<Funko>> getNotificacion() {
        return FunkoNotificationFlux;
    }

    /**
     * Notifica a los subscriptores
     * @param notify
     */
    @Override
    public void notify(Notificacion<Funko> notify) {
        FunkoNotification.next(notify);
    }
```


### Storage (Almacenamiento)

Como ultima cosa por resaltar tenemos la clase storage que se encargar de hacer los backup y las importaciones de la base de datos

``` java
 /**
     * Método que se encarga de crear el directorio para almacenar los backups
     */
    private boolean initDirectories(){
        String absolute_path = Paths.get("").toAbsolutePath().toString();
        backupDirectory = new File(absolute_path + File.separator + "backups");
        boolean existDirectory = backupDirectory.exists();
        System.out.println(backupDirectory.toString());
        if(!existDirectory){
            logger.debug("Creamos directorio para backups");
            return backupDirectory.mkdirs();
        }
        return existDirectory;
    }

    /**
     * Método que se encarga de crear una instancia de la clase
     * @param csvManager
     * @param jsonManager
     */
    public static FunkoStorageServImpl getInstance(CsvManager csvManager, JsonManager jsonManager) {
        if(instance == null){
            instance = new FunkoStorageServImpl(csvManager, jsonManager);
        }
        return instance;
    }

    /**
     * Metodo que se encarga de exportar los datos a un archivo JSON
     * @param data
     */
    public Mono<Boolean> exportToJsonAsync(List<Funko> data) throws ExecutionException, InterruptedException {
        logger.debug("Exportando a datos a JSON");
        String name_file = File.separator + "backupJsonFunkos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".json";
        return jsonManager.writeFunkosToJson(data, backupDirectory.getAbsolutePath() + name_file);
    }

    /**
     * Metodo que se encarga de importar los datos a un archivo CSV
     * @param filePath
     */
    @Override
    public Flux<Funko> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException {
        logger.debug("Importando datos desde csv");
        return csvManager.readCsv(filePath);
    }
```