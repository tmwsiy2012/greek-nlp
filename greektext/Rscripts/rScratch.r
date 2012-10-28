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

mlabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorRemoveOutliers.txt",what=character(),sep=",",nlines=1)
clabelsnoout <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorRemoveOutliers.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
compnoout <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixRemoveOutliers.txt", header=FALSE)
rownames(compnoout) <- mlabelsnoout
colnames(compnoout) <- clabelsnoout
compnoout.dist <- vegdist(compnoout)

clabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorOld.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
mlabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorOld.txt",what=character(),sep=",",nlines=1)
compold <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixOld.txt", header=FALSE)
rownames(compold) <- mlabelsOld
colnames(compold) <- clabelsOld
compold.dist <- vegdist(compold)

mlabels <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorFull.txt",what=character(),sep=",",nlines=1)
clabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorFull.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
compfull <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt", header=FALSE)
rownames(compfull) <- mlabelsFull
colnames(compfull) <- clabelsFull
compfull.dist <- vegdist(compfull)
compfull.pca <- rda(compfull)
compfull.mds <- metaMDS(compfull, trace=FALSE)
compfull.cca <- cca(compfull)

compcosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramCosineMatrixFull.txt", header=FALSE)
mlabelsCosine <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorFull.txt",what=character(),sep=",",nlines=1)

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