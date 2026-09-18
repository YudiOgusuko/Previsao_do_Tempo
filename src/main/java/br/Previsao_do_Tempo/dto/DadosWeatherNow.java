package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosWeatherNow(@JsonProperty("location") DadosLocationNow location,
                              @JsonProperty("current") DadosCurrentNow current){
}
