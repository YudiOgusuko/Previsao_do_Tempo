package br.Previsao_do_Tempo.controller;

import br.Previsao_do_Tempo.dto.CoordinationDto;
import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.handler.exception.BadRequestException;
import br.Previsao_do_Tempo.handler.exception.NotFoundException;
import br.Previsao_do_Tempo.sevice.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService service;

    @Test
    @DisplayName("Clima Geral - GET OK.")
    void weatherWeek_Ok() throws Exception {

       String cidade = "Sao Paulo";
       String regiao = "Sao Paulo";
       String pais = "Brazil";
       Integer dias = 7;

       given(service.weatherWeek(cidade, regiao, pais, dias))
               .willReturn(List.of(WeatherDto.builder().build()));

       mockMvc.perform(
               get("/api/clima")
                       .param("cidade", cidade)
                       .param("regiao", regiao)
                       .param("pais", pais)
                       .param("dias", String.valueOf(dias))
                       .contentType(MediaType.APPLICATION_JSON)
       ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Clima Geral - GET ERRO.")
    void weatherWeek_Erro() throws Exception {

        String cidade = "cidade inexistente";
        String regiao = "região errada";
        String pais = "pais estranho";
        Integer dias = 7;

        given(service.weatherWeek(cidade, regiao, pais, dias))
                .willThrow(new BadRequestException("Os dados fornecidos são inválidos."));

        mockMvc.perform(
                get("/api/clima")
                        .param("cidade", cidade)
                        .param("dias", String.valueOf(dias))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Clima Atual - GET OK.")
    void weatherNow_Ok() throws Exception {

        String cidade = "Santo André";
        String regiao = "Sao Paulo";
        String pais = "Brazil";

        given(service.weatherNow(cidade, regiao, pais))
                .willReturn(WeatherNowDto.builder().build());

        mockMvc.perform(
                get("/api/clima/now")
                        .param("cidade", cidade)
                        .param("regiao", regiao)
                        .param("pais", pais)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Clima Atual - GET ERRO.")
    void weatherNow_Erro() throws Exception {

        String cidade = "Santo André";
        String regiao = "Sao Paulo";
        String pais = "Brazil";

        given(service.weatherNow(cidade, regiao, pais))
                .willThrow(new BadRequestException("Os dados fornecidos são inválidos."));

        mockMvc.perform(
                get("/api/clima/now")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Coordenadas - GET OK.")
    void getCoordination_Ok() throws Exception {

        String cidade = "Tokyo";

        given(service.getCoordination(cidade))
                .willReturn(List.of(CoordinationDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/coordination")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Coordenadas - GET ERRO.")
    void getCoordination_Erro() throws Exception {

        String cidade = "Tokyo";

        given(service.getCoordination(cidade))
                .willThrow(new NotFoundException("Coordenada da cidade não encontrada."));

        mockMvc.perform(
                get("/api/clima/coordination")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Listar cidades com inicias iguais - GET OK.")
    void search_OK() throws Exception {
        String inicial_cidade = "Sao";

        given(service.search(inicial_cidade))
                .willReturn(List.of(CoordinationDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/search")
                        .param("q", inicial_cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Listar cidades com inicias iguais - GET ERRO.")
    void search_Erro() throws Exception {
        String inicial_cidade = "Sao";

        given(service.search(inicial_cidade))
                .willThrow(new NotFoundException("Nenhuma dado foi encontrado."));

        mockMvc.perform(
                get("/api/clima/search")
                        .param("q", inicial_cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Clima Geral - findAll OK.")
    void findAll_Ok() throws Exception {

        given(service.findAll())
                .willReturn(List.of(WeatherDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Clima Geral - findAll ERRO.")
    void findAll_Erro() throws Exception {

        given(service.findAll())
                .willThrow(new NotFoundException("Nenhum dado foi encontrado."));

        mockMvc.perform(
                get("/api/clima/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Clima Atual - findAll OK.")
    void findAllNow_Ok() throws Exception {

        given(service.findAllNow())
                .willReturn(List.of(WeatherNowDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/all/now")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Clima Atual - findAll ERRO.")
    void findAllNow_Erro() throws Exception {

        given(service.findAllNow())
                .willThrow(new NotFoundException("Nenhum dado foi encontrado."));

        mockMvc.perform(
                get("/api/clima/all/now")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}