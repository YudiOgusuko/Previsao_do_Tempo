package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosDay(@JsonProperty("maxtemp_c") Double temperatura_maxima,
                       @JsonProperty("mintemp_c") Double temperatura_minima,
                       @JsonProperty("avghumidity") Integer umidade,
                       @JsonProperty("condition") DadosCondition condition) {
}
