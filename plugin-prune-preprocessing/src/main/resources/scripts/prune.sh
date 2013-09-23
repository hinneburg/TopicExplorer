#!/bin/bash
# Copyright Alexander Hinneburg,  Feb 21 2013, hinneburg@informatik.uni-halle.de
if [ -z "$1" ]; then 
              echo 'usage: '$0' <DataFile.csv> <LowerBound> <UpperBound>'
              exit
          fi
head -1 $1 > $1.pruned.Lower.$2.Upper.$3.csv
sed '1,1d' $1| cut -d';' -f 1,3 |sort |uniq |cut -d';' -f 2|sort|uniq --count|sort -k1,1 -n -r |awk -v 'L='$2 -v 'U='$3 '{if (L<$1 && $1<U) print $2}'>$1.pruned.Lower.$2.Upper.$3.vocabular
grep --fixed-strings --file=$1.pruned.Lower.$2.Upper.$3.vocabular $1 >>$1.pruned.Lower.$2.Upper.$3.csv

