# SQL-Analysen auf einer TopicExplorer Datenbank #

## Übersicht ##
- [SQL Anfrage um Zusammenhänge zwischen Themen zu finden](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-um-zusammenh%C3%A4nge-zwischen-themen-zu-finden)
- [SQL Anfrage zum Finden der Dokumente, die einen Zusammenhang zwischen zwei Themen belegen](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-zum-finden-der-dokumente-die-einen-zusammenhang-zwischen-zwei-themen-belegen)
- [SQL Anfrage zum Auflisten der Wörter eines Themas](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-zum-auflisten-der-w%C3%B6rter-eines-themas)
- [SQL Anfrage für verschiedene Dokument-Rankings für ein gegebenes Topic](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-f%C3%BCr-verschiedene-dokument-rankings-f%C3%BCr-ein-gegebenes-topic)
- [SQL Anfrage für verschiedene Rankings der Blog-URLs für ein gegebenes Topic](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-f%C3%BCr-verschiedene-rankings-der-blog-urls-f%C3%BCr-ein-gegebenes-topic)
- [SQL Anfrage für Ranking der Blog-URLs für ein Corpus mit Wortfilter](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-f%C3%BCr-ranking-der-blog-urls-f%C3%BCr-ein-corpus-mit-wortfilter)
- [SQL Anfrage für die Dokumente eines Blogs für ein gegebenes Topic](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-f%C3%BCr-die-dokumente-eines-blogs-f%C3%BCr-ein-gegebenes-topic)
- [SQL Anfrage für Ranking der Blog-URLs mit Phrasen um ein Suchwort](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-f%C3%BCr-ranking-der-blog-urls-mit-phrasen-um-ein-suchwort)
- [SQL Anfrage zu Exportieren von einzelnen Dokumenten nach bestimmten Kriterien](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-zu-exportieren-von-einzelnen-dokumenten-nach-bestimmten-kriterien)
- [SQL Anfrage um Häufigkeiten eines Wortes in Dokumenten eines Thema auszuwählen](https://github.com/hinneburg/TopicExplorer/blob/master/sql-analytics.md#sql-anfrage-um-h%C3%A4ufigkeiten-eines-wortes-in-dokumenten-eines-thema-auszuw%C3%A4hlen)- [] ()

#### SQL Anfrage um Zusammenhänge zwischen Themen zu finden ####

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
   -- bitte Kommentar entfernen, falls mit Einschränkung auf Zeitraum gearbeitet wird
   -- join DOCUMENT d on (d.DOCUMENT_ID=dt1.DOCUMENT_ID)   
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
-- Kommentar entfernen, um mit Einschränkung auf Zeitraum zu arbeiten, beachte auch Kommentar unter From für Join von d enfernen
-- and d.TIME$TIME_STAMP between UNIX_TIMESTAMP('2015-01-10') and UNIX_TIMESTAMP('2015-01-18')
group by
dt1.TOPIC_ID,
dt2.TOPIC_ID
-- Wechseln der Ordnung zwischen s_min und s_min2
order by s_min3 desc
limit 10
;
```

Bei verschiedenen Versuchen hat s_min3 am ehesten anhand der repräsentativen Dokumente nachvollziehbare Ergebnisse geliefert:

Für Thema 6 (jikosekinin und Geiselnahmen) unterscheidet sich die Reihenfolge bei s_min, s_min2 und s_min3 kaum, aber s_min3 bringt Thema46 und Thema99 (beide ein Cluster für "Japan/"Japaner"/"Amerika"/"Krieg" mit politisch rechtslastigen Texten) am weitesten nach oben. 

--> Für die weitere Analyse sind die Dokumente nötig, die beide Themen möglichst ausgeprägt enthalten (also Thema6(jikosekinin/Geiselnahmen) und Thema 46(Japan/Amerika)!

#### SQL Anfrage zum Finden der Dokumente, die einen Zusammenhang zwischen zwei Themen belegen ####
```
select
    dt1.TOPIC_ID,
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
    dt2.TOPIC_ID,    
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
    least(
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
       )
    as Minimal_Number_of_Tokens,
    least(
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
       )
    /d.NUMBER_OF_TOKENS
    as Minimal_Percentage_of_Tokens,
    least(
       dt1.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT,
       dt2.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
       )
    /sqrt(d.NUMBER_OF_TOKENS)
    as Minimal_SQRT_Percentage_of_Tokens,
    dt1.DOCUMENT_ID,
    TEXT$TITLE,
    TITLE,
    LINK$URL
from
        DOCUMENT_TOPIC dt1
   join DOCUMENT_TOPIC dt2 on (dt1.DOCUMENT_ID=dt2.DOCUMENT_ID)
   join DOCUMENT d on (dt1.DOCUMENT_ID=d.DOCUMENT_ID)
where
-- Hier koennen Sie das Ausgangsthema und das Zielthema aendern
    dt1.TOPIC_ID=142
and dt2.TOPIC_ID=77
-- Kommentar entfernen, um mit Einschränkung auf Zeitraum zu arbeiten
-- and d.TIME$TIME_STAMP between UNIX_TIMESTAMP('2015-01-10') and UNIX_TIMESTAMP('2015-01-18')
order by 
   Minimal_Number_of_Tokens desc
--   Minimal_Percentage_of_Tokens desc
--    Minimal_SQRT_Percentage_of_Tokens desc
limit 10
;
```
-  Ranking nach Minimal_SQRT_Percentage_of_Tokens scheint besser als das Ranking nach Minimal_Number_of_Tokens zu sein, weil ein Thema dominiert
- In einem anderen Fall scheinen beide Rankings Minimal_SQRT_Percentage_of_Tokens und nach Minimal_Number_of_Tokens interessante Dokumente. 
- Vielleicht sollte die Differenz der Token-Anzahlen zusätzlich beachtet werden. Das wird beim Minimum bisher nicht gemacht.

#### SQL Anfrage zum Auflisten der Wörter eines Themas ####
```
select *
from TERM_TOPIC join TERM using (TERM_ID)
-- Hier koennen Sie die topic Nummer ändern
where TOPIC_ID=22
order by NUMBER_OF_DOCUMENT_TOPIC desc
limit 10
;
```
```
select
  TOPIC_ID,
  TERM_NAME,
  NUMBER_OF_TOKEN_TOPIC,
  NUMBER_OF_DOCUMENT_TOPIC
from
TERM_TOPIC join TERM using(TERM_ID) join TOPIC using (TOPIC_ID)
where 
  NUMBER_OF_TOKEN_TOPIC>100 and
  HIERARCHICAL_TOPIC$START=HIERARCHICAL_TOPIC$END  
order by
  HIERARCHICAL_TOPIC$START , 
  NUMBER_OF_TOKEN_TOPIC desc
;
```

#### SQL Anfrage für verschiedene Dokument-Rankings für ein gegebenes Topic ####
```
select
  DOCUMENT.DOCUMENT_ID
  , DOCUMENT_TOPIC.TOPIC_ID
  , PR_DOCUMENT_GIVEN_TOPIC
  , NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT
  , DOCUMENT.NUMBER_OF_TOKENS
  , NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT / DOCUMENT.NUMBER_OF_TOKENS as LinearilyNormalized
  , NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT / sqrt(DOCUMENT.NUMBER_OF_TOKENS) as NonlinearilyNormalized
-- Durch Entfernen der Kommentarzeichen weitere Attribute auswählen
  -- , SUBSTR(DOCUMENT.TEXT, 1, 150) AS KEYWORD_SNIPPET
  -- , DOCUMENT.TITLE AS KEYWORD_TITLE
  -- , CONCAT('[',DOCUMENT.BEST_TOPICS,']') AS TOP_TOPIC
   , TEXT$TITLE
-- Mit Textstellen für Suchworte, z.B. '自己責任'
  -- , CASE WHEN POSITION('自己責任' in TEXT$FULLTEXT)>0 THEN SUBSTR(TEXT$FULLTEXT FROM POSITION('自己責任' in TEXT$FULLTEXT)-30  FOR 60) ELSE 'nichts gefunden' END
  -- , POSITION('自己責任' in TEXT$FULLTEXT)
   , DOCUMENT.LINK$URL
   , orgTable_meta.DOCUMENT_DATE
from DOCUMENT 
  join DOCUMENT_TOPIC on (DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID)
  join TOPIC t on (DOCUMENT_TOPIC.TOPIC_ID=t.TOPIC_ID)
  join orgTable_meta on (orgTable_meta.DOCUMENT_ID=DOCUMENT.DOCUMENT_ID)
where
  -- Ausgangsthema
  t.TOPIC_ID=77
  -- Einschränkung auf Zeitraum
  -- and DOCUMENT.TIME$TIME_STAMP between UNIX_TIMESTAMP('2015-01-19') and UNIX_TIMESTAMP('2015-01-26')
ORDER BY
--  PR_DOCUMENT_GIVEN_TOPIC DESC
--  NonlinearilyNormalized DESC
--  LinearilyNormalized DESC
NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT DESC
-- Zeitliche Sortierung
-- DOCUMENT.TIME$TIME_STAMP asc
LIMIT 30
;
```

Setzt man unter ORDER BY das Kriterium NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT DESC ein und wählt es aus, ergibt sich dieselbe Reihenfolge wie bei den repräsentativen Dokumenten im TopicExplorer. Wählt man für die Tabelle dabei noch DOCUMENT.LINK$URL aus, lässt sich auf einen Blick erkennen, ob die repräsentativen Dokumente von demselben Blog stammen - für die Themenauswertung eine sehr wichtige Erkenntnis!
#### SQL Anfrage für verschiedene Rankings der Blog-URLs für ein gegebenes Topic ####
```
select
       SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         ) as BlogAuthor
  , DOCUMENT_TOPIC.TOPIC_ID
  , t.NUMBER_OF_TOKENS
  , SUM( NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT ) as NUMBER_OF_TOKEN_OF_BLOG_IN_TOPIC
  , SUM( NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT ) / t.NUMBER_OF_TOKENS as PERCENTAGE_OF_TOKEN_OF_BLOG_IN_TOPIC  
  , COUNT(*) as NUMBER_OF_DOCUMENT_OF_BLOG_IN_TOPIC
from DOCUMENT 
  join DOCUMENT_TOPIC on (DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID)
  join TOPIC t on (DOCUMENT_TOPIC.TOPIC_ID=t.TOPIC_ID)
where
  -- Ausgangsthema
  t.TOPIC_ID=32
GROUP BY
       SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         )
  , DOCUMENT_TOPIC.TOPIC_ID
  , t.NUMBER_OF_TOKENS
ORDER BY
 NUMBER_OF_TOKEN_OF_BLOG_IN_TOPIC DESC
-- NUMBER_OF_DOCUMENT_OF_BLOG_IN_TOPIC DESC
LIMIT 30
;
```
Blogs von dem Provider Nifty bauen sich anders auf und werden deshalb bei dieser Abfrage außen vor gelassen:
Die URLs von Nifty-Blogs setzen sich wie folgt zusammen:    http://XXXX-YYYY.cocolog-nifty.com/blog/JAHR 
Andere Blogs, z.B. von yahoo haben den Aufbau               http://blogs.yahoo.co.jp/XXXXYYYY/ZAHL.html
Der für diese Abfrage relevante Name des Blogs steht also bei Nifty direkt hinter dem http://. In der dritten Spalte, in der bei anderen Blog-Providern der Name des Blogs folgt, steht bei allen Nifty-Blogs einheitlich "blog" 

#### SQL Anfrage für Ranking der Blog-URLs für ein Corpus mit Wortfilter ####
Um einen TopicExplorer auf bestimmte Inhalte einzugrenzen, ist es oft notwendig nicht relevante Inhalte aus dem Corpus auszuschließen. Dafür müssen boolsche Suchwortfilter entwickelt werden. Ob der Filter gut funktioniert ist, läßt sich bei Blogs oft an der Blog-URL in Kombnination der Anzahl der ausgeschlossenen Dokumente sehen. 
```
select 
       SUBSTR(URL 
         FROM 1
         FOR locate("/",URL, locate("/",URL, 8) + 1 ) 
         ) as BlogAuthor,
      count(*) as DOCUMENT_NUMBER
from orgTable_meta join orgTable_text using (DOCUMENT_ID)
where 
  (DOCUMENT_TEXT LIKE '%自己責任%')
  AND
-- Wenn das nachfolge not auskommentiert ist, werden die ausgeschlossenen Blog angegeben, 
-- ansonsten die Blogs, die durch den Filter durch kommen
not 
  (
  (DOCUMENT_TEXT not LIKE '%自己責任の上で%')
  AND
  (DOCUMENT_TEXT not LIKE '%自己責任にてお願い%')
  AND
  (DOCUMENT_TEXT not LIKE '%自己責任でお願い%')
  AND
  (DOCUMENT_TEXT not LIKE '%自己責任でよろしく%')
  AND
  (DOCUMENT_TEXT not LIKE '%自己責任でください %')
  )
group by 
       SUBSTR(URL 
         FROM 1
         FOR locate("/",URL, locate("/",URL, 8) + 1 ) 
         ) 
order by 
   DOCUMENT_NUMBER desc
   limit 50
;
```

#### SQL Anfrage für Ranking der Blog-URLs mit Phrasen um ein Suchwort ####
```
select 
       SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         ) as BlogAuthor
      , SUBSTR(TEXT$FULLTEXT FROM POSITION('自己責任' in TEXT$FULLTEXT)-5  FOR 15)   
      , count(*) as DOCUMENT_NUMBER
from DOCUMENT
where 
-- bitte Kommentar entfernen um mit Einschränkung auf Zeitraum zu arbeiten
-- d.TIME$TIME_STAMP between UNIX_TIMESTAMP('2015-01-10') and UNIX_TIMESTAMP('2015-01-18') 
group by 
       SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         ) 
      , SUBSTR(TEXT$FULLTEXT FROM POSITION('自己責任' in TEXT$FULLTEXT)-5  FOR 15) 
order by 
   DOCUMENT_NUMBER desc
   limit 50
;
```

#### SQL Anfrage für die Dokumente eines Blogs für ein gegebenes Topic ####
```
select
   DOCUMENT.DOCUMENT_ID
   , TEXT$TITLE
   , DOCUMENT.LINK$URL
   , SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         ) as Blog
  , DOCUMENT_TOPIC.TOPIC_ID
  , t.NUMBER_OF_TOKENS
  , NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT 
  , NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT / t.NUMBER_OF_TOKENS as PERCENTAGE_OF_TOKEN_OF_DOCUMENT_IN_TOPIC  
from DOCUMENT 
  join DOCUMENT_TOPIC on (DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID)
  join TOPIC t on (DOCUMENT_TOPIC.TOPIC_ID=t.TOPIC_ID)
where
  -- Ausgangsthema
  t.TOPIC_ID=32
  -- Blog
  and 'http://blogs.yahoo.co.jp/lesson848/' = 
      SUBSTR(LINK$URL 
         FROM 1
         FOR locate("/",LINK$URL, locate("/",LINK$URL, 8) + 1 ) 
         )
ORDER BY
  NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT DESC
LIMIT 30
;
```
#### SQL Anfrage zu Exportieren von einzelnen Dokumenten nach bestimmten Kriterien ####
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
#### SQL Anfrage um Häufigkeiten eines Wortes in Dokumenten eines Thema auszuwählen  ####
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
