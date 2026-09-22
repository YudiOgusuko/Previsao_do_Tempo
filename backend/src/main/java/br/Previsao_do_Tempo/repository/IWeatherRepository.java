package br.Previsao_do_Tempo.repository;

import br.Previsao_do_Tempo.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWeatherRepository extends JpaRepository<Weather, Long> {

    List<Weather> findAllByCidadeAndRegiaoAndPaisIgnoreCase(String cidadeFormatada, String regiaoFormatada, String paisFormatado);

    void deleteByCidadeIgnoreCase(String cidadeFormatoBanco);

}
