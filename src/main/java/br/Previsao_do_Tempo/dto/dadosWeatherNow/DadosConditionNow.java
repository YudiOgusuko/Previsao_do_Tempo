package br.Previsao_do_Tempo.dto.dadosWeatherNow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosConditionNow(@JsonProperty("text") String descricao) {
}
