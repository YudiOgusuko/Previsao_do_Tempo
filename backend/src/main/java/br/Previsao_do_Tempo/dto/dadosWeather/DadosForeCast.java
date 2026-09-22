package br.Previsao_do_Tempo.dto.dadosWeather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosForeCast(@JsonProperty("forecastday") List<DadosForeCastDay> dadosForeCastDay) {
}
