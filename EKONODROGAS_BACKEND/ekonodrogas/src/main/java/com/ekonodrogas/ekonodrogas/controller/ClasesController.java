package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.service.ClaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/clases")
@Tag(name = "Clases", description = "API para la gestión de clases")
public class ClasesController {

    private final ClaseService claseService;
    private final ClaseRepository claseRepository;

    @Autowired
    public ClasesController(ClaseService claseService, ClaseRepository claseRepository) {
        this.claseService = claseService;
        this.claseRepository = claseRepository;
    }


    @Operation(summary = "Listar todas las clases",
            description = "Obtiene una lista de todas las clases registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver las clases")
    })

    @GetMapping
    public String listarClases(Model model, Authentication authentication) {
        List<ClaseEntity> clases;

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
            // Si es docente, muestra solo sus clases
            clases = claseService.findClasesByDocente(authentication.getName());
        } else {
            // Para otros roles, muestra todas las clases
            clases = claseRepository.findAll();
        }

        model.addAttribute("clases", clases);
        return "clases";
    }


    @Operation(summary = "Listar todas las clases",
            description = "Obtiene una lista de todas las clases registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver las clases")
    })
    @GetMapping("/crear")
    public String mostrarCreacionClase(Model model) {
        model.addAttribute("claseDTO", new ClaseDTO());
        return "crear";
    }


    @Operation(summary = "Crear una nueva clase",
            description = "Crea una nueva clase con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de clase inválidos"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para crear clases")
    })
    @PostMapping("/crear")
    public String crearClase(@ModelAttribute("claseDTO") ClaseDTO claseDTO, RedirectAttributes redirectAttributes) {
        try {
            // Valida horarios
            if (claseDTO.getHorarioFinal() <= claseDTO.getHorarioInicio()) {
                redirectAttributes.addFlashAttribute("error", "El horario final debe ser " +
                        "mayor al horario de inicio");
                return "redirect:/clases/crear";
            }

            ClaseEntity nuevaClase = ClaseEntity.builder()
                    .nombre(claseDTO.getNombre())
                    .descripcion(claseDTO.getDescripcion())
                    .salon(claseDTO.getSalon())
                    .horarioInicio(claseDTO.getHorarioInicio())
                    .horarioFinal(claseDTO.getHorarioFinal())
                    .docenteUsername(claseDTO.getDocenteUsername())
                    .build();

            claseService.save(nuevaClase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase creada con éxito");
            return "redirect:/home";
            } catch (Exception e) {
                log.error("Error al crear la clase", e);
                redirectAttributes.addFlashAttribute("error", "Error al intentar " +
                        "crear la clase: " + e.getMessage());
                return "redirect:/clases/crear";
            }
    }

    @Operation(summary = "Eliminar una clase",
            description = "Elimina una clase existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase eliminada  con éxito"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar clases")
    })

    @DeleteMapping("/{id}")
    public String eliminarClase(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        try {

            if (!claseRepository.existsById(id)) {
                throw new RuntimeException("Clase no encontrada con ID: " + id);
            }

            claseRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Clase eliminada con éxito");
            return "redirect:/clases";
        } catch (Exception e) {
            log.error("Error al eliminar la clase", e);
            redirectAttributes.addFlashAttribute("error", "Error al intentar " +
                    "eliminar la clase: " + e.getMessage());
            return "redirect:/clases";
        }
    }

    @Operation(summary = "Mostrar formulario de edición",
            description = "Muestra el formulario para editar una clase existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para editar clases")
    })

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ClaseEntity clase = claseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + id));


            ClaseDTO claseDTO = ClaseDTO.builder()
                    .nombre(clase.getNombre())
                    .descripcion(clase.getDescripcion())
                    .horarioInicio(clase.getHorarioInicio())
                    .horarioFinal(clase.getHorarioFinal())
                    .docenteUsername(clase.getDocenteUsername())
                    .salon(clase.getSalon())
                    .build();


            model.addAttribute("claseDTO", claseDTO);
            model.addAttribute("claseId", id);
            return "editar";


        } catch (Exception e) {
            log.error("Error al cargar la clase para editar", e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar la clase: "
                    + e.getMessage());
            return "redirect:/clases";
        }
    }

    @Operation(summary = "Actualizar una clase",
            description = "Actualiza los datos de una clase existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de clase inválidos"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para actualizar clases")
    })

    @PostMapping("/editar/{id}")
    public String actualizarClase(@PathVariable Long id,
                                  @ModelAttribute ClaseDTO claseDTO,
                                  RedirectAttributes redirectAttributes,
                                  Authentication authentication) {
        try {
            ClaseEntity clase = claseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

            // Verificar si es DOCENTE y es su clase
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
                // El docente solo puede modificar horarios
                clase.setHorarioInicio(claseDTO.getHorarioInicio());
                clase.setHorarioFinal(claseDTO.getHorarioFinal());
            } else {
                // El RECTOR puede modificar todo
                clase.setNombre(claseDTO.getNombre());
                clase.setDescripcion(claseDTO.getDescripcion());
                clase.setDocenteUsername(claseDTO.getDocenteUsername());
                clase.setSalon(claseDTO.getSalon());
                clase.setHorarioInicio(claseDTO.getHorarioInicio());
                clase.setHorarioFinal(claseDTO.getHorarioFinal());
            }

            claseRepository.save(clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase actualizada exitosamente");
            return "redirect:/clases";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la clase: " + e.getMessage());
            return "redirect:/clases/editar/" + id;
        }
    }



}