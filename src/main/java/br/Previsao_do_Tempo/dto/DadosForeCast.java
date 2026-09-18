package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosForeCast(@JsonProperty("forecastday") List<DadosForeCastDay> dadosForeCastDay) {
}
