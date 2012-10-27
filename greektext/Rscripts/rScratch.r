train <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\trainingTest.txt"));
test <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\testingTest.txt"));
svm.model <- svm(train[,1025] ~ .,data=train,cost=100,gamma=1)
svm.pred <- predict(svm.model, test[,-1025])
output <- table(pred = svm.pred, true = test[,-1025])



data <- as.matrix(read.csv("c:\\users\\dunng\\workspace\\greektext\\output\\4charGramCosineMatrix.txt", header=FALSE))
out = kmeans(data,center=15,nstart=10000)
mlabels <- scan("c:\\users\\dunng\\workspace\\greektext\\output\\manuscriptLabels.txt",what=character(),sep=",",nlines=1)
hresult <- hclust(dist(data))
plot(hresult,labels=mlabels)
compositedata <- as.matrix(read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt"))

library(vegan)
CompositeGramIDFFeatureMatrixFullOld.txt

library(vegan)
mlabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\manuscriptLabels.txt",what=character(),sep=",",nlines=1)
clabelsFull <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorFull.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
compfull <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFull.txt", header=FALSE)
rownames(compfull) <- mlabelsFull
colnames(compfull) <- clabelsFull
compfull.dist <- vegdist(compfull)

clabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramFeatureVectorFullOld.txt",what=character(),sep=",",nlines=1,encoding="UTF-8")
mlabelsOld <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\oldmanuscriptLabels.txt",what=character(),sep=",",nlines=1)
compold <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramIDFFeatureMatrixFullOld.txt", header=FALSE)
rownames(compold) <- mlabelsOld
colnames(compold) <- clabelsOld
compold.dist <- vegdist(compold)

compcosine <- read.csv("c:\\users\\tmwsiy\\workspace\\greektext\\output\\CompositeGramCosineMatrixFull.txt", header=FALSE)
mlabelsCosine <- scan("c:\\users\\tmwsiy\\workspace\\greektext\\output\\compositeGramManuscriptNameVectorFull.txt",what=character(),sep=",",nlines=1)

compold.mds0 <- monoMDS(compold.dist)
compfull.mds0 <- monoMDS(compfull.dist)
compold.mds <- metaMDS(compold, trace=FALSE)
compfull.mds <- metaMDS(compfull, trace=FALSE)
compold.pca <- rda(compold)
compfull.pca <- rda(compfull)
compold.cca <- cca(compold)
compfull.cca <- cca(compfull)
save.image(file="c:\\users\\tmwsiy\\workspace\\greektext\\output\\dataBlob.RData")

stressplot(data.mds0, data.dis)
ordiplot(data.mds0, type="t")