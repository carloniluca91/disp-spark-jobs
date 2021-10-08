package it.luca.disp.streaming.model.jarvis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class JarvisCiclo {

    public static final String CICLO_DI_RIFERIMENTO = "cicloDiRiferimento";
    public static final String RINOMINA_ENERGIA = "rinominaEnergia";
    public static final String UNITA_DI_MISURA_RINOMINA_ENERGIA = "unitaDiMisuraRinominaEnergia";
    public static final String LIMITE_MINIMO_ENERGIA = "limiteMinimoEnergia";
    public static final String UNITA_DI_MISURA_LIMITE_MINIMO_ENERGIA = "unitaDiMisuraLimiteMinimoEnergia";
    public static final String LIMITE_MASSIMO_ENERGIA = "limiteMassimoEnergia";
    public static final String UNITA_DI_MISURA_LIMITE_MASSIMO_ENERGIA = "unitaDiMisuraLimiteMassimoEnergia";

    private final String cicloDiRiferimento;
    private final Double rinominaEnergia;
    private final String unitaDiMisuraRinominaEnergia;
    private final Double limiteMinimoEnergia;
    private final String unitaDiMisuraLimiteMinimoEnergia;
    private final Double limiteMassimoEnergia;
    private final String unitaDiMisuraLimiteMassimoEnergia;

    @JsonCreator
    public JarvisCiclo(@JsonProperty(CICLO_DI_RIFERIMENTO) String cicloDiRiferimento,
                       @JsonProperty(RINOMINA_ENERGIA) Double rinominaEnergia,
                       @JsonProperty(UNITA_DI_MISURA_RINOMINA_ENERGIA) String unitaDiMisuraRinominaEnergia,
                       @JsonProperty(LIMITE_MINIMO_ENERGIA) Double limiteMinimoEnergia,
                       @JsonProperty(UNITA_DI_MISURA_LIMITE_MINIMO_ENERGIA) String unitaDiMisuraLimiteMinimoEnergia,
                       @JsonProperty(LIMITE_MASSIMO_ENERGIA) Double limiteMassimoEnergia,
                       @JsonProperty(UNITA_DI_MISURA_LIMITE_MASSIMO_ENERGIA) String unitaDiMisuraLimiteMassimoEnergia) {

        this.cicloDiRiferimento = cicloDiRiferimento;
        this.rinominaEnergia = rinominaEnergia;
        this.unitaDiMisuraRinominaEnergia = unitaDiMisuraRinominaEnergia;
        this.limiteMinimoEnergia = limiteMinimoEnergia;
        this.unitaDiMisuraLimiteMinimoEnergia = unitaDiMisuraLimiteMinimoEnergia;
        this.limiteMassimoEnergia = limiteMassimoEnergia;
        this.unitaDiMisuraLimiteMassimoEnergia = unitaDiMisuraLimiteMassimoEnergia;
    }
}
