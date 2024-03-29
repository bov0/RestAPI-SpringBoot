package com.example.APIRest.service;
import com.example.APIRest.exceptions.EjemploBadRequestException;
import com.example.APIRest.exceptions.EjemploImagenException;
import com.example.APIRest.exceptions.EjemploNotFoundException;
import com.example.APIRest.model.Ejemplo;
import com.example.APIRest.repository.EjemploRepository;
import com.example.APIRest.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Service
public class EjemploService {
    @Autowired
    private EjemploRepository ejemploRepository;

    public List<Ejemplo> getAllEjemplos() {
        return ejemploRepository.findAll();
    }

    public Ejemplo createEjemplo(Ejemplo ejemplo, MultipartFile file) throws IOException {
        if (ejemplo.getNombre() == null || ejemplo.getNombre().isEmpty())
            throw new EjemploBadRequestException("Debe introducirse el nombre");
        if (ejemplo.getEdad() == null || ejemplo.getEdad() <= 0)
            throw new EjemploBadRequestException("Debe introducirse la edad y debe ser mayor que 0");

        Ejemplo ejemplosave = new Ejemplo(ejemplo.getNombre(),ejemplo.getEdad());

        System.out.println("Nombre del archivo: " + file.getOriginalFilename());

        if (!file.isEmpty()) {
            ejemplosave.setImagen(file.getOriginalFilename());
            ejemplosave.setFoto(ImageUtils.compressImage(file.getBytes())); // Almacena en BD el binario de la foto
// El resto de líneas es para almacenar la imagen en disco
            Path dirImg = Paths.get("src//main//resources//static//img");
            String rutaAbsoluta = dirImg.toFile().getAbsolutePath();
            try {
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" +
                        file.getOriginalFilename());
                Files.write(rutaCompleta, bytesImg);
            } catch (IOException e) {
                throw new EjemploImagenException("Error de escritura");
            }
        }
        else
            throw new EjemploBadRequestException("Debe introducirse el fichero imagen");

        return ejemploRepository.save(ejemplosave);
    }

    public Optional<Ejemplo> getEjemploById(Long id) {
        return Optional.ofNullable(ejemploRepository.findById(id).orElseThrow(
                () -> new EjemploNotFoundException("No se ha encontrado la persona con id: " + id)
        ));
    }

    public Ejemplo updateEjemplo(Ejemplo ejemplo, MultipartFile file) throws IOException
    {
        if (ejemplo.getNombre() == null || ejemplo.getNombre().isEmpty())
            throw new EjemploBadRequestException("Debe introducirse el nombre");
        if (ejemplo.getEdad() == null || ejemplo.getEdad() <= 0)
            throw new EjemploBadRequestException("Debe introducirse la edad y debe ser mayor que 0");
        if (!file.isEmpty()) {
            ejemplo.setImagen(file.getOriginalFilename());
            ejemplo.setFoto(ImageUtils.compressImage(file.getBytes())); // Almacena en BD el binario de la foto
// El resto de líneas es para almacenar la imagen en disco
            Path dirImg = Paths.get("src//main//resources//static//img");
            String rutaAbsoluta = dirImg.toFile().getAbsolutePath();
            try {
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" +
                        file.getOriginalFilename());
                Files.write(rutaCompleta, bytesImg);
            } catch (IOException e) {
                throw new EjemploImagenException("Error de escritura");
            }
        }
        else
            throw new EjemploBadRequestException("Debe introducirse el fichero imagen");
        return ejemploRepository.save(ejemplo);
    }

    public void deleteEjemploById(Long id) {
        ejemploRepository.deleteById(id);
    }
// Otros métodos para operaciones específicas
// public List<Ejemplo> getEjemplosByNombre(String nombre) { return ejemploRepository.findByNombreContainingIgnoreCase(nombre); }

    public byte[] descargarFoto(Long id) {
        Ejemplo ejemplo = ejemploRepository.findById(id).orElse(null);
        return ejemplo != null ? ImageUtils.decompressImage(ejemplo.getFoto()): null;
    }
}