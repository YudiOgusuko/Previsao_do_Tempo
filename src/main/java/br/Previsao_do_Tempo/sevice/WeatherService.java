package br.Previsao_do_Tempo.sevice;

import br.Previsao_do_Tempo.dto.*;
import br.Previsao_do_Tempo.handler.exception.BadRequestException;
import br.Previsao_do_Tempo.handler.exception.NotFoundException;
import br.Previsao_do_Tempo.model.Weather;
import br.Previsao_do_Tempo.model.WeatherNow;
import br.Previsao_do_Tempo.repository.IWeatherNowRepository;
import br.Previsao_do_Tempo.repository.IWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.api.key}")
    private String apikey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.url_now}")
    private String apiUrlNow;

    private Locale locale = new Locale("pt", "BR");

    private final IWeatherRepository weatherRepository;
    private final IWeatherNowRepository weatherNowRepository;
    private final RestTemplate restTemplate;


    public List<WeatherDto> buscarPrevisaoPorCidade(String cidade) {

        try {
            String url = String.format(apiUrl+ "%s&q=%s&days=7&lang=pt", apikey, cidade);

            DadosWeather api = restTemplate.getForObject(url, DadosWeather.class);

            if (api == null || api.foreCast() == null) {
                throw new NotFoundException("Nenhum dado encontrado para a cidade: " + cidade);
            }

            List<WeatherDto> weatherDtoList = new ArrayList<>();

            Double tempMaxima = 0.0;
            Double tempMinima = 0.0;
            String descricao = null;
            Integer umidade = 0;
            LocalDate data = null;
            for (DadosForeCastDay dadosForeCast : api.foreCast().dadosForeCastDay()) {
                tempMaxima = dadosForeCast.day().temperatura_maxima();
                tempMinima = dadosForeCast.day().temperatura_minima();
                descricao = dadosForeCast.day().condition().descricao();
                umidade = dadosForeCast.day().umidade();
                data = dadosForeCast.data();

                weatherDtoList.add(WeatherDto.builder()
                        .cidade(api.location().cidade())
                        .regiao(api.location().regiao())
                        .pais(api.location().pais())
                        .temperaturaMaxima(tempMaxima)
                        .temperaturaMinima(tempMinima)
                        .umidade(umidade)
                        .descricao(descricao)
                        .data(data)
                        .diaDaSemana(data.getDayOfWeek().getDisplayName(TextStyle.FULL, locale))
                        .build());

            }

            List<Weather> weatherList = new ArrayList<>();
            weatherDtoList.forEach(x -> weatherList.add(new Weather(x)));

            weatherRepository.saveAll(weatherList);
            return weatherDtoList;

        } catch (HttpClientErrorException.BadRequest e) {
            throw new NotFoundException("Cidade não encontrada pela WeatherAPI.");
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Erro ao consultar serviço de clima: " + e.getResponseBodyAsString());
        }
    }


    public List<WeatherDto> findAll() {
        return weatherRepository.findAll().stream().map(x -> new WeatherDto(
                x.getCidade(), x.getRegiao(), x.getPais(),
                x.getTemperaturaMaxima(), x.getTemperaturaMinima(),
                x.getUmidade(), x.getDescricao(), x.getData(), x.getDiaDaSemana()))
                .toList();
    }

    public WeatherNowDto weatherNow(String cidade) {
        String url = String.format(apiUrlNow + cidade + "&lang=pt");

        DadosWeatherNow api = restTemplate.getForObject(url, DadosWeatherNow.class);

        if(api == null) {
            throw new NotFoundException("Nenhum dado encontrado para a cidade: " + cidade);
        }

        WeatherNow weatherNow = WeatherNow.builder()
                .cidade(api.location().cidade())
                .regiao(api.location().regiao())
                .pais(api.location().pais())
                .data(api.location().dataEHorario())
                .temperaturaAtual(api.current().temperaturaAtual())
                .descricao(api.current().condition().descricao())
                .umidade(api.current().umidade())
                .diaDaSemana(api.location().dataEHorario().getDayOfWeek().getDisplayName(TextStyle.FULL, locale))
                .build();

        weatherNowRepository.save(weatherNow);

        return WeatherNowDto.builder()
                .cidade(weatherNow.getCidade())
                .regiao(weatherNow.getRegiao())
                .pais(weatherNow.getPais())
                .data(weatherNow.getData())
                .temperaturaAtual(weatherNow.getTemperaturaAtual())
                .descricao(weatherNow.getDescricao())
                .umidade(weatherNow.getUmidade())
                .diaDaSemana(weatherNow.getDiaDaSemana())
                .build();
    }
}
