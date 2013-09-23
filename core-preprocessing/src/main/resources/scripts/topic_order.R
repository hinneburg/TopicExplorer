args <- commandArgs(TRUE)
SimFileName = args[1]; #Dateiname mit Similarities Eingabe 
K = as.numeric(args[2]); #Anzahl Themen
TopicOrderFile = args[3]; #Ausgabedatei mit Topic Order

x=as.matrix(read.table(file=SimFileName,header=TRUE))

y=matrix(data=1,nrow=K,ncol=K);
for (i in 1:dim(x)[1]) {
 y[x[i,1]+1,x[i,2]+1]=x[i,3];
 y[x[i,2]+1,x[i,1]+1]=x[i,3];
}
d=as.dist(1-y);
hc <- hclust(d,"ward")
gr=cutree(hc,k=2:K)
pdf("etc/sim_hc.pdf")
plot(hc,cex=0.4)
dev.off()
#Farben von Rot bis Gelb, um '�hnlichkeit' von Themen am Anfang bzw. Ende der Hierarchie zu vermeiden
cols=rainbow(K, start=0.125, end=1)
outPut=data.frame()
for (i in 1:length(hc$order)) {
  outPut[i,1]=i
  outPut[i,2]=which(hc$order==i)
  outPut[i,3]=cols[which(hc$order==i)]
  outPut[i,4]=paste(gr[i,],collapse=";")
}
write.table(outPut,file=TopicOrderFile,sep=',',col.names = FALSE,row.names = FALSE)
quit();
