package br.Previsao_do_Tempo.sevice;

public interface IConverterDados {

    <T> T obterDados(String json, Class<T> classe);
}
