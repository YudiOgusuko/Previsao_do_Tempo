package br.Previsao_do_Tempo.dto.dadosCoordination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosCoordination(@JsonProperty("name") String cidade,
                                @JsonProperty("region") String regiao,
                                @JsonProperty("country") String pais,
                                @JsonProperty("lat") Double latitude,
                                @JsonProperty("lon") Double longitude) {
}
