package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLocation(@JsonProperty("name") String cidade,
                            @JsonProperty("region") String regiao,
                            @JsonProperty("country") String pais){
}
