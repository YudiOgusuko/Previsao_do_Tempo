package br.Previsao_do_Tempo.dto.dadosWeather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosWeather(@JsonProperty("location") DadosLocation location,
                           @JsonProperty("forecast") DadosForeCast foreCast) {
}
