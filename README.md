
![icongora](https://user-images.githubusercontent.com/72414871/186887622-9a965cb7-38e1-4f09-a7e8-d9e13c691494.png)
# GoraNiNora

**GoraNiNora** je aplikacija za mobilne naprave Android in je bila razvita v okviru diplomske naloge. Namenjena je vsem, ki se pozimi odpravljajo v gore. 
Glavni namen aplikacije je pošiljanje in prikaz kontekstno odvisnih sporočil. 

## Kako deluje?
Aplikacija za zaznavanje konteksta uporabi lokacijo uporabnika ter trenutno vreme in plazozovni bilten.
Za pridobivanje podrobnosti o lokaciji uporabi [ArcGis API](https://developers.arcgis.com/rest/elevation/api-reference/summarize-elevation.htm),
vremensko napoved pridobi od [ARSO vreme](https://meteo.arso.gov.si/uploads/probase/www/fproduct/text/sl/forecast_si-upperAir-new_latest.xml) in plazovni bilten od 
[ARSO plazovi](https://vreme.arso.gov.si/api/1.0/avalanche_bulletin/).

## Pravila
Pravila opisujejo, kateri pogoji morajo veljati, da uporabniku aplikacija prikaže določeno opozorilo. Pravila so določena v mapi ```Assets``` v datoteki ```rules.json```.

## Uporabniški vmesnik
V aplikaciji so 3. glavni zasloni:
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
