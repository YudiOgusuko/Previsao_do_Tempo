package br.Previsao_do_Tempo.dto;

import br.Previsao_do_Tempo.model.WeatherNow;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public WeatherNowDto(WeatherNow weatherNow) {
        this(weatherNow.getCidade(),
                weatherNow.getRegiao(),
                weatherNow.getPais(),
                weatherNow.getTemperaturaAtual(),
                weatherNow.getDescricao(),
                weatherNow.getUmidade(),
                weatherNow.getData() != null ? LocalDateTime.ofInstant(weatherNow.getData(), ZoneId.of("America/Sao_Paulo")) : null,
                weatherNow.getDiaDaSemana());
    }
}
