package br.Previsao_do_Tempo.repository;

import br.Previsao_do_Tempo.model.WeatherNow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWeatherNowRepository extends JpaRepository<WeatherNow, Long> {
}
