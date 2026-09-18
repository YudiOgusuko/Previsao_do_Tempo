package br.Previsao_do_Tempo.sevice;

import tools.jackson.databind.ObjectMapper;

public class ConverterDados implements IConverterDados{

   private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        return objectMapper.readValue(json, classe);
    }
}
