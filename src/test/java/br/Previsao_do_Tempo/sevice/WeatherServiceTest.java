package br.Previsao_do_Tempo.sevice;

import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.dto.dadosWeather.*;
import br.Previsao_do_Tempo.handler.exception.NotFoundException;
import br.Previsao_do_Tempo.model.Weather;
import br.Previsao_do_Tempo.model.WeatherNow;
import br.Previsao_do_Tempo.repository.IWeatherNowRepository;
import br.Previsao_do_Tempo.repository.IWeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService service;

    @Mock
    private IWeatherRepository iWeatherRepository;

    @Mock
    private IWeatherNowRepository iWeatherNowRepository;

    @Mock
    private RestTemplate restTemplate;

    private final String cidade = "Sao Paulo";
    private final Integer dias = 7;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "apiUrl", "${weather.api.url}");
        ReflectionTestUtils.setField(service, "apiKey", "${weather.api.key}");
        ReflectionTestUtils.setField(service, "locale", new Locale("pt", "Br"));
    }

    @Test
    @DisplayName("Deve retornar do Banco de não chamar a API.")
    void weatherWeek_retornarDoBanco() {
        LocalDate hoje = LocalDate.now();
        LocalDate diaLimite = hoje.plusDays(dias);

        WeatherDto dtoExistente = WeatherDto.builder()
                .cidade(cidade)
                .data(diaLimite)
                .build();

        given(iWeatherRepository.findAllByCidadeIgnoreCase(anyString()))
                .willReturn(List.of(dtoExistente));

        List<WeatherDto> lista = service.weatherWeek(cidade, dias);

        assertThat(lista.isEmpty()).isFalse();
        then(restTemplate).should(never()).getForObject(anyString(), any());
        then(iWeatherRepository).should(never()).deleteByCidadeIgnoreCase(anyString());

    }

    @Test
    @DisplayName("Deve buscar na API e salvar no Banco quando não tem a previsão completa.")
    void weatherWeek_buscarNaAPI_semPrevisaoCompleta() {
        given(iWeatherRepository.findAllByCidadeIgnoreCase(anyString()))
                .willReturn(List.of());

        DadosCondition condition = new DadosCondition("Ensolarado");
        DadosDay day = new DadosDay(30.0, 20.0, 60, condition);
        DadosForeCastDay dadosForeCastDay = new DadosForeCastDay(LocalDate.now(), day);
        DadosForeCast foreCast = new DadosForeCast(List.of(dadosForeCastDay));
        DadosLocation location = new DadosLocation(cidade, "SP", "Brasil");

        DadosWeather dadosWeather = new DadosWeather(location, foreCast);

        given(restTemplate.getForObject(anyString(), eq(DadosWeather.class)))
                .willReturn(dadosWeather);

        List<WeatherDto> list = service.weatherWeek(cidade, dias);

        assertThat(list.isEmpty()).isFalse();
        then(iWeatherRepository).should().deleteByCidadeIgnoreCase(anyString());
        then(iWeatherRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("Deve lançar exceção NotFoundException qunado a API não retornar dados.")
    void weatherWeek_lancarExcecao() {
        given(iWeatherRepository.findAllByCidadeIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), any()))
                .willReturn(null);

        assertThrows(NotFoundException.class, () -> service.weatherWeek(cidade, dias));
    }

    @Test
    void weatherNow() {
    }

    @Test
    void getCoordination() {
    }

    @Test
    @DisplayName("OK - findAll para previsão do tempo geral.")
    void findAllOk() {

        given(iWeatherRepository.findAll())
                .willReturn(List.of(Weather.builder().build()));

        List<WeatherDto> list = service.findAll();
        assertThat(list.isEmpty()).isFalse();
        then(iWeatherRepository).should().findAll();

    }

    @Test
    @DisplayName("ERRO - findAll para previsão do tempo geral.")
    void findAllErro() {

        given(iWeatherRepository.findAll())
                .willReturn(List.of());

        List<WeatherDto> list = service.findAll();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("OK - findAll para previsão do tempo atual.")
    void findAllNowOk() {

        given(iWeatherNowRepository.findAll())
                .willReturn(List.of(WeatherNow.builder().build()));

        List<WeatherNowDto> list = service.findAllNow();
        assertThat(list.isEmpty()).isFalse();
        then(iWeatherNowRepository).should().findAll();
    }

    @Test
    @DisplayName("ERRO - findAll para previsão do tempo atual.")
    void findAllNowErro() {

        given(iWeatherNowRepository.findAll())
                .willReturn(List.of());

        List<WeatherNowDto> list = service.findAllNow();
        assertThat(list.isEmpty()).isTrue();
    }
}