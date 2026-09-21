package br.Previsao_do_Tempo.repository;

import br.Previsao_do_Tempo.dto.WeatherDto;
import br.Previsao_do_Tempo.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWeatherRepository extends JpaRepository<Weather, Long> {
    List<WeatherDto> findAllByCidadeIgnoreCase(String cidade);

    void deleteByCidadeIgnoreCase(String cidadeFormatoBanco);
}
