package br.Previsao_do_Tempo.handler.entity;

import lombok.Builder;

@Builder
public record ErrorResponse(String message,
                            Integer status){
}
