# Saksbehandlerinnstillinger for modiapersonoversikt
En tjeneste for å ta vare på saksbehandlerinnstillinger for modiapersonoversikt.

## Ruter
 * GET `innstillinger/{navident}`
 * POST `innstillinger/{navident}`
 * DELETE `innstillinger/{navident}`

## Format på data
Tar imot og leverer innstillinger på dette formatet:
```
{
	"innstillinger": [
		{
			"navn": "innstillingsnavn1",
			"verdi": "verdi"
		},
		{
			"navn": "innstillingsnavn2",
			"verdi": "verdi"
		}
	]
}
```

## Kjøre lokal
`LocalRun.kt` inneholder main-metode for kjøring lokalt

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan rettes mot:

[Team Personoversikt](https://github.com/navikt/info-team-personoversikt)

## Oppskrift for oppsett av nytt prosjekt
Siden dette er en rimelig basic applikasjon er den et fint utgangspunkt for andre applikasjoner, så legger en oppskrift for å sette opp her.

### Aktiver github deploy (begge disse må gjøres av en github admin)
* Legg til `personoversikt-ci` (som er vår github app) tilgang til repoet
* Legg til `NAV deployment` (github app)

### Sette opp docker repo
 * Logg deg inn på dockerhub og lag repo på navikt-organisasjonen med samme navn som github repo
 * Gi gruppen `bots` lov til å skrive og lese

### Sette opp circleci
 * Logg inn på circleci.com med github-kontoen din og gå til Apps
 * Gå inn på Add Projects og finn ditt github repo der
 * Klikk på Set Up Project
 * Følg oppskriften for å starte å bygge
 * Legg til environmentvariabel GITHUB_APP_ID = 23451 (som er vår appid) 
 * Legg til environmentvariabel GITHUB_APP_KEY_BASE64. Denne kan fåes av Richard, som sitter på den private nøkkelen. Denne må legges inn på en litt spesiell måte. Fordi circleci stripper linjeskift, må man legge inn sertifikatet med en erstatting for linjeskift og så endre dette når man leser inn.
 * Legg til environmentvariabel DOCKER_USERNAME med verdi `navikt`
 * Legg til environmentvariabel DOCKER_PASSWORD. Få verdi av noen andre, Richard for eksempel
 * Utover det, sjekk det som står . `.circleci/config.yml` for inspirasjon vi bruker per i dag deployment-cli som er skrevet av Kevin Sillerud

### Sette opp github deploy
 * Krever at applikasjonen bruker naiserator, dokumentert [her](https://github.com/nais/doc/tree/master/content/deploy)
 * Github deployment er dokumentert [her](https://github.com/navikt/deployment) (Husk å registrere team [her](https://deployment.prod-sbs.nais.io/auth/login)!)
 * Har du husket å registrere team [her](https://deployment.prod-sbs.nais.io/auth/login)?
 * Opprett en mappe `deploy` og legg filer inn der. Se i mappen i dette prosjektet for eksempel
 * Filen `deployreq.json` trenger ikke å endres. Det er bare formatet på requesten til deploy rutina, og den er felles for alle våre prosjekt
 * Filen `preprod.yaml` er naiserator-filen for preprod
 * Gjør endringer på `.circleci/config.yml` så man kan gjøre et deployment request
 * `ref` må peke på en git ref. commit sha/tag/branch navn
 * Logg for deploy jobber ligger [her](https://github.com/navikt/modiapersonoversikt-innstillinger/deployments)
 
### Sette opp vault
 * Er dokumentert [her](https://github.com/nais/doc/tree/master/content/secrets)
 * Lag en PR til vault-iac
 * Vent på godkjenning
 * Logg inn på [vault](https://vault.adeo.no)
 * Legg inn secrets der under riktig path her er det `modiapersonoversikt-innstillinger/default`
 * Secrets må legges inn som properties filer, det vil si key er filnavn og value er innholdet på properties-format (KEY=VALUE), dersom man bruker `<filename>.env` som KEY og navikt sitt java baseimage på docker vil alle env-filer sources og variablene gå rett inn i containeren 
