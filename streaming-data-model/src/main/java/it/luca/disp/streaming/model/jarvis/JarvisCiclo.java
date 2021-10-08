package it.luca.disp.streaming.model.jarvis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class JarvisCiclo {

    private final String cicloDiRiferimento;
    private final Double rinominaEnergia;
    private final String unitaDiMisuraRinominaEnergia;
    private final Double limiteMinimoEnergia;
    private final String unitaDiMisuraLimiteMinimoEnergia;
    private final Double limiteMassimoEnergia;
    private final String unitaDiMisuraLimiteMassimoEnergia;

    @JsonCreator
    public JarvisCiclo(@JsonProperty("cicloDiRiferimento") String cicloDiRiferimento,
                       @JsonProperty("rinominaEnergia") Double rinominaEnergia,
                       @JsonProperty("unitaDiMisuraRinominaEnergia") String unitaDiMisuraRinominaEnergia,
                       @JsonProperty("limiteMinimoEnergia") Double limiteMinimoEnergia,
                       @JsonProperty("unitaDiMisuraLimiteMinimoEnergia") String unitaDiMisuraLimiteMinimoEnergia,
                       @JsonProperty("limiteMassimoEnergia") Double limiteMassimoEnergia,
                       @JsonProperty("unitaDiMisuraLimiteMassimoEnergia") String unitaDiMisuraLimiteMassimoEnergia) {

        this.cicloDiRiferimento = cicloDiRiferimento;
        this.rinominaEnergia = rinominaEnergia;
        this.unitaDiMisuraRinominaEnergia = unitaDiMisuraRinominaEnergia;
        this.limiteMinimoEnergia = limiteMinimoEnergia;
        this.unitaDiMisuraLimiteMinimoEnergia = unitaDiMisuraLimiteMinimoEnergia;
        this.limiteMassimoEnergia = limiteMassimoEnergia;
        this.unitaDiMisuraLimiteMassimoEnergia = unitaDiMisuraLimiteMassimoEnergia;
    }
}
