#SQL-Analysen auf einer TopicExplorer Datenbank#
##Übersicht##
- [SQL Anfrage um Zusammenhänge zwischen Themen zu finden](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-um-zusammenh%C3%A4nge-zwischen-themen-zu-finden)
- [SQL Anfrage zum Auflisten der Wörter eines Themas](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-zum-auflisten-der-w%C3%B6rter-eines-themas)
- [SQL Anfrage zu Exportieren von einzelnen Dokumenten nach bestimmten Kriterien](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-zu-exportieren-von-einzelnen-dokumenten-nach-bestimmten-kriterien)
- [SQL Anfrage um Häufigkeiten eines Wortes in Dokumenten eines Thema auszuwählen](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-um-h%C3%A4ufigkeiten-eines-wortes-in-dokumenten-eines-thema-auszuw%C3%A4hlen)- [] ()

####SQL Anfrage um Zusammenhänge zwischen Themen zu finden####

```
select
t1.TOPIC_ID,
t2.TOPIC_ID,
sum(dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT +
    dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT) as s,
sum(dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT) as s_t1,
sum(dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT) as s_t2,
-- Minium der Summen der Topic-Token-Anzahlen
least( sum(dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT),
       sum(dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT)
     ) as s_min,
-- Summe der Minima der Topic-Token-Anzahlen
sum(
  least(
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
       )
) as s_min2,
-- Summe der Minima der Topic-Token-Anzahlen
-- geteilt durch die Anzahl der Dokumente
sum(
  least(
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
       )
) / 
count(distinct dt1.DOCUMENT_ID)
as s_min3,
count(distinct dt1.DOCUMENT_ID)
from
   DOCUMENT_TOPIC dt1
   join TOPIC t1 on (dt1.TOPIC_ID=t1.TOPIC_ID)
   join DOCUMENT_TOPIC dt2 on (dt1.DOCUMENT_ID=dt2.DOCUMENT_ID)
   join TOPIC t2 on (dt2.TOPIC_ID=t2.TOPIC_ID)
where
-- Hier koennen Sie das Ausgangsthema aendern
dt1.TOPIC_ID=142
-- alle Unterthemen von dt1 als Kandidaten fuer dt2 ausschliessen
and not
  (   t1.HIERARCHICAL_TOPIC$START<= t2.HIERARCHICAL_TOPIC$START
  and t2.HIERARCHICAL_TOPIC$END  <= t1.HIERARCHICAL_TOPIC$END
  )
and
-- alle Oberthemen von dt1 als Kandidaten fuer dt2 ausschliessen
not
  (   t2.HIERARCHICAL_TOPIC$START<= t1.HIERARCHICAL_TOPIC$START
  and t1.HIERARCHICAL_TOPIC$END  <= t2.HIERARCHICAL_TOPIC$END
  )
and t2.TOPIC_ID<100
group by
dt1.TOPIC_ID,
dt2.TOPIC_ID
-- Wechseln der Ordnung zwischen s_min und s_min2
order by s_min3 desc
limit 10
;
```
Beobachtung auf Basis der ersten 10 repräsentativen Dokumenten für Thema 74 (Familie, Haus, Tocher): Andere häufige Themen sind:
51 (Frauen, männlich, Männer) : 7 v. 10 Dokumenten 
57 (böse, Partner, gut) : 2 v. 10 Dokumenten
1 (Kinder, Kind) : 1 v. 10 (aber 11 v. 30 Dokumenten)
22 (Verständnis, Bewusstsein) : 0 v. 10 Dokumenten
84 (Ort, gehen): 0 v. 10 Dokumenten

Abfrage über sum_min1 für Thema 74: 22 (Verständnis, Bewusstsein), 84 (Ort, gehen), 64, 57 (böse, Partner), 4 (spaßig, mögen, gut)
Abfrage über sum_min2 für Thema 74: 4 (spaßig, mögen, gut), 57 (böse, Partner), 92, 1, 51
Abfrage über sum_min3 für Thema 74: 1 (Kinder, Kind), 57 (böse, Partner), 51 (Frauen, männlich), 4, 55

-> über die Abfrage sum_min3 entspricht die Themenreihenfolge am ehesten der Themenverteilung in den ersten 10 repräsentativen Dokumenten.

####SQL Anfrage zum Auflisten der Wörter eines Themas####
```
select *
from TERM_TOPIC join TERM using (TERM_ID)
-- Hier koennen Sie die topic Nummer ändern
where TOPIC_ID=22
order by NUMBER_OF_DOCUMENT_TOPIC desc
limit 10
;
```
####SQL Anfrage zu Exportieren von einzelnen Dokumenten nach bestimmten Kriterien####
z.B. durch Abfrage mit '名無し' [Anonym] lassen sich Dokumente mit Forendiskussionen oder Kommentaren finden 
```
select
DOCUMENT_ID,
LINK$URL,
TEXT$TITLE,
FULLTEXT$FULLTEXT
From
DOCUMENT
where
-- Hier koennen Sie die Dokument-IDs einsetzen.
DOCUMENT_ID in (139797,325345)
AND
-- Hiermit kann man Dokumente mit bestimmten Zeichenketten finden
-- auf Grundlage der mit MeCab zerlegten Token, die mit Leerzeichen getrennt sind
FULLTEXT$FULLTEXT LIKE '%名無し%'  
AND
-- auf Grundlage der Originaltexte
TEXT$FULLTEXT LIKE '%文部科学省%'  
;
```
####SQL Anfrage um Häufigkeiten eines Wortes in Dokumenten eines Thema auszuwählen####
auch wenn das Wort in den Dokumenten nicht dem Thema zugeordnet ist
```
select
d.DOCUMENT_ID
, count(*) as Token_Anzahl
, d.LINK$URL
, d.TEXT$TITLE
-- , d.FULLTEXT$FULLTEXT
From
DOCUMENT d
 -- Jiko Sekinin ist in zweii Token getrennt
 join DOCUMENT_TERM_TOPIC dtt_jiko on (d.DOCUMENT_ID = dtt_jiko.DOCUMENT_ID and dtt_jiko.TERM='自己')
 join DOCUMENT_TERM_TOPIC dtt_sekinin on (d.DOCUMENT_ID = dtt_sekinin.DOCUMENT_ID and dtt_sekinin.TERM='責任')
 join DOCUMENT_TOPIC dt on (dt.DOCUMENT_ID=d.DOCUMENT_ID)
where
dtt_jiko.POSITION_OF_TOKEN_IN_DOCUMENT+2=dtt_sekinin.POSITION_OF_TOKEN_IN_DOCUMENT
and dtt_jiko.TOPIC_ID=dtt_sekinin.TOPIC_ID
-- Thema zum Auswaehlen
and dt.TOPIC_ID=74
-- Häufigkeit des Themas im Dokument auswählen
and dt.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT>5
group by
d.DOCUMENT_ID,
d.LINK$URL,
d.TEXT$TITLE,
d.FULLTEXT$FULLTEXT
order by Token_Anzahl desc
limit 10
;
```
