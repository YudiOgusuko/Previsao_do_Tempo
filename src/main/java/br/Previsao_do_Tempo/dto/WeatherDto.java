package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonPropertyOrder({
         "cidade",
         "regiao",
         "pais",
         "temperaturaMaxima",
         "temperaturaMinima",
         "umidade",
         "descricao",
         "data",
        "diaDaSemana"
})

public record WeatherDto(String cidade,
                         String regiao,
                         String pais,
                         Double temperaturaMaxima,
                         Double temperaturaMinima,
                         Integer umidade,
                         String descricao,
                         LocalDate data,
                         String diaDaSemana){
}
