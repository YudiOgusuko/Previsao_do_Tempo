package br.Previsao_do_Tempo.repository;

import br.Previsao_do_Tempo.model.WeatherNow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IWeatherNowRepository extends JpaRepository<WeatherNow, Long> {
    Optional<WeatherNow> findByCidadeIgnoreCase(String cidade);

}
