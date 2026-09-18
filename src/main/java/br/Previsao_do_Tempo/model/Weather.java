package br.Previsao_do_Tempo.model;

import br.Previsao_do_Tempo.dto.WeatherDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_weather")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String cidade;

    @NonNull
    private String regiao;

    @NonNull
    private String pais;

    @NonNull
    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;

    @NonNull
    @Column(name = "temperatura_minima")
    private Double temperaturaMinima;

    @NonNull
    private Integer umidade;

    @NonNull
    private String descricao;

    @NonNull
    private LocalDate data;

    @NonNull
    private String diaDaSemana;

    public Weather(WeatherDto weatherDto) {
        this.cidade = weatherDto.cidade();
        this.regiao = weatherDto.regiao();
        this.pais = weatherDto.pais();
        this.temperaturaMaxima = weatherDto.temperaturaMaxima();
        this.temperaturaMinima = weatherDto.temperaturaMinima();
        this.umidade = weatherDto.umidade();
        this.descricao = weatherDto.descricao();
        this.data = weatherDto.data();
        this.diaDaSemana = weatherDto.diaDaSemana();
    }
}