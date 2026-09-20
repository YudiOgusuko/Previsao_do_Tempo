package br.Previsao_do_Tempo.controller;

import br.Previsao_do_Tempo.dto.CoordinationDto;
import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.handler.exception.NotFoundException;
import br.Previsao_do_Tempo.sevice.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private WeatherDto weatherDto;
    private WeatherNowDto weatherNowDto;
    private CoordinationDto coordinationDto;

    @BeforeEach
    void setUp(){
        weatherDto = WeatherDto.builder()
                .cidade("Sao Paulo")
                .regiao("Sao Paulo")
                .pais("Brazil")
                .temperaturaMaxima(21.3)
                .temperaturaMinima(17.6)
                .umidade(78)
                .descricao("Encoberto")
                .data(LocalDate.now())
                .diaDaSemana("Domingo")
                .build();

        weatherNowDto = WeatherNowDto.builder()
                .cidade("Santo André")
                .regiao("Santo André")
                .pais("Brazil")
                .temperaturaAtual(15.2)
                .descricao("Chuva irregular nas proximidades")
                .umidade(81)
                .data(LocalDateTime.now())
                .diaDaSemana("Domingo")
                .build();

        coordinationDto = CoordinationDto.builder()
                .cidade("Tokyo")
                .regiao("Tokyo")
                .pais("Japan")
                .latitude(35.69)
                .longitude(139.60)
                .build();
    }

    @Test
    @DisplayName("OK - requisição GET para previsão do tempo dos próximos dias.")
    void weatherWeekOk() throws Exception {

       String cidade = "Sao Paulo";
       Integer dias = 7;

       given(service.weatherWeek(cidade, dias))
               .willReturn(List.of(weatherDto));

       mockMvc.perform(
               get("/api/clima")
                       .param("cidade", cidade)
                       .param("dias", String.valueOf(dias))
                       .contentType(MediaType.APPLICATION_JSON)
       ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ERRO - requisição GET para previsão do tempo dos próximos dias.")
    void weatherWeekErro() throws Exception {

        String cidade = "cidade inexistente";
        Integer dias = 7;

        given(service.weatherWeek(cidade, dias))
                .willThrow(new NotFoundException("Cidade não encontrada."));

        mockMvc.perform(
                get("/api/clima")
                        .param("cidade", cidade)
                        .param("dias", String.valueOf(dias))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("OK - requisição GET para previsão do tempo atual.")
    void weatherNowOk() throws Exception {

        String cidade = "Santo André";

        given(service.weatherNow(cidade))
                .willReturn(weatherNowDto);

        mockMvc.perform(
                get("/api/clima/now")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("ERRO - requisição GET para previsão do tempo atual.")
    void weatherNowErro() throws Exception {

        String cidade = "Santo André";

        given(service.weatherNow(cidade))
                .willThrow(new NotFoundException("Cidade não encontrada."));

        mockMvc.perform(
                get("/api/clima/now")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("OK - requisição GET para pegar coordenada da cidade.")
    void getCoordinationOk() throws Exception {

        String cidade = "Tokyo";

        given(service.getCoordination(cidade))
                .willReturn(List.of(coordinationDto));

        mockMvc.perform(
                get("/api/clima/coordination")
                        .param("cidade", cidade)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ERRO - requisição GET para pegar coordenada da cidade.")
    void getCoordinationErro() throws Exception {

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
    @DisplayName("OK - requisição findAll previsão do tempo geral.")
    void findAllOk() throws Exception {

        given(service.findAll())
                .willReturn(List.of(WeatherDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ERRO - requisição findAll previsão do tempo geral.")
    void findAllErro() throws Exception {

        given(service.findAll())
                .willThrow(new NotFoundException("Nenhum dado foi encontrado."));

        mockMvc.perform(
                get("/api/clima/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Ok - requisição findAll previsão do tempo atual.")
    void findAllNowOk() throws Exception {

        given(service.findAllNow())
                .willReturn(List.of(WeatherNowDto.builder().build()));

        mockMvc.perform(
                get("/api/clima/all/now")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ERRO - requisição findAll previsão do tempo atual.")
    void findAllNowErro() throws Exception {

        given(service.findAllNow())
                .willThrow(new NotFoundException("Nenhum dado foi encontrado."));

        mockMvc.perform(
                get("/api/clima/all/now")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}