package com.fachada.cagepa.fachada_cagepa.padroes.watcher;

import com.fachada.cagepa.fachada_cagepa.padroes.ocr.LeituraHidrometroService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

@Component
public class ImagemWatcher implements Runnable {

    private final LeituraHidrometroService leituraHidrometroService;
    private volatile String directoryToWatch;
    private volatile boolean running = false;
    private final Set<String> processedFiles = new HashSet<>();
    private final Object lock = new Object();

    public ImagemWatcher(LeituraHidrometroService leituraHidrometroService) {
        this.leituraHidrometroService = leituraHidrometroService;
    }

    public void setDirectoryToWatch(String path) {
        this.directoryToWatch = path;
    }

    public void start() {
        if (!running) {
            running = true;
            Thread watcherThread = new Thread(this, "ImagemWatcher-Thread");
            watcherThread.setDaemon(false);
            watcherThread.start();
            System.out.println("Iniciando monitoramento do diretorio: " + directoryToWatch);
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            Path path = Paths.get(directoryToWatch);
            if (!Files.exists(path)) {
                System.err.println("Diretorio nao encontrado: " + directoryToWatch);
                return;
            }

            processExistingFiles(path);

            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (running) {
                WatchKey key = watchService.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path filename = (Path) event.context();
                        Path fullPath = path.resolve(filename);

                        String filenameStr = filename.toString().toLowerCase();
                        if (isImageFile(filenameStr)) {
                            // Aguardar mais tempo para arquivo ser completamente escrito
                            Thread.sleep(1500);

                            synchronized (lock) {
                                // Remover do cache para permitir reprocessamento
                                processedFiles.remove(fullPath.toString());
                                processImageFile(fullPath.toFile());
                                processedFiles.add(fullPath.toString());
                            }
                        }
                    }
                }

                if (!key.reset()) {
                    System.err.println("WatchKey invalido, encerrando watcher");
                    break;
                }
            }

            watchService.close();

        } catch (Exception e) {
            System.err.println("Erro no watcher de imagens: " + e.getMessage());
        }
    }

    private void processExistingFiles(Path path) {
        try (var stream = Files.list(path)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> isImageFile(p.toString().toLowerCase()))
                    .forEach(p -> {
                        synchronized (lock) {
                            if (!processedFiles.contains(p.toString())) {
                                processImageFile(p.toFile());
                                processedFiles.add(p.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            System.err.println("Erro ao processar arquivos existentes: " + e.getMessage());
        }
    }

    private void processImageFile(File imageFile) {
        try {
            System.out.println("[ImagemWatcher] Processando arquivo: " + imageFile.getAbsolutePath());
            leituraHidrometroService.processarImagemHidrometro(imageFile);
            System.out.println("[ImagemWatcher] Arquivo processado com sucesso: " + imageFile.getName());
        } catch (Exception e) {
            System.err.println("[ImagemWatcher] Erro ao processar arquivo " + imageFile.getName() + ": " + e.getMessage());
        }
    }

    private boolean isImageFile(String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || 
               filename.endsWith(".png") || filename.endsWith(".bmp") ||
               filename.endsWith(".gif") || filename.endsWith(".tiff");
    }
}
