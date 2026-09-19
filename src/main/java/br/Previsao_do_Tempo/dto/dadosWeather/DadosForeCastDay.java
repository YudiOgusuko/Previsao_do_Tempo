package br.Previsao_do_Tempo.dto.dadosWeather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DadosForeCastDay(@JsonProperty("date") LocalDate data,
                               @JsonProperty("day") DadosDay day){
}
