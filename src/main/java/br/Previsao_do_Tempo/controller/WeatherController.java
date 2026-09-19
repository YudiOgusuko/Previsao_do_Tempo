package br.Previsao_do_Tempo.controller;

import br.Previsao_do_Tempo.dto.CoordinationDto;
import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.sevice.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clima")
@RequiredArgsConstructor
@Validated
public class WeatherController {

    private final WeatherService service;

    @GetMapping
    public ResponseEntity<List<WeatherDto>> weatherWeek(
            @RequestParam
            @NotBlank(message = "O nome da cidade não pode estar em branco.")
            @Size(min = 1, max = 58, message = "O nome da cidade deve ter entre 1 e 58 caracteres.")
            String cidade) {
        return ResponseEntity.ok().body(service.weatherWeek(cidade));
    }

    @GetMapping(value = "/now")
    public ResponseEntity<WeatherNowDto> weatherNow(
            @RequestParam
            @NotBlank(message = "O nome da cidade não pode estar em branco.")
            @Size(min = 1, max = 58, message = "O nome da cidade deve ter entre 1 e 58 caracteres.")
            String cidade) {
        return ResponseEntity.ok().body(service.weatherNow(cidade));
    }

    @GetMapping(value = "/equals")
    public ResponseEntity<List<CoordinationDto>> getCoordination(
            @RequestParam
            @NotBlank(message = "O nome da cidade não pode estar em branco.")
            @Size(min = 1, max = 85, message = "O nome da cidade deve ter entre 1 e 58 caracteres.")
            String cidade) {
        return ResponseEntity.ok().body(service.getCoordination(cidade));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<WeatherDto>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping(value = "/all/now")
    public ResponseEntity<List<WeatherNowDto>> findAllNow() {
        return ResponseEntity.ok().body(service.findAllNow());
    }
}
