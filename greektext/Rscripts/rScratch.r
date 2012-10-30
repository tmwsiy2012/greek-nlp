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
CompositeGramIDFFeatureMatrixFullOld.txt

library(vegan)
mlabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
no_out <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\removeOutliersIDFFeatureMatrix.txt", header=FALSE)
rownames(no_out) <- mlabelsnoout
colnames(no_out) <- clabelsnoout
no_out.dist <- vegdist(no_out)
no_out.mds0 <- monoMDS(no_out.dist)

no_out.mds <- metaMDS(no_out, trace=FALSE)

library(vegan)
mlabelsonlyout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsonlyout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
only_out <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\onlyOutliersIDFFeatureMatrix.txt", header=FALSE)
rownames(only_out) <- mlabelsonlyout
colnames(only_out) <- clabelsonlyout
only_out.dist <- vegdist(only_out)
only_out.mds0 <- monoMDS(only_out.dist)

only_out.mds <- metaMDS(only_out, trace=FALSE)


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

old.mds <- metaMDS(old, trace=FALSE)

mlabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
full <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetIDFFeatureMatrix.txt", header=FALSE)
full.dist <- vegdist(full)
full.mds0 <- monoMDS(full)

fullcosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\fullSetCosineMatrix.txt", header=FALSE)
rownames(full) <- mlabelsFull
colnames(full) <- clabelsFull

full.mds <- metaMDS(full, trace=FALSE)

mlabelsAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
clabelsAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfFeatureVector.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
alike_half <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfIDFFeatureMatrix.txt", header=FALSE)
alike_half.cosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfCosineMatrix.txt", header=FALSE)
mlabelsCosineAlike <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\mostCorrolatedHalfManuscriptNameVector.txt",what=character(),sep=",",nlines=1)
alike_half.cosine.matrix <- cor(as.matrix(alike_half.cosine))
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
diff_half.cosine.matrix <- cor(as.matrix(diff_half.cosine))
rownames(diff_half.cosine.matrix) <- mlabelsDiff
colnames(diff_half.cosine.matrix) <- mlabelsDiff
rownames(diff_half) <- mlabelsDiff
colnames(diff_half) <- mlabelsDiff
diff_half.dist <- vegdist(diff_half)
diff_half.mds0 <- monoMDS(diff_half.dist)

// long
diff_half.mds <- metaMDS(alike_half, trace=FALSE)
alike_half.mds <- metaMDS(alike_half, trace=FALSE)


mostCorrolatedHalf

alike_half

save.image("C:\\Users\\tmwsiy\\Documents\\AllData.RData")


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