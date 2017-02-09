SQL Anfrage um Zusammenhänge zwischen Themen zu finden.
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
