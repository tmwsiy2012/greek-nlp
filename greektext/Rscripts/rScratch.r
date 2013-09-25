train <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\trainingTest.txt"));
test <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\testingTest.txt"));
svm.model <- svm(train[,1025] ~ .,data=train,cost=100,gamma=1)
svm.pred <- predict(svm.model, test[,-1025])
output <- table(pred = svm.pred, true = test[,-1025])



data <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\4charGramCosineMatrix.txt", header=FALSE))
out = kmeans(data,center=15,nstart=10000)
mlabels <- scan("c:\\users\\dunng\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorFull.txt",what=character(),sep=",",nlines=1)
hresult <- hclust(dist(data))
plot(hresult,labels=mlabels)
compositedata <- as.matrix(read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt"))

library(vegan)


library(vegan)
mlabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
no_out <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersIDFFeatureMatrix.txt", header=FALSE)
rownames(no_out) <- mlabelsnoout
colnames(no_out) <- clabelsnoout
no_out.dist <- vegdist(no_out)
no_out.mds0 <- monoMDS(no_out.dist)



library(vegan)
mlabelsonlyout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsonlyout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
only_out <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersIDFFeatureMatrix.txt", header=FALSE)
rownames(only_out) <- mlabelsonlyout
colnames(only_out) <- clabelsonlyout
only_out.dist <- vegdist(only_out)
only_out.mds0 <- monoMDS(only_out.dist)



library(vegan)
clabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
mlabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
old <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldIDFFeatureMatrix.txt", header=FALSE)
oldcosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldCosineMatrix.txt", header=FALSE)
rownames(old) <- mlabelsOld
colnames(old) <- clabelsOld
old.dist <- vegdist(old)
old.mds0 <- monoMDS(old.dist)

ordiplot(data.mds0, type="t")



mlabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\reallyOldManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\reallyOldFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
reallyOld <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\reallyOldIDFFeatureMatrix.txt", header=FALSE)
rownames(full) <- mlabelsFull
colnames(full) <- clabelsFull
reallyOld.dist <- vegdist(reallyOld)
reallyOld.deco <- decorana(reallyOld.dist)






mlabelsAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
alike_half <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfIDFFeatureMatrix.txt", header=FALSE)
alike_half.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfCosineMatrix.txt", header=FALSE)
mlabelsCosineAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
alike_half.cosine.matrix <- as.matrix(alike_half.cosine)
rownames(alike_half.cosine.matrix) <- mlabelsCosineAlike
colnames(alike_half.cosine.matrix) <- mlabelsCosineAlike
rownames(alike_half) <- mlabelsAlike
colnames(alike_half) <- clabelsAlike
alike_half.dist <- vegdist(alike_half)
alike_half.mds0 <- monoMDS(alike_half.dist)

mlabelsDiff <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\leastCorrolatedHalfManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsDiff <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\leastCorrolatedHalfFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
diff_half <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\leastCorrolatedHalfIDFFeatureMatrix.txt", header=FALSE)
diff_half.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\leastCorrolatedHalfCosineMatrix.txt", header=FALSE)
diff_half.cosine.matrix <- as.matrix(diff_half.cosine)
rownames(diff_half.cosine.matrix) <- mlabelsDiff
colnames(diff_half.cosine.matrix) <- mlabelsDiff
rownames(diff_half) <- mlabelsDiff
colnames(diff_half) <- clabelsDiff
diff_half.dist <- vegdist(diff_half)
diff_half.mds0 <- monoMDS(diff_half.dist)

save.image("C:\\Users\\tmwsiy\\Documents\\AllData.RData")

// long
diff_half.mds <- metaMDS(alike_half, trace=FALSE)
full.mds <- metaMDS(full, trace=FALSE)
old.mds <- metaMDS(old, trace=FALSE)
only_out.mds <- metaMDS(only_out, trace=FALSE)
no_out.mds <- metaMDS(no_out, trace=FALSE)
alike_half.mds <- <- metaMDS(alike_half, trace=FALSE)







compfull.pca <- rda(compfull)

compfull.cca <- cca(compfull)

compcosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramCosineMatrixFull.txt", header=FALSE)
mlabelsCosine <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorFull.txt",what=character(),sep=",",nlines=1)
rownames(compcosine) <- mlabelsCosine
colnames(compcosine) <- mlabelsCosine



old.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldCosineMatrix.txt", header=FALSE)
mlabelsCosineOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
old.cosine.matrix <- cor(as.matrix(old.cosine))
rownames(old.cosine.matrix) <- mlabelsCosineOld
colnames(old.cosine.matrix) <- mlabelsCosineOld



no_out.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersCosineMatrix.txt", header=FALSE)
mlabelsCosineremoveOutliers <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
no_out.cosine.matrix <- cor(as.matrix(no_out.cosine))
rownames(no_out.cosine.matrix) <- mlabelsCosineremoveOutliers
colnames(no_out.cosine.matrix) <- mlabelsCosineremoveOutliers

only_out.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersCosineMatrix.txt", header=FALSE)
mlabelsCosineonlyOutliers <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
only_out.cosine.matrix <- cor(as.matrix(only_out.cosine))
rownames(only_out.cosine.matrix) <- mlabelsCosineonlyOutliers
colnames(only_out.cosine.matrix) <- mlabelsCosineonlyOutliers

full.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetCosineMatrix.txt", header=FALSE)
mlabelsCosinefull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
full.cosine.matrix <- cor(as.matrix(full.cosine))
rownames(full.cosine.matrix) <- mlabelsCosineremovefull
colnames(full.cosine.matrix) <- mlabelsCosineremovefull


compcosineold <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramCosineMatrixOld.txt", header=FALSE)
mlabelsCosineOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorOld.txt",what=character(),sep=",",nlines=1)
rownames(compcosineold) <- mlabelsCosineOld
colnames(compcosineold) <- mlabelsCosineOld
compcosineold.matrix <- as.matrix(compcosineold)

compold.mds0 <- monoMDS(compold.dist)
compfull.mds0 <- monoMDS(compfull.dist)

compold.mds <- metaMDS(compold, trace=FALSE)

compnoout.mds <- metaMDS(compnoout, trace=FALSE)
compold.pca <- rda(compold)
compnoout.pca <- rda(compnoout)

compold.cca <- cca(compold)

save.image(file="c:\\users\\tmwsiy\\workspace\\greektext\\output\\dataBlob.RData")

stressplot(data.mds0, data.dis)
ordiplot(data.mds0, type="t")

    source("http://bioconductor.org/biocLite.R")
    biocLite("genefilter")
	
	corrplot(no_out.matrix.scaled, order="hclust",tl.cex=.4)
	
	cor(t(df), use="pairwise.complete.obs")
	
	cor(t(full), use="pairwise.complete.obs")
	
old.gc <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldGlobalCounts.txt", header=FALSE,encoding="UTF-8")	
colnames(old.gc) <- c('gram','count')
old.idfgc <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOldGlobalIDFCounts.txt", header=FALSE,encoding="UTF-8")
colnames(old.idfgc) <- c('gram','count')

fullSet.dist <- vegdistfullSet)
fullSet.mds0 <- monoMDS(fullSet.dist)
fullSet.mds <- metaMDS(fullSet.dist)
fullSet.cca <- cca(fullSet.dist)


ordiplot(reallyOld.deco, display="si", type="t",main="DCA Really Old Set")

ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set")
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group A",xlim=c(-0.03,0.005),ylim=c(-0.005,0.03))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group B",xlim=c(-0.03,0.05),ylim=c(-0.045,-0.03))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group C",xlim=c(-0.001,0.034),ylim=c(0.01,0.045))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group D",xlim=c(-0.1,-0.03),ylim=c(0.01,0.03))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group E",xlim=c(0.04,0.08),ylim=c(0.035,0.075))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group F",xlim=c(0.02,0.06),ylim=c(-0.105,-0.08))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group G",xlim=c(0.082,0.125),ylim=c(0.01,0.045))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group H",xlim=c(-0.01,0.01),ylim=c(-0.13,-0.1))
ordiplot(fullSet.deco, display="si", type="t",main="DCA Full Set Group I",xlim=c(-0.22,-0.17),ylim=c(-0.01,0.01))

corrplot(fullSet.cosine, order="hclust", tl.cex=.3)
