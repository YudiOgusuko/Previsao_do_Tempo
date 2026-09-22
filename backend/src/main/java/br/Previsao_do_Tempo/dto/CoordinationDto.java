package br.Previsao_do_Tempo.dto;

import br.Previsao_do_Tempo.dto.dadosCoordination.DadosCoordination;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({
        "cidade",
        "regiao",
        "pais",
        "latitude",
        "longitude"
})

public record CoordinationDto(String cidade,
                              String regiao,
                              String pais,
                              Double latitude,
                              Double longitude) {

    public CoordinationDto (DadosCoordination dadosCoordination) {
        this(dadosCoordination.cidade(),
                dadosCoordination.regiao(),
                dadosCoordination.pais(),
                dadosCoordination.latitude(),
                dadosCoordination.longitude());
    }
}
