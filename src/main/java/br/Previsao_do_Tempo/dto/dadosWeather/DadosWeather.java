package br.Previsao_do_Tempo.dto.dadosWeather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosWeather(@JsonProperty("location") DadosLocation location,
                           @JsonProperty("forecast") DadosForeCast foreCast) {
}
