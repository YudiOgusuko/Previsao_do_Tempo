package br.Previsao_do_Tempo.dto.dadosWeather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosForeCastDay(@JsonProperty("date") LocalDate data,
                               @JsonProperty("day") DadosDay day){
}
