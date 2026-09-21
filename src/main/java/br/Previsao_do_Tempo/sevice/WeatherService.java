package br.Previsao_do_Tempo.sevice;

import br.Previsao_do_Tempo.dto.CoordinationDto;
import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.dto.WeatherNowDto;
import br.Previsao_do_Tempo.dto.dadosCoordination.DadosCoordination;
import br.Previsao_do_Tempo.dto.dadosWeather.DadosForeCastDay;
import br.Previsao_do_Tempo.dto.dadosWeather.DadosWeather;
import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosWeatherNow;
import br.Previsao_do_Tempo.handler.exception.BadRequestException;
import br.Previsao_do_Tempo.handler.exception.NotFoundException;
import br.Previsao_do_Tempo.model.Weather;
import br.Previsao_do_Tempo.model.WeatherNow;
import br.Previsao_do_Tempo.repository.IWeatherNowRepository;
import br.Previsao_do_Tempo.repository.IWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.url_now}")
    private String apiUrlNow;

    @Value("${weather.api.url_coordination}")
    private String apiUrlCoordination;

    private Locale locale = new Locale("pt", "BR");

    private final IWeatherRepository weatherRepository;
    private final IWeatherNowRepository weatherNowRepository;
    private final RestTemplate restTemplate;


    @Transactional
    public List<WeatherDto> weatherWeek(String cidade, Integer dias) {

        String cidadeFormatoBanco = cidade.toLowerCase().replace("_", " ").trim();
        String cidadeFormatada = cidade.toLowerCase().replace(" ", "_").trim();
        LocalDate hoje = LocalDate.now();
        LocalDate ultimoDiaPrevisao = hoje.plusDays(dias);

        List<WeatherDto> weatherDto = weatherRepository.findAllByCidadeIgnoreCase(cidadeFormatoBanco);

        boolean possuiPrevisaoCompleta = weatherDto.stream().anyMatch(d -> d.data().isEqual(ultimoDiaPrevisao));

        if(!weatherDto.isEmpty() && possuiPrevisaoCompleta) {
            return weatherDto.stream()
                    .filter(d -> !d.data().isBefore(hoje))
                    .toList();
        }

        try {
            String urlApi = String.format("%s%s&q=%s&days=%d&lang=pt", apiUrl, apiKey, cidadeFormatada, dias);
            DadosWeather apiWeather = restTemplate.getForObject(urlApi, DadosWeather.class);

            if (apiWeather == null || apiWeather.foreCast() == null) {throw new NotFoundException("Nenhum dado encontrado para a cidade: " + cidade);}

            List<WeatherDto> weatherDtoList = new ArrayList<>();

            for (DadosForeCastDay dadosForeCast : apiWeather.foreCast().dadosForeCastDay()) {
                LocalDate data = dadosForeCast.data();

                weatherDtoList.add(WeatherDto.builder()
                                .cidade(apiWeather.location().cidade())
                                .regiao(apiWeather.location().regiao())
                                .pais(apiWeather.location().pais())
                                .temperaturaMaxima(dadosForeCast.day().temperatura_maxima())
                                .temperaturaMinima(dadosForeCast.day().temperatura_minima())
                                .umidade(dadosForeCast.day().umidade())
                                .descricao(dadosForeCast.day().condition().descricao())
                                .data(data)
                                .diaDaSemana(data.getDayOfWeek().getDisplayName(TextStyle.FULL, locale))
                                .build());
            }

            weatherRepository.deleteByCidadeIgnoreCase(cidadeFormatoBanco);
            weatherRepository.saveAll(weatherDtoList.stream().map(Weather::new).toList());

            return weatherDtoList;
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new NotFoundException("Cidade não encontrada pela WeatherAPI.");
        }
        catch (HttpClientErrorException e) {
            throw new BadRequestException("Erro ao consultar serviço de clima: " + e.getResponseBodyAsString());
        }
    }

    @Transactional
    public WeatherNowDto weatherNow(String cidade) {

        String cidadeFormatada = cidade.toLowerCase().replace(" ", "_").trim();
        String cidadeFormatoBanco = cidade.toLowerCase().replace("_", " ").trim();

        Optional<WeatherNow> weatherNowOptional = weatherNowRepository.findByCidadeIgnoreCase(cidadeFormatoBanco);

        Instant umaHoraAtras = Instant.now().minus(1, ChronoUnit.HOURS);

        if(weatherNowOptional.isPresent()) {
            WeatherNow weatherNow = weatherNowOptional.get();
            if(weatherNow.getData().isAfter(umaHoraAtras)) {
                return new WeatherNowDto(weatherNow);
            }
        }

        try {
            String url = String.format("%s%s&lang=pt", apiUrlNow, cidadeFormatada);
            DadosWeatherNow api = restTemplate.getForObject(url, DadosWeatherNow.class);

            if(api == null) {
                throw new NotFoundException("Nenhum dado encontrado para a cidade: " + cidadeFormatada);
            }

            String diaDaSemana = api.location().dataEHorario().getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

            WeatherNow weatherNow = weatherNowOptional.orElseGet(WeatherNow::new);
            weatherNow.atualizarDados(api, diaDaSemana);

            weatherNowRepository.save(weatherNow);

            return new WeatherNowDto(weatherNow);
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new NotFoundException("Cidade não encontrada pela WeatherAPI.");
        }
        catch (HttpClientErrorException e) {
            throw new BadRequestException("Erro ao consultar serviço de clima: " + e.getResponseBodyAsString());
        }
    }

    public List<CoordinationDto> getCoordination(String cidade) {

        String cidadeFormatada = cidade.toLowerCase().replace(" ", "_").trim();

        String url = apiUrlCoordination + cidadeFormatada;
        DadosCoordination[] coordination = restTemplate.getForObject(url, DadosCoordination[].class);

        if (coordination == null) {
            throw new NotFoundException("Nenhum dado encontrado para a cidade: " + cidade);
        }

        List<DadosCoordination> dadosCoordinationList = Arrays.asList(coordination);

        return dadosCoordinationList.stream().map(CoordinationDto::new).toList();
    }

    public List<WeatherDto> findAll() {
        return weatherRepository.findAll().stream().map(WeatherDto::new).toList();
    }

    public List<WeatherNowDto> findAllNow() {
        return weatherNowRepository.findAll().stream().map(WeatherNowDto::new).toList();
    }

}
