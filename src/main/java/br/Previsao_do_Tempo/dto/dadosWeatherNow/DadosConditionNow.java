package br.Previsao_do_Tempo.dto.dadosWeatherNow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosConditionNow(@JsonProperty("text") String descricao) {
}
