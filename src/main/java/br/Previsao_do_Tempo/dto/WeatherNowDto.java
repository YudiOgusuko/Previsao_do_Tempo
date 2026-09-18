package br.Previsao_do_Tempo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonPropertyOrder({
        "cidade",
        "regiao",
        "pais",
        "temperatura",
        "descricao",
        "umidade",
        "data",
        "diaDaSemana"
})

public record WeatherNowDto(String cidade,
                            String regiao,
                            String pais,
                            Double temperaturaAtual,
                            String descricao,
                            Integer umidade,
                            @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime data,
                            String diaDaSemana) {
}
