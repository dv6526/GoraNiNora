
![icongora](https://user-images.githubusercontent.com/72414871/186887622-9a965cb7-38e1-4f09-a7e8-d9e13c691494.png)
# GoraNiNora

**GoraNiNora** je aplikacija za mobilne naprave Android in je bila razvita v okviru diplomske naloge. Namenjena je vsem, ki se pozimi odpravljajo v gore. 
Glavni namen aplikacije je pošiljanje in prikaz kontekstno odvisnih sporočil. 

## Kako deluje?
Aplikacija za zaznavanje konteksta uporabi **lokacijo** uporabnika ter trenutno **vreme** in **plazovni bilten**.
Za pridobivanje podrobnosti o nadmorski višini, naklonu in usmerjenosti pobočja uporablja [ArcGis API](https://developers.arcgis.com/rest/elevation/api-reference/summarize-elevation.htm),
vremensko napoved pridobi od [ARSO vreme](https://meteo.arso.gov.si/uploads/probase/www/fproduct/text/sl/forecast_si-upperAir-new_latest.xml) in plazovni bilten od 
[ARSO plazovi](https://vreme.arso.gov.si/api/1.0/avalanche_bulletin/).

Spodnja tabela prikazuje podatke, ki so na voljo za zaznavanje konteksta.

| Podatek      | Opis                                                        |
|--------------|------------------------------------------------------------|
| Nadmorska višina | Višina v m nad morjem                                                |
| Lokacija     | Območje, kjer se nahajamo                                            |
| Telesna dejavnost uporabnika | Telesna dejavnost, ki jo uporabnik izvaja: hoja ali mirovanje |
| Naklon       | Naklon pobočja v stopinjah &deg;                                   |
| Ekspozicija  | Usmerjenost pobočja v stopinjah (0&deg; do 360&deg;) |
| Temperatura   | Temperatura odvisna od nadmorske višine                             |
| Veter         | Hitrost vetra odvisna od nadmorske višine                        |
| Vreme         | Oblačnost, vremenski pojav in intenzivnost padavin            |
| Problem       | Plazovni problem v snežni odeji [(vsi možni plazovni problemi)](https://vreme.arso.gov.si/api/1.0/avalanche_problems/)                                    |
| Vzorec        | Značilni plazovni vzorci [(vsi možni plazovni vzorci)](https://vreme.arso.gov.si/api/1.0/avalanche_patterns/)                                         |
| Nevarnost     | Stopnja plazovne nevarnosti od 1 do 5   


## Pravila
Pravila opisujejo, kateri pogoji morajo veljati, da uporabniku aplikacija prikaže določeno opozorilo. Pravila so določena v mapi ```Assets``` v datoteki ```rules.json```.
```yaml
{
    "aspect": "<Usmerjenost pobočja>",
    "min_slope": "<Najmanjši naklon>",
    "max_slope": "<Največji naklon>",
    "elevation_min": "<Najmanjša nadmorska višina>",
    "elevation_max": "<Največja nadmorska višina>",
    "hour_min": "<Ura med hour_min in hour_max>",
    "hour_max": "<Ura med hour_min in hour_max>",
    "user_hiking": "<True označuje trenutno opozorilo, false označuje splošno opozorilo.>",
    "av_area_id": "<ID območja>",
    "notification_name": "<Ime opozorila>",
    "notification_text": "<Opis opozorila>",
    "weather_descriptions": [
      {
        "day_delay": "<Zamik v dnevih za vremensko napoved>",
        "temp_avg_min": "<Spodnja meja za povprečno temperaturo>",
        "temp_avg_max": "<Zgornja meja za povprečno temperaturo>",
        "hour_min": "<Vremenska napoved od ure hour_min do hour_max>",
        "hour_max": "<Vremenska napoved od ure hour_min do hour_max>",
        "oblacnost": "<Vrsta oblačnosti>",
        "vremenski_pojav": "<Vremenski pojav>",
        "intenzivnost": "<Intenzivnost padavin>",
        "elevation": "<Temperatura na nadmorski višini>"
      }
    ],
    "problems": [
      {
        "problem_id": "<ID problema>",
        "day_delay": "<Zamik v dnevih za plazovni problem, ki mora biti prisoten>",
        "hour_min": "<Plazovni problem prisoten od ure hour_min do hour_max>",
        "hour_max": "<Plazovni problem prisoten od ure hour_min do hour_max>",
        "check_elevation": "<Preveri, če se uporabnik nahaja na nadmorski višini, na kateri je prisoten plazovni problem>"
      }
    ],
    "patterns":[
      {
         "pattern_id": "<ID plazovnega vzorca>",
         "day_delay": "<Zamik v dnevih za plazovni vzorec, ki mora biti prisoten>",
         "hour_min": "<Plazovni vzorec prisoten od ure hour_min do hour_max>",
         "hour_max": "<Plazovni vzorec prisoten od ure hour_min do hour_max>",
         "check_elevation": "<Preveri, če se uporabnik nahaja na nadmorski višini, na kateri je prisoten plazovni vzorec>"
      }
    ],
    "dangers": [
      {
         "value": "<Stopnja plazovne nevarnosti>",
         "day_delay": "<Zamik v dnevih od trenutnega dne za plazovno nevarnost, ki mora biti prisotna.>",
         "am": "<True dopoldne oz. false popoldne.>"
      }
    ]
  }

```


## Samodejno pošiljanje opozoril
Aplikacija s pomočjo senzorjev samodejno zazna hojo, in v primeru, da se uporabnik nahaja v hribih, vklopi pošiljanje trenutnih opozoril. Če aplikacija samodejno ne zazna hoje v hribe, lahko uporabnik še vedno ročno vključi pošiljanje trenutnih opozoril.

## Razlika med splošnimi in trenutnimi opozorili?
S splošnimi opozorili želimo uporabnika vnaprej opozoriti na različne situacije v gorah. Trenutna opozorila pa so namenjena, da se uporabniku prikažejo, ko se ta nahaja v hribih in je aplikacija zaznala nevarno situacijo.

## Uporabniški vmesnik
1. ```Zgodovina```
    * omogoča pregled nad opozorili starejšimi od enega dne
    
2. ```Opozorila```
    * omogoča pregled trenutnih in splošnih opozoril trenutnega dne
    * filtriranje splošnih opozoril glede na izbrano območje
    * vklop in izkop hoje v hribe
    * klik na opozorilo odpre okno, kjer je bolj podrobno opisano opozorilo
    
3. ```Nastavitve```
    * omogoča vklop in izklop varčevanja z energijo
    * omogoča potrditev dovoljenj
<img width="1194" alt="screenshot" src="https://user-images.githubusercontent.com/72414871/186887176-42b01417-470a-4ede-b5ef-31690f3f152f.png">
