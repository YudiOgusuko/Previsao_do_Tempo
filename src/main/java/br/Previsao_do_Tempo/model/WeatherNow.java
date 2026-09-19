package br.Previsao_do_Tempo.model;

import br.Previsao_do_Tempo.dto.dadosWeatherNow.DadosWeatherNow;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.ZoneId;

@Entity
@Table(name = "tb_weatherNow")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherNow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cidade;
    private String regiao;
    private String pais;
    private Double temperaturaAtual;
    private String descricao;
    private Integer umidade;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Instant data;

    private String diaDaSemana;

    public void atualizarDados(DadosWeatherNow api, String diaDaSemana) {
        setCidade(api.location().cidade());
        setRegiao(api.location().regiao());
        setPais(api.location().pais());
        setTemperaturaAtual(api.current().temperaturaAtual());
        setDescricao(api.current().condition().descricao());
        setUmidade(api.current().umidade());
        setData(api.location().dataEHorario().atZone(ZoneId.systemDefault()).toInstant());
        setDiaDaSemana(diaDaSemana);
    }
}
