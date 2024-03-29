package com.example.APIRest.controller;

import com.example.APIRest.model.Ejemplo;
import com.example.APIRest.service.EjemploService;
import com.example.APIRest.util.ImageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EjemploController {
    @Autowired
    private EjemploService ejemploService;

    @GetMapping("/persona")
    public List<Ejemplo> getAllEjemplos() {
        return ejemploService.getAllEjemplos();
    }

    @Operation(summary = "Crea una persona", description = "Añade una persona a la coleccion", tags = {"personas"})
    @ApiResponse(responseCode = "201", description = "Persona añadida")
    @ApiResponse(responseCode = "400", description = "Datos de persona no válidos")
    @PostMapping(value = "/persona", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Ejemplo> createEjemplo(@RequestParam String nombre, @RequestParam Integer edad,
                                                 @RequestPart(name="imagen",required=false) MultipartFile imagen) throws IOException {

        Ejemplo createdEjemplo = ejemploService.createEjemplo(new Ejemplo(nombre, edad), imagen);
        return new ResponseEntity<>(createdEjemplo, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene una Persona", description = "Obtiene una persona dado su id", tags = {"personas"})
    @Parameter(name = "id", description = "ID de la Persona", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Persona encontrada")
    @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    @GetMapping("/persona/{id}")
    public ResponseEntity<Ejemplo> getEjemploById(@PathVariable Long id) {
        Optional<Ejemplo> optionalEjemplo = ejemploService.getEjemploById(id);
        if (((Optional<?>) optionalEjemplo).isPresent()) {
            optionalEjemplo = ejemploService.getEjemploById(id);
            return new ResponseEntity<>(optionalEjemplo.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/persona/{id}")
    public ResponseEntity<Ejemplo> updateEjemplo(@PathVariable Long id,
                                                 @RequestParam String nombre,
                                                 @RequestParam Integer edad,
                                                 @RequestPart(name = "imagen",required = false)
                                                     MultipartFile imagen) throws IOException {
        Optional<Ejemplo> optionalEjemplo = ejemploService.getEjemploById(id);

        if (((Optional<?>) optionalEjemplo).isPresent()) {
            Ejemplo existingEjemplo = optionalEjemplo.get();
            existingEjemplo.setNombre(nombre);
            existingEjemplo.setEdad(edad);
            existingEjemplo.setUpdated_at(LocalDateTime.now());
            existingEjemplo.setFoto(ImageUtils.compressImage(imagen.getBytes()));

            Ejemplo updatedEjemplo = ejemploService.updateEjemplo(existingEjemplo,imagen);
            return new ResponseEntity<>(updatedEjemplo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/persona/{id}")
    public ResponseEntity<Void> deleteEjemplo(@PathVariable Long id) {
        Optional<Ejemplo> optionalEjemplo = ejemploService.getEjemploById(id);
        if (optionalEjemplo.isPresent()) {
            ejemploService.deleteEjemploById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Muestra foto", description = "Obtiene foto de persona dado el id", tags = {"personas"})
    @Parameter(name = "id", description = "ID de la Persona", required = true,
            example = "13")
    @ApiResponse(responseCode = "200", description = "Foto de la persona")
    @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    @GetMapping(value = "/{id}/foto", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> descargarFoto(@PathVariable Long id) {
        byte[] foto = ejemploService.descargarFoto(id);
        if (foto != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(foto);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/personasview")
    public ModelAndView listado(Model modelo) throws UnsupportedEncodingException {
        List<Ejemplo> personas = getAllEjemplos();
        modelo.addAttribute("listaPersonas", personas);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("listado.html");
        return modelAndView;
    }
}
