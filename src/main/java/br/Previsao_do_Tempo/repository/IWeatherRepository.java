package br.Previsao_do_Tempo.repository;

import br.Previsao_do_Tempo.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWeatherRepository extends JpaRepository<Weather, Long> {
}
