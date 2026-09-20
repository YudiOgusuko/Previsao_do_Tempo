package br.Previsao_do_Tempo.dto.dadosWeatherNow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosCurrentNow(@JsonProperty("temp_c") Double temperaturaAtual,
                              @JsonProperty("condition") DadosConditionNow condition,
                              @JsonProperty("humidity") Integer umidade){
}
