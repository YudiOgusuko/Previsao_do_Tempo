package br.Previsao_do_Tempo.dto.dadosWeatherNow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLocationNow(@JsonProperty("name") String cidade,
                               @JsonProperty("region") String regiao,
                               @JsonProperty("country") String pais,
                               @JsonProperty("localtime") @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime dataEHorario) {
}
