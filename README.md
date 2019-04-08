# Saksbehandlerinnstillinger for modiapersonoversikt
En tjeneste for å ta vare på saksbehandlerinnstillinger for modiapersonoversikt.

## Kjøre lokal
`LocalRun.kt` inneholder main-metode for kjøring lokalt

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan rettes mot:

-   Daniel Winsvold, daniel.winsvold@nav.no
-   Jan-Eirik B. Nævdal, jan.eirik.b.navdal@nav.no
-   Jørund Amsen, jorund.amsen@nav.no
-   Richard Borge, richard.borge@nav.no

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #personoversikt-intern.

## Oppskrift for oppsett av nytt prosjekt
Siden dette er en rimelig basic applikasjon er den et fint utgangspunkt for andre applikasjoner, så legger en oppskrift for å sette opp her.

### Sette opp circleci
 * Logg inn på circleci.com med github-kontoen din og gå til Apps
 * Gå inn på Add Projects og finn ditt github repo der
 * Klikk på Set Up Project
 * Følg oppskriften for å starte å bygge
 * Legg til environmentvariabel PERSONOVERSIKTCI_KEY. Denne kan fåes av Richard, som sitter på den private nøkkelen. Denne må legges inn på en litt spesiell måte. Fordi circleci stripper linjeskift, må man legge inn sertifikatet med en erstatting for linjeskift og så endre dette når man leser inn.
 * Legg til environmentvariabel DOCKER_USERNAME med verdi `navikt`
 * Legg til environmentvariabel DOCKER_PASSWORD. Få verdi av noen andre, Richard for eksempel
 * Utover det, sjekk det som står . `.circleci/config.yml` for inspirasjon

### Sette opp naiserator
 * Er dokumentert [her](https://github.com/nais/doc/tree/master/content/deploy)

### Sette opp github deploy
 * Er dokumentert [her](https://github.com/navikt/deployment)

### Sette opp vault
 * Er dokumentert [her](https://github.com/nais/doc/tree/master/content/secrets)
