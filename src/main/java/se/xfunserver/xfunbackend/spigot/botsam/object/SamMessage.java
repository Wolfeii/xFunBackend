package se.xfunserver.xfunbackend.spigot.botsam.object;

import lombok.Getter;

import java.util.Random;

public enum SamMessage {

    CANNOT_DO_THAT(
            "Det finns flera anledningar till varför jag inte gör det där...",
            "Det där går inte. Dubbelkolla alltid innan du skriver detta kommando.",
            "Oops. Förlåt, jag kan inte behandla din förfrågan."
    ),

    ERROR(
            "Oh nej... Nu gick något fel",
            "Min kod gick precis sönder, bra jobbat spelare!",
            "Mitt system säger mig att något gick fel."
    ),

    NO_PERMISSIONS(
            "Förlåt, men du har inte tillgång till detta.",
            "Du har inte tillåtelse att använda detta.",
            "Det skulle vara trevligt om du hade tillåtelse, ellerhur?",
            "Inte tillgång!",
            "Kan tyvärr inte låta dig. Inte tillång!",
            "Förlåt, jag kan inte tillåta dig att använda detta.",
            "Försök igen, fast med tillgång istället!",
            "Du visste att du inte hade tillgång -_- (Inte tillåtelse)"
    );

    @Getter
    private String[] messages;

    SamMessage(String... messages) {
        this.messages = messages;
    }

    public String getRandom() {
        Random random = new Random();
        int rInt = random.nextInt(this.messages.length);
        return messages[rInt];
    }
}
