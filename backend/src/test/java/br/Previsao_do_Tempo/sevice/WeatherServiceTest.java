package br.Previsao_do_Tempo.sevice;

import br.Previsao_do_Tempo.dto.CoordinationDto;
import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.dto.dadosCoordination.DadosCoordination;
import br.Previsao_do_Tempo.dto.dadosWeather.*;
import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosConditionNow;
import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosCurrentNow;
import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosLocationNow;
import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosWeatherNow;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        ReflectionTestUtils.setField(service, "apiUrlNow", "${weather.api.url_now}");
        ReflectionTestUtils.setField(service,  "apiUrlCoordination", "${weather.api.url_coordination}");
        ReflectionTestUtils.setField(service, "locale", new Locale("pt", "Br"));
    }

    @Test
    @DisplayName("Clima Geral - Deve retornar do Banco de não chamar a API.")
    void weatherWeek_Banco() {
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
    @DisplayName("Clima Geral - Deve buscar na API e salvar no Banco quando não tem a previsão completa.")
    void weatherWeek_API() {
        given(iWeatherRepository.findAllByCidadeIgnoreCase(anyString()))
                .willReturn(List.of());

        DadosCondition condition = DadosCondition.builder().descricao("Ensolarado").build();
        DadosDay day = DadosDay.builder().temperatura_maxima(30.0).temperatura_minima(20.0).umidade(60).condition(condition).build();
        DadosForeCastDay dadosForeCastDay = DadosForeCastDay.builder().data(LocalDate.now()).day(day).build();
        DadosForeCast foreCast = DadosForeCast.builder().dadosForeCastDay(List.of(dadosForeCastDay)).build();
        DadosLocation location = DadosLocation.builder().cidade(cidade).regiao("SP").pais("Brasil").build();

        DadosWeather dadosWeather = DadosWeather.builder().location(location).foreCast(foreCast).build();

        given(restTemplate.getForObject(anyString(), eq(DadosWeather.class)))
                .willReturn(dadosWeather);

        List<WeatherDto> list = service.weatherWeek(cidade, dias);

        assertThat(list.isEmpty()).isFalse();
        then(iWeatherRepository).should().deleteByCidadeIgnoreCase(anyString());
        then(iWeatherRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("Clima Geral - Deve lançar exceção NotFoundException quando a API não retornar dados.")
    void weatherWeek_Exception() {
        given(iWeatherRepository.findAllByCidadeIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), any()))
                .willReturn(null);

        assertThrows(NotFoundException.class, () -> service.weatherWeek(cidade, dias));
    }

    @Test
    @DisplayName("Clima Atual - Deve ser retornado do Banco.")
    void weatherNow_Banco() {

        WeatherNow weatherNow = WeatherNow.builder()
                .cidade("Santo André")
                .regiao("Sao Paulo")
                .pais("Brazil")
                .data(Instant.now())
                .build();

        given(iWeatherNowRepository.findByCidadeIgnoreCase(anyString()))
                .willReturn(Optional.of(weatherNow));

        WeatherNowDto weatherNowDto = service.weatherNow(cidade);

        assertNotNull(weatherNowDto);
        then(iWeatherNowRepository).should().findByCidadeIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Clima Atual - Deve ser retornado da API.")
    void weatherNow_API() {

        DadosConditionNow condition = DadosConditionNow.builder().descricao("Encoberto").build();
        DadosCurrentNow current = DadosCurrentNow.builder().temperaturaAtual(25.5).condition(condition).umidade(64).build();
        DadosLocationNow location = DadosLocationNow.builder().cidade("Santo André").regiao("Sao Paulo").pais("Brazil").dataEHorario(LocalDateTime.now()).build();

        DadosWeatherNow dadosWeatherNow = DadosWeatherNow.builder().location(location).current(current).build();

        given(iWeatherNowRepository.findByCidadeIgnoreCase(anyString()))
                .willReturn(Optional.empty());

        given(restTemplate.getForObject(anyString(), eq(DadosWeatherNow.class)))
                .willReturn(dadosWeatherNow);

        WeatherNowDto weatherNowDto = service.weatherNow(cidade);

        assertNotNull(weatherNowDto);
        then(restTemplate).should().getForObject(anyString(), eq(DadosWeatherNow.class));
        then(iWeatherNowRepository).should().findByCidadeIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Clima Atual - Deve lançar exceção.")
    void weatherNow_Exception() {

        given(iWeatherNowRepository.findByCidadeIgnoreCase(anyString()))
                .willReturn(Optional.empty());

        given(restTemplate.getForObject(anyString(), eq(DadosWeatherNow.class)))
                .willReturn(null);

        assertThrows(NotFoundException.class, () ->  service.weatherNow(cidade));

    }

    @Test
    @DisplayName("Coordenada - Deve pegar da API.")
    void getCoordination_API() {

        DadosCoordination[] dadosCoordination = new DadosCoordination[]{new DadosCoordination("Sao Paulo", "Sao Paulo", "Brazil", -23.53, -46.62)};

        given(restTemplate.getForObject(anyString(), eq(DadosCoordination[].class)))
                .willReturn(dadosCoordination);

        List<CoordinationDto> coordinationDtoList = service.getCoordination(cidade);

        assertNotNull(coordinationDtoList);
        then(restTemplate).should().getForObject(anyString(), eq(DadosCoordination[].class));
    }

    @Test
    @DisplayName("Coordenada - Deve lançar exceção.")
    void getCoordination_Exception() {

        given(restTemplate.getForObject(anyString(), eq(DadosCoordination[].class)))
                .willReturn(null);

        assertThrows(NotFoundException.class, () -> service.getCoordination(cidade));
    }

    @Test
    @DisplayName("Clima Geral - findAll OK.")
    void findAll_Ok() {

        given(iWeatherRepository.findAll())
                .willReturn(List.of(Weather.builder().build()));

        List<WeatherDto> list = service.findAll();
        assertThat(list.isEmpty()).isFalse();
        then(iWeatherRepository).should().findAll();

    }

    @Test
    @DisplayName("Clima Geral - findAll ERRO.")
    void findAll_Erro() {

        given(iWeatherRepository.findAll())
                .willReturn(List.of());

        List<WeatherDto> list = service.findAll();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Clima Atual - findAll OK.")
    void findAllNow_Ok() {

        given(iWeatherNowRepository.findAll())
                .willReturn(List.of(WeatherNow.builder().build()));

        List<WeatherNowDto> list = service.findAllNow();
        assertThat(list.isEmpty()).isFalse();
        then(iWeatherNowRepository).should().findAll();
    }

    @Test
    @DisplayName("Clima Atual - findAll ERRO.")
    void findAllNow_Erro() {

        given(iWeatherNowRepository.findAll())
                .willReturn(List.of());

        List<WeatherNowDto> list = service.findAllNow();
        assertThat(list.isEmpty()).isTrue();
    }
}