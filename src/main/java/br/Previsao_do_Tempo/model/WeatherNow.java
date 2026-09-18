package br.Previsao_do_Tempo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime data;

    private String diaDaSemana;
}
